package io.vertigo.chatbot.engine;

import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.ai.bt.BTNodes.succeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import io.vertigo.ai.bb.BlackBoardManager;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTNodes;
import io.vertigo.ai.bt.BehaviorTreeManager;
import io.vertigo.ai.impl.bb.BlackBoardManagerImpl;
import io.vertigo.ai.impl.bt.BehaviorTreeManagerImpl;
import io.vertigo.ai.impl.nlu.NluManagerImpl;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.plugins.bb.memory.MemoryBlackBoardStorePlugin;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.commons.CommonsFeatures;
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
		topics.add(TopicDefinition.ofWithBotNodeProvider("START", botNodeProvider -> {
			return sequence(
					botNodeProvider.say("It works"),
					botNodeProvider.inputString("u/name", "Hello I'm Alan what is your name ?"),
					//intents
					main(botNodeProvider),
					botNodeProvider.say("bye bye {{u/name}}"));
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
			botResponse = runTick(botEngine, BotInput.of(userResponse));
		}
		sc.close();
		System.out.println(">> end ***********************");
	}

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.addModule(new CommonsFeatures().build())// for transactions
				.addModule(
						ModuleConfig.builder("myModule")
								.addComponent(BlackBoardManager.class, BlackBoardManagerImpl.class)
								.addPlugin(MemoryBlackBoardStorePlugin.class)
								.addComponent(BehaviorTreeManager.class, BehaviorTreeManagerImpl.class)
								.addComponent(BotManager.class, BotManagerImpl.class)
								.addComponent(NluManager.class, NluManagerImpl.class)
								.addPlugin(MockNluEnginePlugin.class)
								.build())
				.build();
	}

	private static BTNode main(final BotNodeProvider botNodeProvider) {
		return selector(
				botNodeProvider.eq("i/name", "X"),
				sequence(
						//botEngine.clear("i/*"),
						//botEngine.clear("rate/*"),
						//						botEngine.fulfill("i/name", "Hi {{u/name}} please select [W]eather, [T]icket, [G]ame or e[X]it ?", "W", "G", "T", "X"),
						botNodeProvider.inputString("i/name", "Hi {{u/name}} please select [W]eather, [X]icket, [G]ame or e[X]it ?", "W", "G", "X"),
						selector(
								botNodeProvider.fulfilled("i/done"),
								botNodeProvider.doSwitch("i/name")
										.when("W", weather(botNodeProvider))
										.when("G", game(botNodeProvider))
										//								.when("T", ticket())
										.build()),
						rate(botNodeProvider),
						botNodeProvider.remove("i/*"),
						botNodeProvider.remove("rate/*")));
	}

	private static BTNode weather(final BotNodeProvider botNodeProvider) {
		return sequence(
				botNodeProvider.inputString("w/city", "Please choose a city"),
				botNodeProvider.say("It's sunny in {{w/city}} !"),
				botNodeProvider.set("i/done", "ok"),
				botNodeProvider.remove("w/*"));
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

	private static BTNode game(final BotNodeProvider botNodeProvider) {
		return sequence(
				//first select a random number between 0 and 100
				selector(
						botNodeProvider.fulfilledInteger("g/target"),
						sequence(
								botNodeProvider.say("You have chosen to play !"),
								botNodeProvider.say("{{u/name}}, you must find the number I have chosen between 0 and 100"),
								botNodeProvider.set("g/target",
										Double.valueOf(Math.floor(Math.random() * 101)).intValue()))),
				//make your choice until having found the right number
				selector(
						botNodeProvider.eqIntegerByValue("g/target", "g/choice"),
						sequence(
								botNodeProvider.inputInteger("g/choice", "What is your choice ?"),
								botNodeProvider.incr("g/rounds"),
								selector(
										sequence(
												botNodeProvider.gtByValue("g/target", "g/choice"),
												botNodeProvider.say("select up !"),
												botNodeProvider.remove("g/choice"),
												BTNodes.running()),
										sequence(
												botNodeProvider.ltByValue("g/target", "g/choice"),
												botNodeProvider.say("select down !"),
												botNodeProvider.remove("g/choice"),
												BTNodes.running()),
										succeed()))),
				//The right number has been found
				botNodeProvider.say("Bravo {{u/name}} you have found the right number {{g/target}} in {{g/rounds}} rounds"),
				botNodeProvider.set("i/done", "ok"),
				botNodeProvider.remove("g/*"));
	}

	private static BTNode rate(final BotNodeProvider botNodeProvider) {
		return sequence(
				botNodeProvider.inputString("rate/rating", "Please rate the response [0, 1, 2, 3, 4, 5]", "0", "1", "2", "3", "4", "5"),
				botNodeProvider.say("You have rated {{rate/rating}}"),
				botNodeProvider.remove("rate/*"));
	}
}
