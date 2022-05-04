package io.vertigo.chatbot.analytics;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.core.analytics.process.AProcess;
import io.vertigo.core.analytics.process.AProcessBuilder;

import java.time.Instant;
import java.util.UUID;

public final class AnalyticsUtils {

	//Analytics keys
	public static final String DB_KEY = "chatbotmessages";
	public static final String SESSION_ID_KEY = "sessionId";
	public static final String TEXT_KEY = "text";
	public static final String TYPE_KEY = "type";
	public static final String RATING_KEY = "rating";
	public static final String RATING_COMMENT_KEY = "ratingComment";
	public static final String NLU_KEY = "isNlu";
	public static final String USER_MESSAGE_KEY = "isUserMessage";
	public static final String CONFIDENCE_KEY = "confidence";
	public static final String TECHNICAL_KEY = "isTechnical";
	public static final String FALLBACK_KEY = "isFallback";
	public static final String SESSION_START_KEY = "isSessionStart";

	//Input types key
	public static final String BUTTONS_INPUT_KEY = "button";
	//for the topic {} instructions
	public static final String SWITCH_INPUT_KEY = "switch";
	public static final String TECHNICAL_INPUT_KEY = "technical";
	public static final String RATING_INPUT_KEY = "technical_rating";

	//Training keys
	public static final String BOT_KEY = "botId";
	public static final String NODE_KEY = "nodId";
	public static final String TRAINING_KEY = "traId";
	public static final String MODEL_KEY = "modelName";

	public static final Double TRUE_BIGDECIMAL = 1D;
	public static final Double FALSE_BIGDECIMAL = 0D;

	private AnalyticsUtils() {
		//utils class
	}

	public static AProcessBuilder prepareMessageProcess(final String codeTopic, final String text, final String type) {
		return AProcess.builder(DB_KEY, codeTopic, Instant.now(), Instant.now()) // timestamp of emitted event
				.addTag(TEXT_KEY, text)
				.addTag(TYPE_KEY, type);
	}

	public static AProcessBuilder prepareEmptyMessageProcess(final String codeTopic, final String type) {
		return prepareMessageProcess(codeTopic, "", type);
	}

	public static AProcessBuilder prepareRatingProcess(final IncomeRating incomeRating) {
		final AProcessBuilder builder =  AProcess.builder(RATING_KEY, RATING_KEY, Instant.now(), Instant.now())
				.addTag(RATING_INPUT_KEY, incomeRating.getNote().toString())// timestamp of emitted event
				.setMeasure(RATING_KEY + incomeRating.getNote().toString(), TRUE_BIGDECIMAL)
				.setMeasure(RATING_KEY, incomeRating.getNote());
		if (incomeRating.getComment() != null) {
			builder.addTag(RATING_COMMENT_KEY, incomeRating.getComment());
		}
		return builder;
	}

	public static void setConfiguration(final UUID sessionId, final AProcessBuilder builder, final ExecutorConfiguration executorConfiguration) {
		builder
				.addTag(SESSION_ID_KEY, sessionId.toString())
				.addTag(BOT_KEY, String.valueOf(executorConfiguration.getBotId()))
				.addTag(NODE_KEY, String.valueOf(executorConfiguration.getNodId()))
				.addTag(TRAINING_KEY, String.valueOf(executorConfiguration.getTraId()))
				.addTag(MODEL_KEY, String.valueOf(executorConfiguration.getModelName()));
	}

}
