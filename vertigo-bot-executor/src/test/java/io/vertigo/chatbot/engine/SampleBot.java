package io.vertigo.chatbot.engine;

import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.ai.bt.BTNodes.succeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import io.vertigo.ai.AiFeatures;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BtBotDriver;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.util.InjectorUtil;

public class SampleBot {

	@Inject
	private BotManager botManager;

	private static BotEngine botEngine;

	public static void main(final String[] args) {
		try (AutoCloseableNode node = new AutoCloseableNode(buildNodeConfig())) {
			final SampleBot sampleBot = InjectorUtil.newInstance(SampleBot.class);
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
		topics.add(TopicDefinition.of(BotEngine.START_TOPIC_NAME, params -> {
			final BtBotDriver botDriver = getNodeProvider(params);
			return sequence(
					//botNodeProvider.say("It works"),
					botDriver.inputString("/u/name", "Hello I'm Alan what is your name ?"),
					//intents
					main(botDriver),
					botDriver.say("bye bye {{/u/name}}"));
		}));
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

	private static BtBotDriver getNodeProvider(final List<Object> params) {
		final var blackBoard = params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("A BackBoard is needed in parameters."));

		return new BtBotDriver(blackBoard);
	}

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.addModule(new CommonsFeatures().build())// for transactions
				.addModule(
						new AiFeatures()
								.withBlackboard()
								.withMemoryBlackboard()
								.withNLU()
								.addPlugin(MockNluEnginePlugin.class)
								.build())
				.addModule(ModuleConfig.builder("bot-module")
						.addComponent(BotManager.class, BotManagerImpl.class)
						.build())
				.build();
	}

	private static BTNode main(final BtBotDriver botDriver) {
		return selector(
				botDriver.eq("/i/name", "X"),
				sequence(
						//botEngine.clear("i/*"),
						//botEngine.clear("rate/*"),
						//						botEngine.fulfill("i/name", "Hi {{u/name}} please select [W]eather, [T]icket, [G]ame or e[X]it ?", "W", "G", "T", "X"),
						botDriver.inputString("/i/name", "Hi {{/u/name}} please select [W]eather, [X]icket, [G]ame or e[X]it ?", "W", "G", "X"),
						selector(
								botDriver.fulfilled("/i/done"),
								botDriver.doSwitch("/i/name")
										.when("W", weather(botDriver))
										.when("G", game(botDriver))
										//								.when("T", ticket())
										.build()),
						rate(botDriver),
						botDriver.remove("/i/*"),
						botDriver.remove("/rate/*")));
	}

	private static BTNode weather(final BtBotDriver botDriver) {
		return sequence(
				botDriver.inputString("/w/city", "Please choose a city"),
				botDriver.say("It's sunny in {{/w/city}} !"),
				botDriver.set("/i/done", "ok"),
				botDriver.remove("/w/*"));
	}

	//	private BTNode ticket() {
	//		return sequence(
	//				botEngine.say("You have chosen to book a ticket, I have some questions..."),
	//				botEngine.fulfill("t/return", "Do you want a return ticket  ? Y/N", "Y", "N"),
	//				botEngine.fulfill("t/from", "from ?"),
	//				botEngine.fulfill("t/to", "to ?"),
	//				botEngine.fulfill("t/count", "How many tickets ?",
	//						BTUtils.isInteger().and(s -> Integer.valueOf(s) > 0 && Integer.valueOf(s) < 10)),
	//				loopUntil(botEngine.eq("t/idx", "{{t/count}}"),
	//						botEngine.inc("t/idx"),
	//						botEngine.fulfill("t/{{t/idx}}/name", "What is the name of the {{t/idx}} person ?"),
	//						botEngine.say("The ticket {{t/idx}} is booked for {{t/{{t/idx}}/name}}")),
	//				botEngine.say("Thank you, your ticket from {{t/from}} to {{t/to}} for {{t/count}} persons will be sent..."),
	//				botEngine.clear("t/*"));
	//	}

	private static BTNode game(final BtBotDriver botDriver) {
		return sequence(
				//first select a random number between 0 and 100
				selector(
						botDriver.fulfilledInteger("/g/target"),
						sequence(
								botDriver.say("You have chosen to play !"),
								botDriver.say("{{/u/name}}, you must find the number I have chosen between 0 and 100"),
								botDriver.set("/g/target",
										Double.valueOf(Math.floor(Math.random() * 101)).intValue()))),
				//make your choice until having found the right number
				selector(
						botDriver.eqIntegerByValue("/g/target", "/g/choice"),
						sequence(
								botDriver.inputInteger("/g/choice", "What is your choice ?"),
								botDriver.incr("/g/rounds"),
								selector(
										sequence(
												botDriver.gtByValue("/g/target", "/g/choice"),
												botDriver.remove("/g/choice"),
												botDriver.inputInteger("/g/choice", "select up !")),
										sequence(
												botDriver.ltByValue("/g/target", "/g/choice"),
												botDriver.remove("/g/choice"),
												botDriver.inputInteger("/g/choice", "select down !")),
										succeed()))),
				//The right number has been found
				botDriver.say("Bravo {{/u/name}} you have found the right number {{/g/target}} in {{/g/rounds}} rounds"),
				botDriver.set("/i/done", "ok"),
				botDriver.remove("/g/*"));
	}

	private static BTNode rate(final BtBotDriver botDriver) {
		return sequence(
				botDriver.inputString("/rate/rating", "Please rate the response [0, 1, 2, 3, 4, 5]", "0", "1", "2", "3", "4", "5"),
				botDriver.say("You have rated {{/rate/rating}}"),
				botDriver.remove("/rate/*"));
	}
}
