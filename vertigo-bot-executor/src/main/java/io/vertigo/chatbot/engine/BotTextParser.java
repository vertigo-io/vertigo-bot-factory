package io.vertigo.chatbot.engine;

import java.util.Random;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTNodes;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.Assertion;

public final class BotTextParser {
	private final BlackBoard bb;

	private BotTextParser(final BlackBoard blackBoard) {
		Assertion.check()
				.isNotNull(blackBoard);
		// ---
		bb = blackBoard;
	}

	public static BTNode stringToBTRoot(final BlackBoard blackBoard, final String in) {
		final var me = new BotTextParser(blackBoard);
		return me.stringToBTRoot(in);
	}

	private BTNode stringToBTRoot(final String in) {
		final String[] respMockList = { "Euuu...", "Joker 😅", "C'est pas faux", "Hum 🤔", "Ca c'est bien vrai !" };
		final int rnd = new Random().nextInt(respMockList.length);
		final String respMock = respMockList[rnd];

		final var botNodeProvider = new BotNodeProvider(bb);

		return BTNodes.sequence(botNodeProvider.say(respMock), () -> BTStatus.Running);
	}
}
