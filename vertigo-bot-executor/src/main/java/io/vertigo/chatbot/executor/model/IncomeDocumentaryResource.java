package io.vertigo.chatbot.executor.model;

/**
 * Documentary resource click information (Java 17 record).
 * 
 * @param dreId Documentary resource ID
 * @param title Documentary resource title
 * @param dreTypeCd Documentary resource type code (URL or FILE)
 */
public record IncomeDocumentaryResource(
		Long dreId,
		String title,
		String dreTypeCd
) {}
