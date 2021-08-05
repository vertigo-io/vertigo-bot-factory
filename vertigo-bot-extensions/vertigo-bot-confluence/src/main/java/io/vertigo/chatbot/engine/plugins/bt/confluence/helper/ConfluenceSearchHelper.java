package io.vertigo.chatbot.engine.plugins.bt.confluence.helper;

import java.util.Arrays;
import java.util.List;

import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchObject;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchOperator;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.MultipleConfluenceSearch;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.SingleConfluenceSearch;

public final class ConfluenceSearchHelper {

	private static final String TITLE = "title";
	private static final String TEXT = "text";

	private ConfluenceSearchHelper() {
		//helper
	}

	public static ConfluenceSearchObject createConfluenceSearchObject(final String phrase) {
		final List<String> words = Arrays.asList(phrase.split(" "));
		final ConfluenceSearchObject titleSearch = createSearchObject(words, TITLE);
		final ConfluenceSearchObject textSearch = createSearchObject(words, TEXT);
		return new MultipleConfluenceSearch(titleSearch, textSearch, ConfluenceSearchOperator.OR);
	}

	private static ConfluenceSearchObject createSearchObject(final List<String> words, final String confluenceKey) {
		ConfluenceSearchObject result = new SingleConfluenceSearch(confluenceKey, ConfluenceSearchOperator.CONTAINS, words.get(0));
		//The first result is remove
		for (int i = 1; i < words.size(); i++) {
			final String word = words.get(i);
			final ConfluenceSearchObject newSearch = new SingleConfluenceSearch(confluenceKey, ConfluenceSearchOperator.CONTAINS, word);
			result = new MultipleConfluenceSearch(result, newSearch, ConfluenceSearchOperator.OR);
		}

		return result;
	}

}
