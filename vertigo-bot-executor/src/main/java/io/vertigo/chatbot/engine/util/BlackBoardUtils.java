package io.vertigo.chatbot.engine.util;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.core.lang.VSystemException;

import java.util.List;

public final class BlackBoardUtils {

	public static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("No BlackBoard found"));
	}
}
