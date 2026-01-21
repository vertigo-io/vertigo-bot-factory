package io.vertigo.chatbot.analytics;

import java.time.Instant;
import java.util.UUID;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.core.analytics.trace.TraceSpan;
import io.vertigo.core.analytics.trace.TraceSpanBuilder;

/**
 * Utility class for building analytics trace spans.
 * Provides helper methods to create trace spans for different event types
 * including messages, ratings, conversations, and documentary resource clicks.
 *
 * @author Chatbot Team
 */
public final class AnalyticsUtils {

	//Analytics keys
	public static final String DB_KEY = "chatbotmessages";
	public static final String SESSION_ID_KEY = "sessionId";
	public static final String TEXT_KEY = "text";
	public static final String TYPE_KEY = "type";
	public static final String RATING_KEY = "rating";
	public static final String CONVERSATION_KEY = "conversation";
	public static final String RATING_COMMENT_KEY = "ratingComment";
	public static final String NLU_KEY = "isNlu";
	public static final String USER_MESSAGE_KEY = "isUserMessage";
	public static final String BOT_MESSAGE_KEY = "isBotMessage";
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

	//Documentary resource keys
	public static final String DOCUMENTARY_RESOURCE_KEY = "documentaryresource";
	public static final String DRE_ID_KEY = "dreId";
	public static final String TITLE_KEY = "title";
	public static final String DRE_TYPE_CD_KEY = "dreTypeCd";

	//Question/Answer keys
	public static final String QUESTION_ANSWER_KEY = "questionanswer";
	public static final String QA_ID_KEY = "qaId";
	public static final String QUESTION_KEY = "question";
	public static final String CAT_LABEL_KEY = "catLabel";

	public static final String TRUE = "1";
	public static final String FALSE = "0";
	public static final Double TRUE_BIGDECIMAL = 1D;
	public static final Double FALSE_BIGDECIMAL = 0D;

	private AnalyticsUtils() {
		//utils class
	}

	/**
	 * Prepare a message process trace span
	 *
	 * @param codeTopic topic code
	 * @param text message text
	 * @param type message type
	 * @return trace span builder
	 */
	public static TraceSpanBuilder prepareMessageProcess(final String codeTopic, final String text, final String type) {
		return TraceSpan.builder(DB_KEY, codeTopic, Instant.now(), Instant.now()) // timestamp of emitted event
				.withTag(TEXT_KEY, text)
				.withTag(TYPE_KEY, type);
	}

	/**
	 * Prepare an empty message process trace span
	 *
	 * @param codeTopic topic code
	 * @param type message type
	 * @return trace span builder
	 */
	public static TraceSpanBuilder prepareEmptyMessageProcess(final String codeTopic, final String type) {
		return prepareMessageProcess(codeTopic, "", type);
	}

	/**
	 * Prepare a rating process trace span
	 *
	 * @param incomeRating rating information
	 * @return trace span builder
	 */
	public static TraceSpanBuilder prepareRatingProcess(final IncomeRating incomeRating) {
		final TraceSpanBuilder builder = TraceSpan.builder(RATING_KEY, RATING_KEY, Instant.now(), Instant.now())
				.withTag(RATING_INPUT_KEY, incomeRating.getNote().toString())// timestamp of emitted event
				.withMeasure(RATING_KEY + incomeRating.getNote().toString(), TRUE_BIGDECIMAL)
				.withMeasure(RATING_KEY, incomeRating.getNote());
		if (incomeRating.getComment() != null) {
			builder.withTag(RATING_COMMENT_KEY, incomeRating.getComment());
		}
		return builder;
	}

	/**
	 * Prepare a rating comment process trace span
	 *
	 * @param incomeRating rating information with comment
	 * @return trace span builder
	 */
	public static TraceSpanBuilder prepareRatingCommentProcess(final IncomeRating incomeRating) {
		return TraceSpan.builder(RATING_KEY, RATING_KEY, Instant.now(), Instant.now())
				.withMeasure(RATING_KEY, FALSE_BIGDECIMAL)
				.withTag(RATING_COMMENT_KEY, incomeRating.getComment());
	}

	/**
	 * Prepare a conversation process trace span
	 *
	 * @param text message text
	 * @param userMessage true if message is from user, false if from bot
	 * @return trace span builder
	 */
	public static TraceSpanBuilder prepareConversationProcess(final String text, final boolean userMessage) {
		return TraceSpan.builder(CONVERSATION_KEY, CONVERSATION_KEY, Instant.now(), Instant.now())
				.withTag(TEXT_KEY, text)
				.withTag(USER_MESSAGE_KEY, userMessage ? TRUE : FALSE)
				.withTag(BOT_MESSAGE_KEY, !userMessage ? TRUE : FALSE);
	}

	/**
	 * Prepare a documentary resource click process trace span
	 *
	 * @param dreId documentary resource ID
	 * @param title documentary resource title
	 * @param dreTypeCd documentary resource type code
	 * @return trace span builder
	 */
	public static TraceSpanBuilder prepareDocumentaryResourceClickProcess(final Long dreId, final String title, final String dreTypeCd) {
		return TraceSpan.builder(DOCUMENTARY_RESOURCE_KEY, DOCUMENTARY_RESOURCE_KEY, Instant.now(), Instant.now())
				.withTag(DRE_ID_KEY, String.valueOf(dreId))
				.withTag(TITLE_KEY, title)
				.withTag(DRE_TYPE_CD_KEY, dreTypeCd)
				.withMeasure("name", TRUE_BIGDECIMAL);
	}

	/**
	 * Prepare a question/answer click process trace span
	 *
	 * @param qaId question/answer ID
	 * @param question question text (truncated to 250 chars if needed)
	 * @param catLabel category label
	 * @return trace span builder
	 */
	public static TraceSpanBuilder prepareQuestionAnswerClickProcess(final Long qaId, final String question, final String catLabel) {
		// Truncate question to 250 characters to avoid InfluxDB issues
		final String truncatedQuestion = question != null && question.length() > 250 
				? question.substring(0, 250) 
				: question;
		return TraceSpan.builder(QUESTION_ANSWER_KEY, QUESTION_ANSWER_KEY, Instant.now(), Instant.now())
				.withTag(QA_ID_KEY, String.valueOf(qaId))
				.withTag(QUESTION_KEY, truncatedQuestion)
				.withTag(CAT_LABEL_KEY, catLabel)
				.withMeasure("name", TRUE_BIGDECIMAL);
	}

	/**
	 * Set executor configuration on a trace span builder
	 *
	 * @param sessionId session identifier
	 * @param builder trace span builder
	 * @param executorConfiguration executor configuration with bot, node, and training info
	 */
	public static void setConfiguration(final UUID sessionId, final TraceSpanBuilder builder, final ExecutorConfiguration executorConfiguration) {
		builder
				.withTag(SESSION_ID_KEY, sessionId.toString())
				.withTag(BOT_KEY, String.valueOf(executorConfiguration.getBotId()))
				.withTag(NODE_KEY, String.valueOf(executorConfiguration.getNodId()))
				.withTag(TRAINING_KEY, String.valueOf(executorConfiguration.getTraId()))
				.withTag(MODEL_KEY, String.valueOf(executorConfiguration.getModelName()));
	}

}
