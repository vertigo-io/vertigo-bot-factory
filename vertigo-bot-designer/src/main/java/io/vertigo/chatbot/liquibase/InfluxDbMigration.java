package io.vertigo.chatbot.liquibase;

import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.OrganizationsQuery;

import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.node.Node;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import okhttp3.OkHttpClient;

public abstract class InfluxDbMigration implements CustomTaskChange {

	private static final Logger LOGGER = LogManager.getLogger(InfluxDbMigration.class);

	private static final int INFLUX_READ_TIMEOUT_MINUTE = 20;

	protected ParamManager paramManager;

	protected String influxDbUrl;
	protected String influxDbToken;
	protected String influxDbOrg;
	protected String influxDbName;

	protected InfluxDBClient influxDBClient;

	protected ResourceAccessor resourceAccessor;

	@Override
	public void setUp() throws SetupException {
		paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);

		influxDbUrl = paramManager.getParam("INFLUXDB_URL").getValueAsString();
		influxDbToken = paramManager.getParam("INFLUXDB_TOKEN").getValueAsString();
		influxDbOrg = paramManager.getOptionalParam("INFLUXDB_ORG").map(Param::getValueAsString).orElse("chatbot");
		influxDbName = paramManager.getParam("boot.ANALYTICA_DBNAME").getValueAsString();

		final OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(INFLUX_READ_TIMEOUT_MINUTE, TimeUnit.MINUTES);

		final InfluxDBClientOptions options = InfluxDBClientOptions.builder()
				.url(influxDbUrl)
				.authenticateToken(influxDbToken.toCharArray())
				.org(influxDbOrg)
				.okHttpClient(builder)
				.build();

		influxDBClient = InfluxDBClientFactory.create(options);
	}

	// --

	@Override
	public String getConfirmationMessage() {
		return "InfluxDb updated successfully.";
	}

	@Override
	public void setFileOpener(final ResourceAccessor resourceAccessor) {
		this.resourceAccessor = resourceAccessor;
	}

	@Override
	public ValidationErrors validate(final Database database) {
		return null;
	}

	/**
	 * Delete and recreate the bucket. Old data is lost.
	 *
	 * @param bucketName name of the bucket
	 */
	protected final void recreateBucket(final String bucketName) {
		deleteBucket(bucketName);

		final var orgQ = new OrganizationsQuery();
		orgQ.setOrg(influxDbOrg);
		final var org = influxDBClient.getOrganizationsApi().findOrganizations(orgQ).get(0);

		influxDBClient.getBucketsApi().createBucket(bucketName, org.getId());
	}

	/**
	 * Delete the bucket. Data is lost.
	 *
	 * @param bucketName name of the bucket
	 */
	protected void deleteBucket(final String bucketName) {
		final var oldBucket = influxDBClient.getBucketsApi().findBucketByName(bucketName);
		if (oldBucket != null) {
			influxDBClient.getBucketsApi().deleteBucket(oldBucket);
		}
	}

	/**
	 * Replace old bucket by new bucket. Data on old bucket is lost.
	 *
	 * @param old name of the bucket to replace
	 * @param by name of the new bucket
	 */
	protected void replaceBucket(final String old, final String by) {
		deleteBucket(old);

		final var newBucket = influxDBClient.getBucketsApi().findBucketByName(by);
		newBucket.setName(old);
		influxDBClient.getBucketsApi().updateBucket(newBucket);
	}

	/**
	 * Execute request with 1 day chunks. range must be written with the placeholder #RANGE# and will be replaced automatically.
	 * This chunk execution permit less memory and network problem than executing on all data at once.
	 *
	 * @param request flux request. range must be a placeholder (see desc)
	 */
	protected void executeRequestChunked(final String request) {
		executeRequestChunked(request, Period.ofDays(1));
	}

	/**
	 * Execute request by increment chunks. range must be written with the placeholder #RANGE# and will be replaced automatically.
	 * This chunk execution permit less memory and network problem than executing on all data at once.
	 *
	 * @param request flux request. range must be a placeholder (see desc)
	 * @param increment chunk size
	 */
	protected void executeRequestChunked(final String request, final TemporalAmount increment) {
		executeChunked(
				(from, to) -> {
					final String range = "|> range(start: " + formatDate(from) + ", stop: " + formatDate(to) + ")";
					final String executableRequest = request.replace("#RANGE#", range);

					executeQuery(executableRequest);
				},
				increment);
	}

	protected void executeChunked(final BiConsumer<OffsetDateTime, OffsetDateTime> job, final TemporalAmount increment) {
		OffsetDateTime from = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC); // chatbot factory doesn't exists before this point
		OffsetDateTime to = from.plus(increment);
		final OffsetDateTime end = OffsetDateTime.now();

		while (from.isBefore(end)) {
			LOGGER.info("Executing influx request from '{}' to '{}'.", from, to);

			job.accept(from, to);

			from = to;
			to = from.plus(increment);
		}
	}

	/**
	 * Execute query without doing nothing with the result.
	 * Only asynchronous query permit dropping response without messing up with java memory.
	 * This method transform asynchronous behavior in a synchronous one.
	 *
	 * @param q query yo execute
	 */
	protected void executeQuery(final String q) {
		final var cl = new CountDownLatch(1);
		final List<Throwable> exeptions = new ArrayList<>();

		influxDBClient.getQueryApi().queryRaw(q,
				(c, s) -> {
					// drop response data
				},
				e -> {
					exeptions.add(e);
					cl.countDown();
				},
				() -> {
					cl.countDown();
				});

		try {
			cl.await(INFLUX_READ_TIMEOUT_MINUTE + 1, TimeUnit.MINUTES); // wait longer than the read timeout of the request
		} catch (final InterruptedException e1) {
			// nothing
		}

		if (!exeptions.isEmpty()) {
			throw WrappedException.wrap(exeptions.get(0));
		}
	}

	public static String formatDate(final OffsetDateTime odt) {
		return odt.format(DateTimeFormatter.ISO_DATE_TIME);
	}

}
