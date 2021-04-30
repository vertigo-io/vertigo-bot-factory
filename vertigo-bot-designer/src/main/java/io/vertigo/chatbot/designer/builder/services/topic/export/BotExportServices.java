package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class BotExportServices implements Component {

	@Inject
	private List<TopicsExportServices> services;

	public BotExport exportBot(final Chatbot bot) {
		final BotExport export = new BotExport();
		export.setBot(bot);
		export.setTopics(exportActiveTopics(bot));
		export.setFallbackBT("");
		export.setEndBT("");
		export.setWelcomeBT("");
		return export;
	}

	private DtList<TopicExport> exportActiveTopics(final Chatbot bot) {
		final DtList<TopicExport> result = new DtList<>(TopicExport.class);
		for (final TopicsExportServices service : services) {
			result.addAll(service.exportTopics(bot));
		}
		return result;
	}

}
