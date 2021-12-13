package io.vertigo.chatbot.engine.plugins.bt.welcometour.command.bot;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.core.node.component.Component;

import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.inputString;

public final class BotWelcomeTourNodeProvider implements Component {

	public BTNode welcomeTour(final BlackBoard bb, final String welcomeTourName) {
		return sequence(
				() -> {
					bb.putString(BBKey.of(BotEngine.BOT_OUT_METADATA_PATH, "/welcometour"), welcomeTourName);
					return BTStatus.Succeeded;
				});

	}

}
