package io.vertigo.chatbot.analytics.rasa.rabbitmq;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import io.vertigo.chatbot.analytics.rasa.model.AbstractRasaTrackerEvent;
import io.vertigo.chatbot.analytics.rasa.model.RasaTrackerActionEvent;
import io.vertigo.chatbot.analytics.rasa.model.RasaTrackerBotEvent;
import io.vertigo.chatbot.analytics.rasa.model.RasaTrackerRewindEvent;
import io.vertigo.chatbot.analytics.rasa.model.RasaTrackerUserEvent;
import io.vertigo.chatbot.analytics.rasa.model.nested.RasaTrackerIntent;
import io.vertigo.chatbot.analytics.rasa.util.GsonOptionalTypeAdapter;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.commons.analytics.AnalyticsManager;
import io.vertigo.commons.analytics.process.AProcess;
import io.vertigo.commons.analytics.process.AProcessBuilder;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.lang.VSystemException;

public class RasaRabbitMqConsumer implements Component, Activeable {

	protected static final Logger LOGGER = LogManager.getLogger("rasa");

	private Connection conn;
	private Channel channel;
	private Gson gson;

	@Inject
	private AnalyticsManager analyticsManager;

	@Inject
	private ParamManager paramManager;

	@Inject
	private ExecutorConfigManager executorConfigManager;

	private String queueName;
	private String consumerTag;

	@Override
	public void start() {
		queueName = paramManager.getParam("rabbitMq.queueName").getValueAsString();
		consumerTag = paramManager.getParam("rabbitMq.consumerTag").getValueAsString();

		startGsonEngine();

		startRabbitMqConsumer();
	}

	private void startGsonEngine() {
		final RuntimeTypeAdapterFactory<AbstractRasaTrackerEvent> rttaf = RuntimeTypeAdapterFactory.of(AbstractRasaTrackerEvent.class, "event")
				.registerSubtype(RasaTrackerUserEvent.class, "user")
				.registerSubtype(RasaTrackerActionEvent.class, "action")
				.registerSubtype(RasaTrackerBotEvent.class, "bot")
				.registerSubtype(RasaTrackerRewindEvent.class, "rewind");

		gson = new GsonBuilder()
				.registerTypeAdapterFactory(rttaf)
				.registerTypeAdapterFactory(GsonOptionalTypeAdapter.FACTORY)
				.create();
	}

	private void startRabbitMqConsumer() {
		final ConnectionFactory factory = new ConnectionFactory();
		// "guest"/"guest" by default, limited to localhost connections
		// factory.setUsername(userName);
		// factory.setPassword(password);
		// factory.setVirtualHost(virtualHost);
		// factory.setHost(hostName);
		// factory.setPort(portNumber);

		try {
			conn = factory.newConnection();
			channel = conn.createChannel();

			channel.queueDeclare(queueName, true, false, false, null);

			channel.basicConsume(queueName, false, consumerTag, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(final String consumerTag, final Envelope envelope,
						final AMQP.BasicProperties properties, final byte[] body) throws IOException {

					//					final String routingKey = envelope.getRoutingKey(); // rasa
					//					final String contentType = properties.getContentType(); // null
					final long deliveryTag = envelope.getDeliveryTag();

					processMessage(new String(body, StandardCharsets.UTF_8));

					channel.basicAck(deliveryTag, false);
				}
			});

		} catch (IOException | TimeoutException e) {
			throw new VSystemException(e, "Error while connecting to RabbitMQ");
		}
	}

	private void processMessage(final String message) {
		LOGGER.debug("Received message from RabbitMQ : {}", message);

		final AbstractRasaTrackerEvent action = gson.fromJson(message, AbstractRasaTrackerEvent.class);

		if (action instanceof RasaTrackerUserEvent) {
			final RasaTrackerUserEvent userAction = (RasaTrackerUserEvent) action;

			final String userText = userAction.getText();
			final RasaTrackerIntent intent = userAction.getParseData().getIntent();

			RasaTypeAction rta;
			if (userText.startsWith("/start")) {
				rta = RasaTypeAction.OPEN;
			} else if (userText.startsWith("/reponse")) {
				rta = RasaTypeAction.RESPONSE_INFO;
			} else if (userText.startsWith("/eval")) {
				rta = RasaTypeAction.RATING;
			} else if (userText.startsWith("/")) {
				rta = RasaTypeAction.BUTTON;
			} else {
				rta = RasaTypeAction.MESSAGE;
			}

			final ExecutorConfiguration executorConfiguration = executorConfigManager.getExecutorConfiguration();

			final AProcessBuilder processBuilder = AProcess.builder("chatbot", intent.getName().orElse("unknown"), userAction.getTimestamp(), userAction.getTimestamp()) // timestamp of emitted event
					.addTag("text", userAction.getText())
					.addTag("type", rta.name())
					.addTag("sessionId", userAction.getSenderId())
					.addTag("messageId", userAction.getMessageId())
					.addTag("botId", executorConfiguration.getBotId().toString())
					.addTag("nodId", executorConfiguration.getNodId().toString())
					.setMeasure("confidence", intent.getConfidence().doubleValue())
					.setMeasure("isFallback", intent.getConfidence().compareTo(BigDecimal.valueOf(0.5)) < 0 ? 1d : 0d)
					.setMeasure("isTypeOpen", rta == RasaTypeAction.OPEN ? 1d : 0d)
					.setMeasure("isTypeMessage", rta == RasaTypeAction.MESSAGE ? 1d : 0d)
					.setMeasure("isTypeResponseInfo", rta == RasaTypeAction.RESPONSE_INFO ? 1d : 0d)
					.setMeasure("isTypeButton", rta == RasaTypeAction.BUTTON ? 1d : 0d)
					.setMeasure("isTypeRating", rta == RasaTypeAction.RATING ? 1d : 0d);

			analyticsManager.addProcess(processBuilder.build());
		}
	}

	private enum RasaTypeAction {
		OPEN,
		MESSAGE,
		RESPONSE_INFO,
		BUTTON,
		RATING,
	}

	@Override
	public void stop() {
		Exception exception = null;
		try {
			if (channel != null) {
				channel.close();
			}
		} catch (IOException | TimeoutException e) {
			exception = e;
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (final IOException e) {
			if (exception != null) {
				e.addSuppressed(exception);
			}
			exception = e;
		}
		if (exception != null) {
			LOGGER.info("Erreur while closing RabbitMQ connexion", exception);
		}
	}

}
