package io.vertigo.chatbot.engine.plugins.bt.confluence.command.bot;

import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.inputString;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceSearchHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.impl.ConfluenceServerService;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResult;
import io.vertigo.core.node.component.Component;

public final class BotConfluenceNodeProvider implements Component {

	@Inject
	private ConfluenceServerService confluenceServerService;

	public BTNode confluenceSearch(final BlackBoard bb, final String keyTemplate, final String question) {
		return sequence(
				inputString(bb, keyTemplate, question),
				() -> {
					final Map<String, String> headers = ConfluenceHttpRequestHelper.getHeadersWithAuthorization();
					final Map<String, String> params = new HashMap<>();
					params.put("limit", "3");
					final var searchObject = ConfluenceSearchHelper.createConfluenceSearchObject(bb.getString(BBKey.of(keyTemplate)));
					final ConfluenceSearchResponse searchResult = confluenceServerService.searchOnConfluence(params, headers, searchObject);
					final List<ConfluenceSearchResult> results = Arrays.asList(searchResult.getResults());
					//Reverse to push in the good way
					Collections.reverse(results);
					for (ConfluenceSearchResult result : results) {
						bb.listPush(BotEngine.BOT_RESPONSE_KEY, createLinkUrl(result.getUrl(), result.getDetail().getTitle()));
					}
					bb.delete(BBKeyPattern.of(keyTemplate));
					return BTStatus.Succeeded;
				});

	}

	private String createLinkUrl(final String link, final String name) {
		final String url = ConfluenceHttpRequestHelper.BASE_URL + "/" + link;
		final var builder = new StringBuilder();
		builder.append("<a href=\"");
		builder.append(url);
		builder.append("\">");
		builder.append(name);
		builder.append("</a>");
		return builder.toString();
	}

}
