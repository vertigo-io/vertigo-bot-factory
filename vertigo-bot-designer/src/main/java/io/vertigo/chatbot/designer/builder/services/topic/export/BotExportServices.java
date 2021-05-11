package io.vertigo.chatbot.designer.builder.services.topic.export;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class BotExportServices implements Component {

	@Inject
	private SmallTalkExportServices smallTalkExportServices;

	@Inject
	private ScriptIntentionExportServices scriptIntentionExportServices;

	public BotExport exportBot(final Chatbot bot) {
		final BotExport export = new BotExport();
		export.setBot(bot);
		export.setTopics(exportActiveTopics(bot));
		export.setFallbackBT(smallTalkExportServices.getBasicBt(bot, KindTopicEnum.FAILURE.name()));
		export.setEndBT(smallTalkExportServices.getBasicBt(bot, KindTopicEnum.END.name()));
		export.setWelcomeBT(smallTalkExportServices.getBasicBt(bot, KindTopicEnum.START.name()));
		return export;
	}

	private DtList<TopicExport> exportActiveTopics(final Chatbot bot) {
		final DtList<TopicExport> result = new DtList<>(TopicExport.class);
		result.addAll(smallTalkExportServices.exportTopics(bot));
		result.addAll(scriptIntentionExportServices.exportTopics(bot));
		return result;
	}

}
