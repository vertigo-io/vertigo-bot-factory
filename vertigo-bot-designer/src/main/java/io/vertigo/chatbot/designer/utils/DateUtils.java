package io.vertigo.chatbot.designer.utils;

import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

	public static final String FORMAT_JJMMAAAA = "dd/MM/yyyy";
	public static final String FORMAT_JJMMAAAAHHMM = "dd/MM/yyyy HH:mm";

	public static final DateTimeFormatter FORMAT_JJMMAAAA_PATTERN = DateTimeFormatter.ofPattern(FORMAT_JJMMAAAA);
	public static final DateTimeFormatter FORMAT_JJMMAAAAHHMM_PATTERN = DateTimeFormatter.ofPattern(FORMAT_JJMMAAAAHHMM);

	private DateUtils() {

	}

	public static String toStringJJMMAAAA(final ChronoLocalDate date) {
		return date.format(FORMAT_JJMMAAAA_PATTERN);
	}
}
