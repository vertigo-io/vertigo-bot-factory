package io.vertigo.chatbot.engine.plugins.bt.confluence.command.bot;

import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.inputString;

import java.util.Arrays;
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
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceSearchHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.impl.ConfluenceServerServices;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResult;
import io.vertigo.core.node.component.Component;

public final class BotConfluenceNodeProvider implements Component {

	@Inject
	private ConfluenceServerServices confluenceServerService;

	public BTNode confluenceSearch(final BlackBoard bb, final String keyTemplate, final String question, final String listPresentation) {
		return sequence(
				inputString(bb, keyTemplate, question),
				() -> {
					bb.listPush(BotEngine.BOT_RESPONSE_KEY, listPresentation);
					final Map<String, String> headers = confluenceServerService.getHeadersWithAuthorization();
					final Map<String, String> params = new HashMap<>();
					final var searchObject = ConfluenceSearchHelper.createConfluenceSearchObject(bb.getString(BBKey.of(keyTemplate)));
					final ConfluenceSearchResponse searchResult = confluenceServerService.searchOnConfluence(params, headers, searchObject);
					final List<ConfluenceSearchResult> results = Arrays.asList(searchResult.getResults());
					for (ConfluenceSearchResult result : results) {
						bb.listPush(BotEngine.BOT_RESPONSE_KEY, createLinkUrl(result.getUrl(), result.getDetail().getTitle()));
					}
					bb.delete(BBKeyPattern.of(keyTemplate));
					return BTStatus.Succeeded;
				});

	}

	private String createLinkUrl(final String link, final String name) {
		final String url = confluenceServerService.getBaseUrl() + "/" + link;
		final var builder = new StringBuilder();
		builder.append("<a href=\"");
		builder.append(url);
		builder.append("\">");
		builder.append(name);
		builder.append("</a>");
		return builder.toString();
	}

}
