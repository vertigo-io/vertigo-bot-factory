package io.vertigo.chatbot.analytics;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.core.analytics.trace.TraceSpan;
import io.vertigo.core.analytics.trace.TraceSpanBuilder;

/**
 * Test class for analytics tracking functionality.
 * Tests the creation of trace spans for documentary resource and question/answer clicks.
 *
 * @author Chatbot Team
 */
public class AnalyticsTrackingTest {

	private ExecutorConfiguration executorConfiguration;

	@BeforeEach
	public void setUp() {
		executorConfiguration = new ExecutorConfiguration();
		executorConfiguration.setBotId(1L);
		executorConfiguration.setNodId(2L);
		executorConfiguration.setTraId(3L);
		executorConfiguration.setModelName("test-model");
		executorConfiguration.setNluThreshold(BigDecimal.valueOf(0.6));
	}

	@AfterEach
	public void tearDown() {
		executorConfiguration = null;
	}

	/**
	 * Test that prepareDocumentaryResourceClickProcess creates a trace span builder.
	 */
	@Test
	public void testPrepareDocumentaryResourceClickProcess() {
		// Arrange
		final Long dreId = 123L;
		final String title = "Test Documentary Resource";
		final String dreTypeCd = "URL";

		// Act
		final TraceSpanBuilder builder = AnalyticsUtils.prepareDocumentaryResourceClickProcess(dreId, title, dreTypeCd);

		// Assert
		Assertions.assertNotNull(builder, "Trace span builder should not be null");
		
		// Build the span to ensure the builder was configured correctly
		final TraceSpan span = builder.build();
		Assertions.assertNotNull(span, "Built trace span should not be null");
	}

	/**
	 * Test that prepareQuestionAnswerClickProcess creates a trace span builder.
	 */
	@Test
	public void testPrepareQuestionAnswerClickProcess() {
		// Arrange
		final Long qaId = 456L;
		final String question = "What is the meaning of life?";
		final String catLabel = "Philosophy";

		// Act
		final TraceSpanBuilder builder = AnalyticsUtils.prepareQuestionAnswerClickProcess(qaId, question, catLabel);

		// Assert
		Assertions.assertNotNull(builder, "Trace span builder should not be null");
		
		// Build the span to ensure the builder was configured correctly
		final TraceSpan span = builder.build();
		Assertions.assertNotNull(span, "Built trace span should not be null");
	}

	/**
	 * Test that prepareQuestionAnswerClickProcess handles long questions without error.
	 * The method should truncate questions longer than 250 characters.
	 */
	@Test
	public void testPrepareQuestionAnswerClickProcessWithLongQuestion() {
		// Arrange
		final Long qaId = 789L;
		final String longQuestion = "A".repeat(300); // 300 characters
		final String catLabel = "Test Category";

		// Act - should not throw any exception
		final TraceSpanBuilder builder = AnalyticsUtils.prepareQuestionAnswerClickProcess(qaId, longQuestion, catLabel);

		// Assert
		Assertions.assertNotNull(builder, "Trace span builder should not be null even with long question");
		final TraceSpan span = builder.build();
		Assertions.assertNotNull(span, "Built trace span should not be null");
	}

	/**
	 * Test that setConfiguration adds configuration to a trace span builder without error.
	 */
	@Test
	public void testSetConfiguration() {
		// Arrange
		final UUID sessionId = UUID.randomUUID();
		final TraceSpanBuilder builder = AnalyticsUtils.prepareDocumentaryResourceClickProcess(1L, "Test", "URL");

		// Act - should not throw any exception
		AnalyticsUtils.setConfiguration(sessionId, builder, executorConfiguration);

		// Assert
		final TraceSpan span = builder.build();
		Assertions.assertNotNull(span, "Built trace span should not be null after configuration");
	}

}
