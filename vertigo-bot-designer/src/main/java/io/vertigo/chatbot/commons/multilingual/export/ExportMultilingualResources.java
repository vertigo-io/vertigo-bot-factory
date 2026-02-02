package io.vertigo.chatbot.commons.multilingual.export;

import io.vertigo.core.locale.LocaleMessageKey;

/**
 * Multilingual resources for export-related messages.
 * This enum provides internationalized messages for various export operations
 * including error messages and file type descriptions.
 *
 * @author Chatbot Team
 */
public enum ExportMultilingualResources implements LocaleMessageKey {

	ERR_CSV_FILE,
	ERR_SIZE_FILE,
	ERR_MAPPING_FILE,
	ERR_UNEXPECTED,
	MANDATORY_TYPE_BOT_EXPORT,
	FILE_TYPE_CATEGORIES,
	FILE_TYPE_TOPICS,
	FILE_TYPE_DICTIONARY,
	FILE_TYPE_UNKNOWN_MESSAGES,
	FILE_TYPE_USER_ACTIONS_CONVERSATIONS,
	FILE_TYPE_CONVERSATION_STATS,
	FILE_TYPE_TOPIC_USAGE,
	FILE_TYPE_DOCUMENTARY_RESOURCES,
	FILE_TYPE_QUESTION_ANSWERS
}
