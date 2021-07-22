package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.List;

import io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonExport;

public class BtBuilderUtils {

	private static final String LINE_BREAK = "\n";
	private static final String QUOTE = "\"";
	private static final String SPACE = " ";
	private static final String OPEN_BRACKET = "{{";
	private static final String CLOSE_BRACKET = "}}";

	private BtBuilderUtils() {
		//Classe utilitaire
	}

	/*
	 * choose:button:nlu topic/<topicCode>/responseButton "text"
	 * button value "label"
	 * button value2 "label2"
	 * end choose:button:nlu
	 * topic topic/<topicCode>/responseButton
	 *
	 */
	public static void createButton(final String text, final List<ResponseButtonExport> responses, final StringBuilder bt) {
		final String bb = String.format("/user/local/topic/%s/responsebutton", responses.get(0).getTopCode());
		bt.append("begin choose:button:nlu ");
		bt.append(bb);
		addSpaceQuote(bt);
		bt.append(text);
		addQuote(bt);
		addLineBreak(bt);
		for (final ResponseButtonExport response : responses) {
			bt.append("button ");
			addQuote(bt);
			bt.append(response.getText().replaceAll("'", "\'"));
			addQuote(bt);
			bt.append(SPACE);
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

	/*
	 * begin random
	 * say "text"
	 * say "text2"
	 * end random
	 * choose:button:nlu topic/<topicCode>/responseButton ""
	 * button value "label"
	 * button value2 "label2"
	 * end choose:button:nlu
	 * topic topic/<topicCode>/responseButton
	 *
	 */
	public static void createButtonRandomText(final String[] splitUtter, final List<ResponseButtonExport> responses, final StringBuilder bt) {
		createRandomSequence(splitUtter, bt);
		createButton("", responses, bt);
	}

	/*
	 * say "<splitUtter[0]>"
	 */
	public static void createRichtext(final String[] splitUtter, final StringBuilder bt) {
		bt.append("say");
		addSpaceQuote(bt);
		//Only one utter text
		bt.append(splitUtter[0].replaceAll("'", "\'"));
		addQuote(bt);
		addLineBreak(bt);
	}

	/*
	 * begin random
	 * say "text"
	 * say "text2"
	 * end random
	 */
	public static void createRandomSequence(final String[] splitUtter, final StringBuilder bt) {
		bt.append("begin random");
		addLineBreak(bt);
		for (final String text : splitUtter) {
			bt.append("say ");
			addQuote(bt);
			bt.append(text.replaceAll("'", "\'"));
			addQuote(bt);
			addLineBreak(bt);
		}
		bt.append("end random");
		addLineBreak(bt);
	}

	/*
	 * choose:nlu "text"
	 */
	public static void askNlu(final StringBuilder bt, final String text) {
		bt.append("choose:nlu");
		addSpaceQuote(bt);
		bt.append(text);
		addQuote(bt);
	}

	public static void addLineBreak(final StringBuilder builder) {
		builder.append(LINE_BREAK);
	}

	public static void addQuote(final StringBuilder bt) {
		bt.append(QUOTE);
	}

	public static void addSpaceQuote(final StringBuilder bt) {
		bt.append(SPACE);
		bt.append(QUOTE);
	}

}
