package io.vertigo.chatbot.engine;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

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
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.datamodel.DataModelFeatures;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.vega.VegaFeatures;

public class ExecutorTest {

	@Inject
	private ExecutorManager executorManager;

	private AutoCloseableNode node;

	private final String botWelcome = "" +
			"\n	begin sequence" +
			"\n		say \"Welcome to this test bot.\"" +
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
			"\n	end selector";

	@Test
	public void testTopic1() {
		buildBot();

		BotResponse response = executorManager.startNewConversation(new BotInput());
		var textResponses = response.getHtmlTexts();
		final UUID uuid = (UUID) response.getMetadatas().get("sessionId");

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("Welcome to this test bot.", textResponses.get(0));
		Assertions.assertEquals("Please choose topic.", textResponses.get(1));
		Assertions.assertEquals(2, response.getChoices().size());
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput(Map.of("payload", "topic1")));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Talking, response.getStatus());
		Assertions.assertEquals(2, textResponses.size());
		Assertions.assertEquals("This is topic 1", textResponses.get(0));
		Assertions.assertEquals("What is your name ?", textResponses.get(1));
		//--
		response = executorManager.handleUserMessage(uuid, new BotInput("John"));
		textResponses = response.getHtmlTexts();

		Assertions.assertEquals(BotStatus.Ended, response.getStatus());
		Assertions.assertEquals(1, textResponses.size());
		Assertions.assertEquals("Nice to meet you John", textResponses.get(0));
	}

	private void buildBot() {
		final var botExport = new BotExport();
		botExport.setWelcomeBT(botWelcome);
		botExport.setEndBT(botEnd);

		final var topicExport1 = new TopicExport();
		topicExport1.setName("topic1");
		topicExport1.setTopicBT(botTopic1);

		final var topicExport2 = new TopicExport();
		topicExport2.setName("topic2");
		topicExport2.setTopicBT(botTopic2);

		botExport.setTopics(DtList.of(topicExport1, topicExport2));

		final var executorConfig = new ExecutorConfiguration();
		executorConfig.setNluThreshold(BigDecimal.valueOf(0.6));

		executorManager.loadModel(botExport, executorConfig);
	}

	@BeforeEach
	public final void setUp() throws Exception {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
	}

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
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
