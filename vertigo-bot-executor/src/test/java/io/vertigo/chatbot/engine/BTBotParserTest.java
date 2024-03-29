package io.vertigo.chatbot.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.AiFeatures;
import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bb.BlackBoardManager;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.ai.command.BtCommandManager;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotBtCommandParserDefinitionProvider;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;

public class BTBotParserTest {

	@Inject
	private BtCommandManager btCommandManager;

	@Inject
	private BlackBoardManager blackBoardManager;

	private AutoCloseableNode node;

	@BeforeEach
	public final void setUp() throws Exception {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
	}

	private NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.addModule(
						new AiFeatures()
								.withParser()
								.withBlackboard()
								.withMemoryBlackboard()
								.build())
				.addModule(ModuleConfig.builder("myApp")
						.addDefinitionProvider(BotBtCommandParserDefinitionProvider.class)
						.build())
				.build();
	}

	@AfterEach
	public final void tearDown() throws Exception {
		if (node != null) {
			node.close();
		}
	}

	@Test
	public void testSay() {
		final String bt = "begin sequence\n" +
				"say \"bonjour !\"\n" +
				"end sequence";

		final Function<List<Object>, BTNode> nodeProducer = btCommandManager.parse(bt);

		final BlackBoard blackBoard = blackBoardManager.connect(BBKey.of("/test"));
		final BTNode rootNode = nodeProducer.apply(List.of(blackBoard));
		final BTStatus status = rootNode.eval();
		//---
		Assertions.assertEquals(BTStatus.Succeeded, status);
		Assertions.assertEquals(1, blackBoard.listSize(BotEngine.BOT_RESPONSE_KEY), "Expected only one say");
		Assertions.assertEquals("bonjour !", blackBoard.listGet(BotEngine.BOT_RESPONSE_KEY, 0));
	}

	@Test
	public void testSwitch() {
		final String bt = "begin switch /val\n" +
				"	begin case 1\n" +
				"		say case1\n" +
				"	end case\n" +
				"	begin case 2\n" +
				"		say case2\n" +
				"	end case\n" +
				"	say case3\n" +
				"end switch";

		final Function<List<Object>, BTNode> nodeProducer = btCommandManager.parse(bt);

		final BlackBoard blackBoard = blackBoardManager.connect(BBKey.of("/test"));

		blackBoard.putString(BBKey.of("/val"), "2");
		BTNode rootNode = nodeProducer.apply(List.of(blackBoard));
		BTStatus status = rootNode.eval();
		Assertions.assertEquals(BTStatus.Succeeded, status);
		Assertions.assertEquals(1, blackBoard.listSize(BotEngine.BOT_RESPONSE_KEY), "Expected only one say");
		Assertions.assertEquals("case2", blackBoard.listGet(BotEngine.BOT_RESPONSE_KEY, 0));

		blackBoard.delete(BBKeyPattern.of("/*"));

		blackBoard.putString(BBKey.of("/val"), "42");
		rootNode = nodeProducer.apply(List.of(blackBoard));
		status = rootNode.eval();
		Assertions.assertEquals(BTStatus.Succeeded, status);
		Assertions.assertEquals(1, blackBoard.listSize(BotEngine.BOT_RESPONSE_KEY), "Expected only one say");
		Assertions.assertEquals("case3", blackBoard.listGet(BotEngine.BOT_RESPONSE_KEY, 0));
	}

	@Test
	public void testNoCompositeRoot() {
		final String bt = "say bonjour\n";

		final BlackBoard blackBoard = blackBoardManager.connect(BBKey.of("/test"));

		final Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
			final Function<List<Object>, BTNode> nodeProducer = btCommandManager.parse(bt);
			nodeProducer.apply(List.of(blackBoard));
		});

		assertEquals("Root level only accepts composite nodes", exception.getMessage());
	}

	@Test
	public void testCaseAlone() {
		final String bt = "begin sequence\n" +
				"begin case 1\n" +
				"end case\n" +
				"end sequence";

		final BlackBoard blackBoard = blackBoardManager.connect(BBKey.of("/test"));

		final Exception exception = Assertions.assertThrows(VSystemException.class, () -> {
			final Function<List<Object>, BTNode> nodeProducer = btCommandManager.parse(bt);
			final BTNode rootNode = nodeProducer.apply(List.of(blackBoard));
			rootNode.eval();
		});

		assertEquals("'case' must be inside 'switch'", exception.getMessage());
	}

	@Test
	public void testEqWithMultipleSpace() {
		final String bt = "begin sequence\n\n" +
				"	set   /val/2 2   \n" +
				"	begin selector\n" +
				"		eq /val/1    1\n" +
				"		set /val/2 99\n" +
				"	end selector\n" +
				"end sequence";

		final Function<List<Object>, BTNode> nodeProducer = btCommandManager.parse(bt);

		final BlackBoard blackBoard = blackBoardManager.connect(BBKey.of("/test"));
		blackBoard.putString(BBKey.of("/val/1"), "1");

		final BTNode rootNode = nodeProducer.apply(List.of(blackBoard));
		final BTStatus status = rootNode.eval();
		//---
		Assertions.assertEquals(BTStatus.Succeeded, status);
		Assertions.assertEquals("2", blackBoard.getString(BBKey.of("/val/2")));
	}

}
