package io.vertigo.chatbot.designer.builder.services.topic.export;

import io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonExport;
import io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonUrlExport;

import java.util.List;
import java.util.regex.Pattern;

public class BtBuilderUtils {

	private static final String LINE_BREAK = "\n";
	private static final String QUOTE = "\"";
	private static final String SPACE = " ";
	private static final String OPEN_BRACKET = "{{";
	private static final String CLOSE_BRACKET = "}}";
	private static final Pattern BREAK_DELIMITER_PATTERN = Pattern.compile("<hr\\s*/?>");

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
	public static void createButton(final String text, final List<ResponseButtonExport> responses, final List<ResponseButtonUrlExport> responsesUrl, final StringBuilder bt) {
		String topicCode = !responses.isEmpty() ? responses.get(0).getTopCode() : responsesUrl.get(0).getTopCode() ;
		final String bb = String.format("/user/local/topic/%s/responsebutton", topicCode.toLowerCase());
		bt.append("begin choose:button:nlu ");
		bt.append(bb);
		addSpaceQuote(bt);
		bt.append(text.replaceAll("'", "\'"));
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
		for (final ResponseButtonUrlExport responseUrl : responsesUrl) {
			bt.append("button:url ");
			addQuote(bt);
			bt.append(responseUrl.getText().replaceAll("'", "\'"));
			addQuote(bt);
			bt.append(SPACE);
			bt.append("LINK");
			bt.append(SPACE);
			addQuote(bt);
			bt.append(responseUrl.getUrl());
			addQuote(bt);
			bt.append(SPACE);
			bt.append(responseUrl.getNewTab().toString());
			addLineBreak(bt);
		}
		bt.append("end choose:button:nlu");
		addLineBreak(bt);
		bt.append("begin switch");
		bt.append(SPACE);
		bt.append(bb);
		addLineBreak(bt);
		bt.append("begin case");
		bt.append(SPACE);
		addQuote(bt);
		bt.append("LINK");
		addQuote(bt);
		addLineBreak(bt);
		bt.append("topic !IDLE");
		addLineBreak(bt);
		bt.append("end case");
		addLineBreak(bt);
		bt.append("topic ");
		bt.append(OPEN_BRACKET);
		bt.append(bb);
		bt.append(CLOSE_BRACKET);
		addLineBreak(bt);
		bt.append("end switch");
		addLineBreak(bt);
	}

	/* begin selector
	 * fulfilled /user/local/<topicCode>/responseButton
	 * begin random
	 * say "text"
	 * say "text2"
	 * end random
	 * end selector
	 * choose:button:nlu topic/<topicCode>/responseButton ""
	 * button value "label"
	 * button value2 "label2"
	 * end choose:button:nlu
	 * topic topic/<topicCode>/responseButton
	 *
	 */
	public static void createSelectorRandomSequence(final String[] splitUtter, final List<ResponseButtonExport> responses,
													final List<ResponseButtonUrlExport> responsesUrl, final StringBuilder bt)  {
		bt.append("begin selector");
		addLineBreak(bt);
		final String bb = String.format("fulfilled /user/local/topic/%s/responsebutton", responses.get(0).getTopCode().toLowerCase());
		bt.append(bb);
		addLineBreak(bt);
		createRandomSequence(splitUtter, bt);
		bt.append("end selector");
		addLineBreak(bt);
		createButton("", responses, responsesUrl, bt);
	}

	/*
	 * say "<splitUtter[0]>"
	 */
	public static void createRichtext(final String[] splitUtter, final StringBuilder bt) {
		for (final String text : BREAK_DELIMITER_PATTERN.split(splitUtter[0])) {
			bt.append("say");
			addSpaceQuote(bt);
			//Only one utter text
			bt.append(text.replaceAll("'", "\'"));
			addQuote(bt);
			addLineBreak(bt);
		}
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
			bt.append("begin sequence");
			addLineBreak(bt);
			for (final String messageBubble : BREAK_DELIMITER_PATTERN.split(text)) {
				bt.append("say ");
				addQuote(bt);
				bt.append(messageBubble.replaceAll("'", "\'"));
				addQuote(bt);
				addLineBreak(bt);
			}
			bt.append("end sequence");
			addLineBreak(bt);
		}
		addLineBreak(bt);
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
