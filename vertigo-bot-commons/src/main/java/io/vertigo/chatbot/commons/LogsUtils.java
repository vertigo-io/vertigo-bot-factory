package io.vertigo.chatbot.commons;

public final class LogsUtils {

	private static final String BR = "<br>";
	private static final String OK = "OK";
	private static final String KO = "KO";

	LogsUtils() {
		//utils class
	}

	public static void addLogs(final StringBuilder logs, final String... args) {
		for (final String arg : args) {
			logs.append(arg);
		}
	}

	public static void addLogs(final StringBuilder logs, final Object arg) {
		logs.append(arg);
	}

	public static void logOK(final StringBuilder logs) {
		logs.append(OK);
		breakLine(logs);
	}

	public static void logKO(final StringBuilder logs) {
		logs.append(KO);
		breakLine(logs);
	}

	public static void breakLine(final StringBuilder logs) {
		logs.append(BR);
	}

}
