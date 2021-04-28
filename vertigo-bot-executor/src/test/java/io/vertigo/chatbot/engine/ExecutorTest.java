package io.vertigo.chatbot.engine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.AiFeatures;
import io.vertigo.chatbot.commons.ChatbotCommonsFeatures;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.executor.ExecutorFeatures;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.core.plugins.resource.url.URLResourceResolverPlugin;
import io.vertigo.datamodel.DataModelFeatures;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.vega.VegaFeatures;

public class ExecutorTest {

	@Inject
	private ExecutorManager executorManager;

	private AutoCloseableNode node;

	private final String botWelcome = "" +
			"\n	begin sequence" +
			"\n		say:once \"Welcome to this test bot.\"" +
			"\n		begin choose:button " + BotEngine.USER_LOCAL_PATH.key() + "/topic \"Please choose topic.\"" +
			"\n			button \"Le topic 1\" topic1" +
			"\n			button \"Le topic 2\" topic2" +
			"\n		end choose:button" +
			"\n		topic {{" + BotEngine.USER_LOCAL_PATH.key() + "/topic}}" +
			"\n	end sequence";

	private final String botTopic1 = "" +
			"\n	begin sequence" +
			"\n		say \"This is topic 1\"" +
			"\n		inputString /user/global/name \"What is your name ?\"" +
			"\n	end sequence";

	private final String botTopic2 = "" +
			"\n	begin sequence" +
			"\n		say \"This is topic 2\"" +
			"\n		inputString /user/global/city \"What is your city ?\"" +
			"\n	end sequence";

	private final String botTopic3 = "" +
			"\n	begin sequence" +
			"\n		say \"This is topic 3\"" +
			"\n	end sequence";

	private final String botEnd = "" +
			"\n	begin selector" +
			"\n		begin sequence" +
			"\n			fulfilled /user/global/name" +
			"\n			say \"Nice to meet you {{/user/global/name}}\"" +
			"\n		end sequence" +
			"\n		begin sequence" +
			"\n			fulfilled /user/global/city" +
			"\n			say \"I wish to visit {{/user/global/city}} someday\"" +
			"\n		end sequence" +
			"\n		say \"Byebye\"" +
			"\n	end selector";

	private final String botWelcomeNlu = "" +
			"\n	begin sequence" +
			"\n		say:once \"Welcome to this test bot.\"" +
			"\n		choose:nlu \"Please ask topic.\"" +
			"\n	end sequence";

	private final String botFallback = "" +
			"\n	begin sequence" +
			"\n		say \"Sorry, try again.\"" +
			"\n	end sequence";

	private final List<String> topic1Nlu = List.of(
			"I love this topic",
			"this topic is awesome",
			"go to my topic please",
			"the best topic is this topic");

	private final List<String> topic2Nlu = List.of(
			"the other topic sucks",
			"i dont want this topic",
			"this is not good",
			"please dont go there");

	private final List<String> topic3Nlu = List.of(
			"the sun is shining",
			"i like to swim",
			"holydays are great",
			"i like holydays",
			"I like to swim on holydays");

	@Test
	public void testTopicButton() {
		buildBot(botWelcome, botEnd, botTopic1, botTopic2);

		BotResponse response = executorManager.startNewConversation(new BotInput());
		var textResponses = response.getHtmlTexts();
		final UUID uuid = (UUID) response.getMetadatas().get("sessionId");

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("Welcome to this test bot.", textResponses.get(0));
		Assertions.assertEquals("Please choose topic.", textResponses.get(1));
		Assertions.assertEquals(2, response.getChoices().size());
		Assertions.assertFalse(response.getMetadatas().containsKey("accepttext"));
		Assertions.assertEquals("BotButton", response.getMetadatas().get("buttontype"));
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput(Map.of("payload", "topic1")));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("This is topic 1", textResponses.get(0));
		Assertions.assertEquals("What is your name ?", textResponses.get(1));
		Assertions.assertTrue(response.getMetadatas().containsKey("accepttext"));
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput("John"));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Ended, response.getStatus());
		Assertions.assertEquals(1, textResponses.size());
		Assertions.assertEquals("Nice to meet you John", textResponses.get(0));
		Assertions.assertFalse(response.getMetadatas().containsKey("accepttext"));
	}

	@Test
	public void testTopicNlu() {
		buildBot(botWelcomeNlu, botEnd, botFallback, List.of(
				Tuple.of(botTopic1, topic1Nlu),
				Tuple.of(botTopic2, topic2Nlu),
				Tuple.of(botTopic3, topic3Nlu)));

		BotResponse response = executorManager.startNewConversation(new BotInput());
		var textResponses = response.getHtmlTexts();
		final UUID uuid = (UUID) response.getMetadatas().get("sessionId");

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("Welcome to this test bot.", textResponses.get(0));
		Assertions.assertEquals("Please ask topic.", textResponses.get(1));
		Assertions.assertTrue(response.getMetadatas().containsKey("accepttext"));
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput("i dunno"));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("Sorry, try again.", textResponses.get(0));
		Assertions.assertEquals("Please ask topic.", textResponses.get(1));
		Assertions.assertTrue(response.getMetadatas().containsKey("accepttext"));
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput("i still dunno"));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("Sorry, try again.", textResponses.get(0));
		Assertions.assertEquals("Please ask topic.", textResponses.get(1));
		Assertions.assertTrue(response.getMetadatas().containsKey("accepttext"));
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput("go to my topic please"));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("This is topic 1", textResponses.get(0));
		Assertions.assertEquals("What is your name ?", textResponses.get(1));
		Assertions.assertTrue(response.getMetadatas().containsKey("accepttext"));
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput("John"));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Ended, response.getStatus());
		Assertions.assertEquals(1, textResponses.size());
		Assertions.assertEquals("Nice to meet you John", textResponses.get(0));
		Assertions.assertFalse(response.getMetadatas().containsKey("accepttext"));
	}

	private void buildBot(final String welcome, final String end, final String... topics) {
		final List<Tuple<String, List<String>>> resolvedTopics = new ArrayList<>();

		for (final String topic : topics) {
			resolvedTopics.add(Tuple.of(topic, null));
		}

		buildBot(welcome, end, null, resolvedTopics);
	}

	private void buildBot(final String welcome, final String end, final String fallback, final List<Tuple<String, List<String>>> topics) {
		final var botExport = new BotExport();
		botExport.setWelcomeBT(welcome);
		botExport.setEndBT(end);
		if (fallback != null) {
			botExport.setFallbackBT(fallback);
		}

		final var topicsExport = new DtList<TopicExport>(TopicExport.class);
		int cpt = 1;
		for (final Tuple<String, List<String>> topicDef : topics) {
			final var topicExport = new TopicExport();
			topicExport.setName("topic" + cpt);
			topicExport.setTopicBT(topicDef.getVal1());
			if (topicDef.getVal2() != null) {
				topicExport.setNluTrainingSentences(topicDef.getVal2());
			}
			topicsExport.add(topicExport);
			cpt++;
		}

		botExport.setTopics(topicsExport);

		final var executorConfig = new ExecutorConfiguration();
		executorConfig.setNluThreshold(BigDecimal.valueOf(0.6));

		executorManager.loadModel(botExport, executorConfig);
	}

	@BeforeEach
	public final void setUp() throws Exception {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
	}

	@AfterEach
	public final void tearDown() throws Exception {
		if (node != null) {
			node.close();
		}
	}

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.addPlugin(URLResourceResolverPlugin.class)
						.build())
				.addModule(new CommonsFeatures().build())// for transactions
				.addModule(new DataModelFeatures().build())// for json engine
				.addModule(new VegaFeatures().build())// for json engine
				.addModule(
						new AiFeatures()
								.withBlackboard()
								.withMemoryBlackboard()
								.withNLU()
								.addPlugin(MockNluEnginePlugin.class)
								.withParser()
								.build())
				.addModule(new EngineFeatures().build())
				.addModule(new ChatbotCommonsFeatures().build())
				.addModule(new ExecutorFeatures().build())
				.build();
	}

}
