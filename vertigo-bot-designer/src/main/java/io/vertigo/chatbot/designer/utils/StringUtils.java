package io.vertigo.chatbot.designer.utils;

import java.util.regex.Pattern;

import io.vertigo.core.util.StringUtil;

public final class StringUtils {

	private static final Pattern emptyHtmlCleanerPattern = Pattern.compile("</?(div|br|p|b|i|font)( .*?)?/?>|&nbsp;");

	private StringUtils() {
		//utility class
	}

	/**
	 * Check if HTML is empty.
	 * Ignore some markups like br and div then check if only empty characters remains.
	 *
	 * @param html the HTML to check
	 * @return true if content is empty
	 */
	public static boolean isHtmlEmpty(final String html) {
		return StringUtil.isBlank(emptyHtmlCleanerPattern.matcher(html).replaceAll(r -> ""));
	}
}
