package io.vertigo.chatbot.engine.plugins.bt.command.bot.file;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;

public class BtNodeFileProvider implements Component {

	public BTNode addImageFileNode(final BlackBoard bb, final String label) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, formatImageFile(label));
			return BTStatus.Succeeded;
		};
	}

	public BTNode addFileNode(final BlackBoard bb, final String label) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, formatFile(label));
			return BTStatus.Succeeded;
		};
	}

	private static String formatImageFile(final String label) {
		Assertion.check().isNotNull(label);
		return "<img src='../../api/chatbot/getAttachment?label=" + label + "' class='imgClass' />";
	}

	private static String formatFile(final String label) {
		Assertion.check().isNotNull(label);
		return "<a href='../../api/chatbot/getAttachment?label=" + label + "' target='_blank'>" + label + "</a>";
	}

}
