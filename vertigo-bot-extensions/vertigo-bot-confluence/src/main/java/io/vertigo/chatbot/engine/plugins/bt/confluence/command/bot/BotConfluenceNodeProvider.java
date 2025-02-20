package io.vertigo.chatbot.engine.plugins.bt.confluence.command.bot;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.confluence.impl.ConfluenceServerServices;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.inputString;
import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.say;

public final class BotConfluenceNodeProvider implements Component {

	@Inject
	private ConfluenceServerServices confluenceServerService;

	public BTNode confluenceSearch(final BlackBoard bb, final String keyTemplate, final String question, final String listPresentation, final String emptySearchResult, final String topicFallbackConfluence) {
		return sequence(
				inputString(bb, keyTemplate, question),
				() -> {
					final var searchObject = bb.getString(BBKey.of(keyTemplate));
					final List<String> searchResult = confluenceServerService.searchOnConfluenceCommand(searchObject);
					if (!searchResult.isEmpty()) {
						bb.listPush(BotEngine.BOT_RESPONSE_KEY, listPresentation);

						for (final String result : searchResult) {
							bb.listPush(BotEngine.BOT_RESPONSE_KEY, result);
						}
						bb.delete(BBKeyPattern.of(keyTemplate));
						return BTStatus.Succeeded;
					}
					bb.listPush(BotEngine.BOT_RESPONSE_KEY, emptySearchResult);
					return BotNodeProvider.switchTopic(bb, topicFallbackConfluence).eval();
				});

	}

	public BTNode confluenceAutomaticSearch(final BlackBoard bb, final String introductionSentence, final String emptySearchResult, final String topicFallbackConfluence, final String[] keyWords) {
		return sequence(
				say(bb, introductionSentence),
				() -> {
					final List<String> searchResult = confluenceServerService.searchOnConfluenceCommand(String.join(" ", keyWords));
					if (!searchResult.isEmpty()) {
						for (final String result : searchResult) {
							bb.listPush(BotEngine.BOT_RESPONSE_KEY, result);
						}
						return BTStatus.Succeeded;
					}
					bb.listPush(BotEngine.BOT_RESPONSE_KEY, emptySearchResult);
					return BotNodeProvider.switchTopic(bb, topicFallbackConfluence).eval();
				});

	}

}
