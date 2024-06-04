package io.vertigo.chatbot.analytics;

import java.util.List;
import java.util.UUID;

import io.vertigo.core.analytics.trace.TraceSpan;
import io.vertigo.core.analytics.trace.TraceSpanBuilder;
import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.analytics.AnalyticsManager;
import io.vertigo.core.node.component.Component;

@Transactional
public class AnalyticsSenderServices implements Component {

	@Inject
	private AnalyticsManager analyticsManager;

	/**
	 * Send all the events to the events database
	 *
	 * @param botResponse botResponse
	 * @param executorConfiguration the executor configuration (i.e. node, bot)
	 * @param input the user input
	 */
	public void sendEventToDb(final UUID sessionId, final BotResponse botResponse, final ExecutorConfiguration executorConfiguration, final BotInput input) {
		//Get values from response
		final AnalyticsObjectSend analytics = (AnalyticsObjectSend) botResponse.getMetadatas().get(BotEngine.ANALYTICS_KEY);
		final String codeTopic = analytics.getTopic().getCode();
		final Double accuracy = analytics.getAccuracy();
		final List<TopicDefinition> topicsPast = analytics.getTopicsPast();

		final boolean isNlu = accuracy != null;

		if (isNlu) {
			sendNluEvent(sessionId, input, codeTopic, accuracy, executorConfiguration);
		}
		sendPastTopics(sessionId, topicsPast, executorConfiguration);

		sendBotInputEvent(sessionId, input, executorConfiguration, botResponse);

		sendConversationEvent(sessionId, String.join("\0", botResponse.getHtmlTexts()), false, executorConfiguration);
	}

	private void sendBotInputEvent(final UUID sessionId, final BotInput input, final ExecutorConfiguration executorConfiguration, final BotResponse botResponse) {
		if (input.getMessage() != null) {
			if (botResponse.getRating().isEnabled()) {
				final IncomeRating incomeRating = new IncomeRating();
				incomeRating.setComment(input.getMessage());
				rateComment(sessionId, incomeRating, executorConfiguration);
			}
			sendConversationEvent(sessionId, input.getMessage(), true, executorConfiguration);
		} else if (input.getMetadatas().get("rating") != null) {
			final String rating = (String) input.getMetadatas().get("rating");
			final IncomeRating incomeRating = new IncomeRating();
			incomeRating.setNote(Integer.parseInt(rating));
			rate(sessionId, incomeRating, executorConfiguration);
			sendConversationEvent(sessionId, rating, true, executorConfiguration);
		} else if (input.getMetadatas().get("text") != null) {
			sendConversationEvent(sessionId, (String) input.getMetadatas().get("text"), true, executorConfiguration);
		} else if (input.getMetadatas().get("filename") != null) {
			sendConversationEvent(sessionId, (String) input.getMetadatas().get("filename"), true, executorConfiguration);
		}
	}

	//Send an event with the nlu topics
	//Send also all the topics passed with topic {} instruction
	private void sendNluEvent(final UUID sessionId, final BotInput input, final String codeTopic, final Double accuracy, final ExecutorConfiguration executorConfiguration) {
		final TraceSpanBuilder processBuilder = AnalyticsUtils.prepareMessageProcess(codeTopic, input.getMessage(), AnalyticsUtils.TEXT_KEY)
				.withTag(AnalyticsUtils.NLU_KEY, AnalyticsUtils.TRUE)
				.withTag(AnalyticsUtils.USER_MESSAGE_KEY, AnalyticsUtils.TRUE)
				.withMeasure(AnalyticsUtils.CONFIDENCE_KEY, accuracy);
		//test if topic is a fallback topic
		if (codeTopic.equals(BotEngine.FALLBACK_TOPIC_NAME)) {
			prepareFallBackEvent(processBuilder);
		} else if (codeTopic.equals(BotEngine.RATING_TOPIC_NAME)) {
			prepareRatingEvent(processBuilder);
		} else {
			processBuilder.withTag(AnalyticsUtils.TECHNICAL_KEY, AnalyticsUtils.FALSE);
		}
		sendProcessWithConfiguration(sessionId, processBuilder, executorConfiguration);
	}

	private static void prepareFallBackEvent(final TraceSpanBuilder builder) {
		builder
				.withTag(AnalyticsUtils.TECHNICAL_KEY, AnalyticsUtils.TRUE)
				.withTag(AnalyticsUtils.FALLBACK_KEY, AnalyticsUtils.TRUE);

	}

	private static void prepareRatingEvent(final TraceSpanBuilder builder) {
		builder
				.withTag(AnalyticsUtils.TECHNICAL_KEY, AnalyticsUtils.TRUE)
				.withTag(AnalyticsUtils.RATING_KEY, AnalyticsUtils.TRUE);

	}

	//Send all the topics passed during the sequence in BT
	private void sendPastTopics(final UUID sessionId, final List<TopicDefinition> topicsPast, final ExecutorConfiguration executorConfiguration) {
		boolean isFirst = true;
		//Can't have fallback instructions
		for (final TopicDefinition topic : topicsPast) {
			//Create the measurements
			final String codeTopic = topic.getCode();
			final boolean isTechnical = codeTopic.equals(BotEngine.END_TOPIC_NAME) || codeTopic.equals(BotEngine.RATING_TOPIC_NAME);
			final String type = isFirst ? AnalyticsUtils.BUTTONS_INPUT_KEY : AnalyticsUtils.SWITCH_INPUT_KEY;
			final TraceSpanBuilder processBuilder = AnalyticsUtils.prepareEmptyMessageProcess(codeTopic, type)
					.withTag(AnalyticsUtils.TYPE_KEY, isFirst ? AnalyticsUtils.BUTTONS_INPUT_KEY : AnalyticsUtils.SWITCH_INPUT_KEY)
					.withTag(AnalyticsUtils.TECHNICAL_KEY, isTechnical ? AnalyticsUtils.TRUE : AnalyticsUtils.FALSE)
					.withMeasure(AnalyticsUtils.CONFIDENCE_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
					.withTag(AnalyticsUtils.NLU_KEY, AnalyticsUtils.FALSE);
			sendProcessWithConfiguration(sessionId, processBuilder, executorConfiguration);
			isFirst = false;
		}

	}

	/**
	 * Set the event start topic to eventdb
	 *
	 * @param executorConfiguration the executor configuration (i.e. node, bot etc...)
	 */
	public void sendEventStartToDb(final UUID sessionId, final BotResponse botResponse, final ExecutorConfiguration executorConfiguration, final BotInput input) {
		//Create the process
		final AnalyticsObjectSend analytics = (AnalyticsObjectSend) botResponse.getMetadatas().get(BotEngine.ANALYTICS_KEY);
		final List<TopicDefinition> topicsPast = analytics.getTopicsPast();
		final TraceSpanBuilder processBuilder = AnalyticsUtils.prepareEmptyMessageProcess(BotEngine.START_TOPIC_NAME, AnalyticsUtils.TECHNICAL_INPUT_KEY)
				.withTag(AnalyticsUtils.SESSION_START_KEY, AnalyticsUtils.TRUE)
				.withMeasure(AnalyticsUtils.CONFIDENCE_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
				.withTag(AnalyticsUtils.TECHNICAL_KEY, AnalyticsUtils.TRUE);

		sendProcessWithConfiguration(sessionId, processBuilder, executorConfiguration);
		sendPastTopics(sessionId, topicsPast, executorConfiguration);
		sendBotInputEvent(sessionId, input, executorConfiguration, botResponse);
		sendConversationEvent(sessionId, String.join("\0", botResponse.getHtmlTexts()), false, executorConfiguration);
	}

	private void sendProcessWithConfiguration(final UUID sessionId, final TraceSpanBuilder builder, final ExecutorConfiguration executorConfiguration) {
		AnalyticsUtils.setConfiguration(sessionId, builder, executorConfiguration);
		analyticsManager.addSpan(builder.build());
	}

	public void rate(final UUID sessionId, final IncomeRating rating, final ExecutorConfiguration executorConfiguration) {
		sendProcessWithConfiguration(sessionId, AnalyticsUtils.prepareRatingProcess(rating), executorConfiguration);
	}

	public void rateComment(final UUID sessionId, final IncomeRating rating, final ExecutorConfiguration executorConfiguration) {
		sendProcessWithConfiguration(sessionId, AnalyticsUtils.prepareRatingCommentProcess(rating), executorConfiguration);
	}

	public void sendConversationEvent(final UUID sessionId, final String text, final boolean userMessage, final ExecutorConfiguration executorConfiguration) {
		sendProcessWithConfiguration(sessionId, AnalyticsUtils.prepareConversationProcess(text, userMessage), executorConfiguration);
	}

}
