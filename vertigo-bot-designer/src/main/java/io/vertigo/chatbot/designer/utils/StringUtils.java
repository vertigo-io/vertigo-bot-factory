package io.vertigo.chatbot.designer.utils;

import java.util.regex.Pattern;

import io.vertigo.core.lang.VUserException;
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
		if (html == null) {
			return true;
		}
		return StringUtil.isBlank(emptyHtmlCleanerPattern.matcher(html).replaceAll(r -> ""));
	}

	/*
	 * Return an error message with the line concerned
	 */
	public static String lineError(final int i) {
		return "[Line " + i + "] ";
	}

	public static void errorManagement(final int i, final String erreur) {
		throw new VUserException(lineError(i) + erreur);
	}
}
