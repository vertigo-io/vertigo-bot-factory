package io.vertigo.chatbot.liquibase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Period;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.util.StringUtil;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

public class InfluxDbFileMigration extends InfluxDbMigration {

	private static final Logger LOGGER = LogManager.getLogger(InfluxDbFileMigration.class);

	private String file;
	private String replaceMeasurement;

	@Override
	public void execute(final Database database) throws CustomChangeException {
		String request;
		try {
			final var stream = resourceAccessor.openStream(null, file);
			if (stream == null) {
				throw new VSystemException("Unable to find file '{0}' for migration.", file);
			}
			request = IOUtils.toString(stream, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			throw new VSystemException(e, "Unable to read file '{0}' for migration.", file);
		}

		final boolean toTmpBucket = !StringUtil.isBlank(replaceMeasurement);
		final String destBucket = toTmpBucket ? influxDbName + "/tmp" : influxDbName;

		if (toTmpBucket) {
			recreateBucket(destBucket);
		}

		request = request
				.replace("#FROM#", "from(bucket:\"" + influxDbName + "\")")
				.replace("#TO#", "|> to(bucket:\"" + destBucket + "\")");

		LOGGER.info("Compute new data.");
		executeRequestChunked(request, Period.ofDays(1));

		if (toTmpBucket) {
			replaceWithDelete(destBucket);
		}
	}

	private void replaceWithDelete(final String destBucket) {
		LOGGER.info("Deleting old measurements.");

		for (final String measurement : replaceMeasurement.split(";")) {
			executeChunked(
					(from, to) -> {
						influxDBClient.getDeleteApi().delete(from, to, "_measurement=\"" + measurement + "\"", influxDbName, influxDbOrg);
					},
					Period.ofDays(1));
		}

		LOGGER.info("Persist new data.");
		final var copyRequest = "from(bucket:\"" + destBucket + "\")\n" +
				"#RANGE#\n" +
				"|> to(bucket:\"" + influxDbName + "\")";
		executeRequestChunked(copyRequest, Period.ofDays(1));

		LOGGER.info("Finalizing.");
		deleteBucket(destBucket);
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(final String file) {
		this.file = file;
	}

	/**
	 * @return the replaceMeasurement
	 */
	public String getReplaceMeasurement() {
		return replaceMeasurement;
	}

	/**
	 * @param replaceMeasurement the replaceMeasurement to set
	 */
	public void setReplaceMeasurement(final String replaceMeasurement) {
		this.replaceMeasurement = replaceMeasurement;
	}

}
