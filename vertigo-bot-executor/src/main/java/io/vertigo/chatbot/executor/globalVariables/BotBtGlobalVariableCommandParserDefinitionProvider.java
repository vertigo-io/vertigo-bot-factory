package io.vertigo.chatbot.executor.globalVariables;

import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import io.vertigo.core.node.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.chatbot.engine.util.BlackBoardUtils.getBB;

public class BotBtGlobalVariableCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

    private static final Logger LOGGER = LogManager.getLogger(BotBtGlobalVariableCommandParserDefinitionProvider.class);

    @Inject
    private BtNodeGlobalVariableProvider nodeGlobalVariableProvider;

    @Override
    public List<? extends Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
        LOGGER.info("loading Global variables grammar");
        return List.of(
                BtCommandParserDefinition.basicCommand("setvar", (c, p) ->
                        nodeGlobalVariableProvider.setGlobalVariableValue(getBB(p),
                                c.getStringParam(0),
                                c.getStringParam(1),
                                c.getStringParam(2),
                                c.getOptStringParam(3).orElse(null),
                                c.getOptStringParam(4).orElse(null),
                                c.getOptStringParam(5).orElse(null))));
    }
}
