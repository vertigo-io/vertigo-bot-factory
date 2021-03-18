/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.analytics.rasa.rabbitmq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

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
import io.vertigo.chatbot.analytics.rasa.model.RasaTrackerRestartEvent;
import io.vertigo.chatbot.analytics.rasa.model.RasaTrackerRewindEvent;
import io.vertigo.chatbot.analytics.rasa.model.RasaTrackerUserEvent;
import io.vertigo.chatbot.analytics.rasa.model.nested.RasaTrackerIntent;
import io.vertigo.chatbot.analytics.rasa.util.GsonOptionalTypeAdapter;
import io.vertigo.chatbot.commons.RasaTypeAction;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.core.analytics.AnalyticsManager;
import io.vertigo.core.analytics.process.AProcess;
import io.vertigo.core.analytics.process.AProcessBuilder;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;

@Deprecated
public class RasaRabbitMqConsumer implements Component, Activeable {

	protected static final Logger LOGGER = LogManager.getLogger("rasa");

	private Connection connection;
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

		// startRabbitMqConsumer();
	}

	private void startGsonEngine() {
		final RuntimeTypeAdapterFactory<AbstractRasaTrackerEvent> rttaf = RuntimeTypeAdapterFactory.of(AbstractRasaTrackerEvent.class, "event")
				.registerSubtype(RasaTrackerUserEvent.class, "user")
				.registerSubtype(RasaTrackerActionEvent.class, "action")
				.registerSubtype(RasaTrackerBotEvent.class, "bot")
				.registerSubtype(RasaTrackerRewindEvent.class, "rewind")
				.registerSubtype(RasaTrackerRestartEvent.class, "restart");

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
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.queueDeclare(queueName, true, false, false, null);

			channel.basicConsume(queueName, false, consumerTag, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(final String myConsumerTag, final Envelope envelope,
						final AMQP.BasicProperties properties, final byte[] body) throws IOException {

					//					final String routingKey = envelope.getRoutingKey(); // rasa
					//					final String contentType = properties.getContentType(); // null
					final long deliveryTag = envelope.getDeliveryTag();

					try {
						processMessage(new String(body, StandardCharsets.UTF_8));

						channel.basicAck(deliveryTag, false);
					} catch (final Exception e) {
						LOGGER.error(new ParameterizedMessage("Impossible de traiter le message rabbitMq : {}", new String(body, StandardCharsets.UTF_8)), e);
					}
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

			final RasaTypeAction rta;
			if (userText.startsWith("/start")) {
				rta = RasaTypeAction.OPEN;
			} else if (userText.startsWith("/restart")) {
				rta = RasaTypeAction.RESTART;
			} else if (userText.startsWith("/reponse")) {
				rta = RasaTypeAction.RESPONSE_INFO;
			} else if (userText.startsWith("/eval")) {
				rta = RasaTypeAction.RATING;
			} else if (userText.startsWith("/")) {
				rta = RasaTypeAction.BUTTON;
			} else {
				rta = RasaTypeAction.MESSAGE;
			}

			final ExecutorConfiguration executorConfiguration = executorConfigManager.getConfig();

			final AProcessBuilder processBuilder = AProcess.builder("chatbotmessages", intent.getName().orElse("unknown"), userAction.getTimestamp(), userAction.getTimestamp()) // timestamp of emitted event
					.addTag("text", userAction.getText())
					.addTag("type", rta.name())
					.addTag("sessionId", userAction.getSenderId())
					.addTag("messageId", userAction.getMessageId())
					.addTag("botId", String.valueOf(executorConfiguration.getBotId()))
					.addTag("nodId", String.valueOf(executorConfiguration.getNodId()))
					.addTag("traId", String.valueOf(executorConfiguration.getTraId()))
					.addTag("modelName", String.valueOf(executorConfiguration.getModelName()))
					.setMeasure("confidence", intent.getConfidence().doubleValue())
					.setMeasure("isFallback", intent.getConfidence().compareTo(executorConfiguration.getNluThreshold()) < 0 ? 1d : 0d)
					.setMeasure("isTypeOpen", rta == RasaTypeAction.OPEN ? 1d : 0d)
					.setMeasure("isTypeMessage", rta == RasaTypeAction.MESSAGE ? 1d : 0d)
					.setMeasure("isTypeResponseInfo", rta == RasaTypeAction.RESPONSE_INFO ? 1d : 0d)
					.setMeasure("isTypeButton", rta == RasaTypeAction.BUTTON ? 1d : 0d)
					.setMeasure("isTypeRating", rta == RasaTypeAction.RATING ? 1d : 0d)
					.setMeasure("isUserMessage", rta.isUserMessage() ? 1d : 0d);

			analyticsManager.addProcess(processBuilder.build());
		}
	}

	@Override
	public void stop() {
		Exception exception = null;
		try {
			if (channel != null) {
				channel.close();
			}
		} catch (final Exception e) {
			exception = e;
		}
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (final Exception e) {
			if (exception != null) {
				e.addSuppressed(exception);
			}
			exception = e;
		}
		if (exception != null) {
			LOGGER.error("Erreur while closing RabbitMQ connexion", exception);
		}
	}

}
