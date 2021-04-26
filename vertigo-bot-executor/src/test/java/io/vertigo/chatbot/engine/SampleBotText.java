package io.vertigo.chatbot.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import io.vertigo.ai.AiFeatures;
import io.vertigo.ai.command.BtCommandManager;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotBtCommandParserDefinitionProvider;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.connectors.redis.RedisFeatures;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.param.Param;
import io.vertigo.core.util.InjectorUtil;

public class SampleBotText {

	@Inject
	private BotManager botManager;
	@Inject
	private BtCommandManager btCommandManager;

	private static BotEngine botEngine;

	private final String botText = "" +
			"\n	begin sequence" +
			"\n		inputString /u/name \"Hello I'm Alan what is your name ?\"" +
			"\n		begin selector" +
			"\n			eq /i/name X" +
			"\n			begin sequence" +
			"\n				inputString /i/name \"Hi {{/u/name}} please select [W]eather, [X]icket, [G]ame or e[X]it ?\" W G X" +
			"\n				begin selector" +
			"\n					fulfilled /i/done" +
			"\n					begin switch /i/name" +
			"\n						begin case W" +
			"\n							inputString /w/city \"Please choose a city\"" +
			"\n							say \"It's sunny in {{/w/city}} !\"" +
			"\n							set /i/done ok" +
			"\n							remove /w/*" +
			"\n						end case" +
			"\n						say \"Choice not supported yet\"" +
			"\n						remove /i/name" +
			"\n					end switch" +
			"\n				end selector" +
			"\n				inputString /rate/rating \"Please rate the response [0, 1, 2, 3, 4, 5]\" 0 1 2 3 4 5" +
			"\n				say \"You have rated {{/rate/rating}}\"" +
			"\n				remove /rate/*" +
			"\n				remove /i/*" +
			"\n			end sequence" +
			"\n		end selector" +
			"\n		say \"bye bye {{/u/name}}\"" +
			"\n	end sequence";

	public static void main(final String[] args) {
		try (AutoCloseableNode node = new AutoCloseableNode(buildNodeConfig())) {
			final SampleBotText sampleBot = InjectorUtil.newInstance(SampleBotText.class);
			sampleBot.runInConsole();
		}

	}

	BotResponse runTick(final BotEngine botEngine, final BotInput userInput) {
		final BotResponse botResponse = botEngine.runTick(userInput);
		System.out.println(String.join("\n", botResponse.getHtmlTexts()));
		return botResponse;
	}

	void runInConsole() {
		// create or parse or retrieve the brain
		final List<TopicDefinition> topics = new ArrayList<>();
		topics.add(TopicDefinition.of(BotEngine.START_TOPIC_NAME, btCommandManager.parse(botText)));
		botManager.updateConfig(topics);

		// create a botEngine that is bound to a specific context
		botEngine = botManager.createBotEngine();

		final Scanner sc = new Scanner(System.in);

		// init conversation
		BotResponse botResponse = runTick(botEngine, new BotInput());
		// run the rest
		while (botResponse.getStatus() != BotStatus.Ended) {
			System.out.println(">>running *************************");
			final var userResponse = sc.nextLine();
			botResponse = runTick(botEngine, new BotInput(userResponse));
		}
		sc.close();
		System.out.println(">> end ***********************");
	}

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.addModule(new RedisFeatures()
						.withJedis(
								Param.of("host", "docker-vertigo.part.klee.lan.net"),
								Param.of("port", 6379),
								Param.of("database", 0))
						.build())
				.addModule(new CommonsFeatures().build())// for transactions
				.addModule(
						new AiFeatures()
								.withBlackboard()
								.withRedisBlackboard()
								.withNLU()
								.addPlugin(MockNluEnginePlugin.class)
								.withParser()
								.build())
				.addModule(ModuleConfig.builder("bot-module")
						.addComponent(BotManager.class, BotManagerImpl.class)
						.addDefinitionProvider(BotBtCommandParserDefinitionProvider.class)
						.build())
				.build();
	}

}
