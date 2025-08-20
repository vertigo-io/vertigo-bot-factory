package io.vertigo.chatbot.executor.globalVariables;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.set;

public class BtNodeGlobalVariableProvider implements Component {

    @Inject
    private ExecutorManager executorManager;

    private static final Logger LOGGER = LogManager.getLogger(BtNodeGlobalVariableProvider.class);

    public BTNode setGlobalVariableValue(final BlackBoard bb, final String keyTemplate, String type, String param1, String param2, String param3, String param4) {
        try {
            return set(bb, keyTemplate, executorManager.getGlobalVariableValue(type,
                    param1 != null ? bb.format(param1) : null,
                    param2 != null ? bb.format(param2) : null,
                    param3 != null ? bb.format(param3) : null,
                    param4 != null ? bb.format(param4) : null));
        } catch (final VSystemException vSystemException) {
            LOGGER.error("Error when fetching value for global variable with type {}", type, vSystemException);
            return () -> BTStatus.Failed;
        }
    }
}
