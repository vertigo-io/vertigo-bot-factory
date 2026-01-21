package io.vertigo.chatbot.executor.model;

/**
 * Question/Answer click information (Java 17 record).
 * 
 * @param qaId Question/Answer ID
 * @param question Question text
 * @param catLabel Category label
 */
public record IncomeQuestionAnswer(
		Long qaId,
		String question,
		String catLabel
) {}
