package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.List;

import io.vertigo.chatbot.designer.builder.topic.export.ResponseButtonExport;

public class BtBuilderUtils {

	private final static String LINE_BREAK = "\n";
	private final static String QUOTE = "\"";
	private final static String SPACE = " ";
	private final static String OPEN_BRACKET = "{{";
	private final static String CLOSE_BRACKET = "}}";

	private BtBuilderUtils() {
		//Classe utilitaire
	}

	public static void createButton(final String text, final List<ResponseButtonExport> responses, final StringBuilder bt) {
		final String bb = getResponseBB(responses.get(0).getTopCode());
		bt.append("choose:button:nlu ");
		bt.append(bb);
		addQuote(bt);
		bt.append(text);
		addQuote(bt);
		addLineBreak(bt);
		for (final ResponseButtonExport response : responses) {
			bt.append("button ");
			addQuote(bt);
			bt.append(response.getText());
			addQuote(bt);
			bt.append(" topic");
			bt.append(response.getTopCodeResponse());
			addLineBreak(bt);
		}
		bt.append("end choose:button:nlu");
		addLineBreak(bt);
		bt.append("topic ");
		bt.append(OPEN_BRACKET);
		bt.append(bb);
		bt.append(CLOSE_BRACKET);
		addLineBreak(bt);
	}

	private static String getResponseBB(final Long topCode) {
		final StringBuilder builder = new StringBuilder();
		builder.append("topic/");
		builder.append(topCode);
		builder.append("/responseButton ");
		return builder.toString();
	}

	public static void createButtonRandomText(final String[] splitUtter, final List<ResponseButtonExport> responses, final StringBuilder bt) {
		createRandomSequence(splitUtter, bt);
		createButton("", responses, bt);
	}

	public static void createRichtext(final String[] splitUtter, final StringBuilder bt) {
		bt.append("say");
		addSpaceQuote(bt);
		//Only one utter text
		bt.append(splitUtter[0]);
		addSpaceQuote(bt);
		addLineBreak(bt);
	}

	public static void createRandomSequence(final String[] splitUtter, final StringBuilder bt) {
		bt.append("begin random");
		addLineBreak(bt);
		for (final String text : splitUtter) {
			bt.append("say ");
			addQuote(bt);
			bt.append(text);
			addQuote(bt);
			addLineBreak(bt);
		}
		bt.append("end random");
		addLineBreak(bt);
	}

	public static void addLineBreak(final StringBuilder builder) {
		builder.append(LINE_BREAK);
	}

	public static void addQuote(final StringBuilder bt) {
		bt.append(QUOTE);
	}

	public static void addSpaceQuote(final StringBuilder bt) {
		bt.append(SPACE + QUOTE);
	}

}
