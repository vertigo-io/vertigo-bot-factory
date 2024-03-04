package io.vertigo.chatbot.liquibase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.influxdb.client.TasksQuery;
import com.influxdb.client.domain.Task;
import com.influxdb.client.domain.TaskStatusType;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.util.StringUtil;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

public class InfluxDbFileTask extends InfluxDbMigration {

	private static final Logger LOGGER = LogManager.getLogger(InfluxDbFileTask.class);

	private String name;
	private String file;
	private String every;
	private String cron;
	private String offset;

	@Override
	public void execute(final Database database) throws CustomChangeException {
		Assertion.check()
				.isNotBlank(name)
				.isTrue(StringUtil.isBlank(every) != StringUtil.isBlank(cron), "Must define either every or cron parameter.");

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

		Assertion.check().isTrue(request.contains("#TASK_DEF#"), "Task definition must contains \"#TASK_DEF#\" placeholder after imports.");

		request = request
				.replace("#TASK_DEF#", getTaskDef())
				.replace("#FROM#", "from(bucket:\"" + influxDbName + "\")")
				.replace("#TO#", "|> to(bucket:\"" + influxDbName + "\")");

		// create or update task
		final var oldTask = findTask(name);
		final Task task = oldTask.orElseGet(this::newTask);
		task.setStatus(TaskStatusType.ACTIVE);
		task.setEvery(every);
		task.setCron(cron);
		task.setOffset(offset);
		task.setFlux(request);

		if (oldTask.isPresent()) {
			LOGGER.info("Updating existing task '{}'", name);
			influxDBClient.getTasksApi().updateTask(task);
		} else {
			LOGGER.info("Creating new task '{}'", name);
			influxDBClient.getTasksApi().createTask(task);
		}
	}

	private String getTaskDef() {
		final var sb = new StringBuilder("option task = {name: \"")
				.append(name)
				.append("\"");

		if (!StringUtil.isBlank(every)) {
			sb.append(", every: ")
					.append(every);
		}
		if (!StringUtil.isBlank(cron)) {
			sb.append(", cron: \"")
					.append(cron)
					.append("\"");
		}
		if (!StringUtil.isBlank(offset)) {
			sb.append(", offset: ")
					.append(offset);
		}

		sb.append("}");

		return sb.toString();
	}

	private Task newTask() {
		final var task = new Task();
		task.setOrg(influxDbOrg);
		task.setName(name);
		return task;
	}

	private Optional<Task> findTask(final String name) {
		final TasksQuery tq = new TasksQuery();
		tq.setName(name);
		final var tasks = influxDBClient.getTasksApi().findTasks(tq);
		Assertion.check().isFalse(tasks.size() > 1, "Multiple tasks with name '{0}' already exists.", name);

		if (tasks.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(tasks.get(0));
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
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
	 * @return the every
	 */
	public String getEvery() {
		return every;
	}

	/**
	 * @param every the every to set
	 */
	public void setEvery(final String every) {
		this.every = every;
	}

	/**
	 * @return the cron
	 */
	public String getCron() {
		return cron;
	}

	/**
	 * @param cron the cron to set
	 */
	public void setCron(final String cron) {
		this.cron = cron;
	}

	/**
	 * @return the offset
	 */
	public String getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(final String offset) {
		this.offset = offset;
	}

}
