package io.vertigo.chatbot.executor.welcometour;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.launchJsEvent;

public class BtNodeWelcomeTourProvider implements Component {

	@Inject
	private ExecutorManager executorManager;

	private static final Logger LOGGER = LogManager.getLogger(BtNodeWelcomeTourProvider.class);

	public BTNode startWelcomeTour(final BlackBoard bb, String welcomeTourLabel) {
		try {
			return launchJsEvent(bb, executorManager.getWelcomeTourTechnicalCode(welcomeTourLabel));
		} catch (VSystemException vSystemException) {
			LOGGER.error("Error when starting Welcome tour with label " + welcomeTourLabel, vSystemException);
			return () -> BTStatus.Failed;
		}
	}
}
