package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;

@Transactional
public class TopicExportServices implements Component, Activeable {

	@Inject
	private TopicServices topicServices;

	private final Set<TopicExportInterfaceServices> topicExportInterfaceServices = new HashSet();

	@Inject
	private SmallTalkExportServices smallTalkExportServices;

	@Inject
	private ScriptIntentionExportServices scriptIntentionExportServices;

	@Override
	public void start() {
		topicExportInterfaceServices.add(scriptIntentionExportServices);
		topicExportInterfaceServices.add(smallTalkExportServices);
	}

	@Override
	public void stop() {
		//nothing
	}

	public String getBasicBt(final Chatbot bot, final String ktoCd) {
		final Topic topic = topicServices.getBasicTopicByBotIdKtoCd(bot.getBotId(), ktoCd);
		String basicBt = null;
		for (final TopicExportInterfaceServices services : topicExportInterfaceServices) {
			if (services.handleObject(topic)) {
				basicBt = services.getBasicBt(bot, ktoCd);
			}
		}
		return basicBt;
	}

}
