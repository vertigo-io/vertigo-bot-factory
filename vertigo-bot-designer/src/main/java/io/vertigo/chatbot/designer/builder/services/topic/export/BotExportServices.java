package io.vertigo.chatbot.designer.builder.services.topic.export;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;

@Transactional
public class BotExportServices implements Component {

	@Inject
	private TopicExportServices topicExportServices;

	public BotExport exportBot(final Chatbot bot) {
		final BotExport export = new BotExport();
		export.setBot(bot);
		export.setTopics(topicExportServices.exportActiveTopics(bot));
		export.setFallbackBT(topicExportServices.getBasicBt(bot, KindTopicEnum.FAILURE.name()));
		export.setEndBT(topicExportServices.getBasicBt(bot, KindTopicEnum.END.name()));
		export.setWelcomeBT(topicExportServices.getBasicBt(bot, KindTopicEnum.START.name()));
		return export;
	}

}
