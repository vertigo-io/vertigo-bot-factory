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

	public BotExport exportBot(final Chatbot bot, final StringBuilder logs) {
		final BotExport export = new BotExport();
		export.setBot(bot);
		logs.append("Active topics export\r\n");
		export.setTopics(topicExportServices.exportActiveTopics(bot, logs));
		logs.append("Failure topic export...");
		export.setFallbackBT(topicExportServices.getBasicBt(bot, KindTopicEnum.FAILURE.name()));
		logs.append("OK\r\n");
		logs.append("End topic export...");
		export.setEndBT(topicExportServices.getBasicBt(bot, KindTopicEnum.END.name()));
		logs.append("OK\r\n");
		logs.append("Start topic export...");
		export.setWelcomeBT(topicExportServices.getBasicBt(bot, KindTopicEnum.START.name()));
		logs.append("OK\r\n");
		return export;
	}

}
