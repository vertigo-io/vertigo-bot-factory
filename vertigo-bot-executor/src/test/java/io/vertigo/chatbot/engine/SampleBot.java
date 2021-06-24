package io.vertigo.chatbot.engine;

import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.ai.bt.BTNodes.succeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.inject.Inject;

import io.vertigo.ai.AiFeatures;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
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
		final BotResponse botResponse = botEngine.runTick(userInput, Optional.empty());
		System.out.println(String.join("\n", botResponse.getHtmlTexts()));
		return botResponse;
	}

	void runInConsole() {
		// create or parse or retrieve the brain
		final List<TopicDefinition> topics = new ArrayList<>();
		topics.add(TopicDefinition.of(BotEngine.START_TOPIC_NAME, params -> {
			final var bb = getBlackBoard(params);
			return sequence(
					//botNodeProvider.say("It works"),
					BotNodeProvider.inputString(bb, "/u/name", "Hello I'm Alan what is your name ?"),
					//intents
					main(bb),
					BotNodeProvider.say(bb, "bye bye {{/u/name}}"));
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

	private static BlackBoard getBlackBoard(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("A BlackBoard is needed in parameters."));
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

	private static BTNode main(final BlackBoard bb) {
		return selector(
				BotNodeProvider.eq(bb, "/i/name", "X"),
				sequence(
						//botEngine.clear("i/*"),
						//botEngine.clear("rate/*"),
						//						botEngine.fulfill("i/name", "Hi {{u/name}} please select [W]eather, [T]icket, [G]ame or e[X]it ?", "W", "G", "T", "X"),
						BotNodeProvider.inputString(bb, "/i/name", "Hi {{/u/name}} please select [W]eather, [X]icket, [G]ame or e[X]it ?", "W", "G", "X"),
						selector(
								BotNodeProvider.fulfilled(bb, "/i/done"),
								BotNodeProvider.doSwitch(bb, "/i/name")
										.when("W", weather(bb))
										.when("G", game(bb))
										//								.when("T", ticket())
										.build()),
						rate(bb),
						BotNodeProvider.remove(bb, "/i/*"),
						BotNodeProvider.remove(bb, "/rate/*")));
	}

	private static BTNode weather(final BlackBoard bb) {
		return sequence(
				BotNodeProvider.inputString(bb, "/w/city", "Please choose a city"),
				BotNodeProvider.say(bb, "It's sunny in {{/w/city}} !"),
				BotNodeProvider.set(bb, "/i/done", "ok"),
				BotNodeProvider.remove(bb, "/w/*"));
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

	private static BTNode game(final BlackBoard bb) {
		return sequence(
				//first select a random number between 0 and 100
				selector(
						BotNodeProvider.fulfilledInteger(bb, "/g/target"),
						sequence(
								BotNodeProvider.say(bb, "You have chosen to play !"),
								BotNodeProvider.say(bb, "{{/u/name}}, you must find the number I have chosen between 0 and 100"),
								BotNodeProvider.set(bb, "/g/target",
										Double.valueOf(Math.floor(Math.random() * 101)).intValue()))),
				//make your choice until having found the right number
				selector(
						BotNodeProvider.eqIntegerByValue(bb, "/g/target", "/g/choice"),
						sequence(
								BotNodeProvider.inputInteger(bb, "/g/choice", "What is your choice ?"),
								BotNodeProvider.incr(bb, "/g/rounds"),
								selector(
										sequence(
												BotNodeProvider.gtByValue(bb, "/g/target", "/g/choice"),
												BotNodeProvider.remove(bb, "/g/choice"),
												BotNodeProvider.inputInteger(bb, "/g/choice", "select up !")),
										sequence(
												BotNodeProvider.ltByValue(bb, "/g/target", "/g/choice"),
												BotNodeProvider.remove(bb, "/g/choice"),
												BotNodeProvider.inputInteger(bb, "/g/choice", "select down !")),
										succeed()))),
				//The right number has been found
				BotNodeProvider.say(bb, "Bravo {{/u/name}} you have found the right number {{/g/target}} in {{/g/rounds}} rounds"),
				BotNodeProvider.set(bb, "/i/done", "ok"),
				BotNodeProvider.remove(bb, "/g/*"));
	}

	private static BTNode rate(final BlackBoard bb) {
		return sequence(
				BotNodeProvider.inputString(bb, "/rate/rating", "Please rate the response [0, 1, 2, 3, 4, 5]", "0", "1", "2", "3", "4", "5"),
				BotNodeProvider.say(bb, "You have rated {{/rate/rating}}"),
				BotNodeProvider.remove(bb, "/rate/*"));
	}
}
