package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingExport;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topic.export.ExportPAO;
import io.vertigo.chatbot.designer.builder.topic.export.ScriptIntentionExport;
import io.vertigo.chatbot.domain.DtDefinitions.ScriptIntentionFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class ScriptIntentionExportServices implements TopicExportInterfaceServices<ScriptIntention>, Component {

	@Inject
	private TopicServices topicServices;

	@Inject
	private ScriptIntentionServices scriptIntentionServices;

	@Inject
	private ExportPAO exportPAO;

	@Override
	public boolean handleObject(final Topic topic) {
		return TypeTopicEnum.SCRIPTINTENTION.name().equals(topic.getTtoCd());
	}

	@Override
	public DtList<TopicExport> exportTopics(final Chatbot bot) {
		final DtList<Topic> topics = topicServices.getAllTopicRelativeScriptIntentionByBot(bot);
		final DtList<NluTrainingExport> nlus = exportPAO.exportScriptIntentionRelativeTrainingSentence(bot.getBotId());
		final Map<Long, String> mapTopicBt = mapTopicToBt(bot);
		final DtList<TopicExport> exports = TopicsExportUtils.mapTopicsToNluTrainingSentences(topics, nlus, mapTopicBt);
		return exports;

	}

	@Override
	public Map<Long, String> mapTopicToBt(final Chatbot bot) {
		final DtList<ScriptIntentionExport> exports = exportPAO.getScriptIntentionExportByBotId(bot.getBotId());
		final Map<Long, String> result = new HashMap<>();
		for (final ScriptIntentionExport export : exports) {
			result.put(export.getTopId(), export.getBt());
		}
		return result;
	}

	public List<String> getColumnNameListToExport() {
		final List<String> columnNameList = new ArrayList<>();
		columnNameList.add(ScriptIntentionFields.topId.name());
		columnNameList.add(ScriptIntentionFields.sinId.name());
		columnNameList.add(ScriptIntentionFields.script.name());
		return columnNameList;
	}

	@Override
	public String getBasicBt(final Chatbot bot, final String ktoCd) {
		final Topic topic = topicServices.getBasicTopicByBotIdKtoCd(bot.getBotId(), ktoCd);
		final ScriptIntention scriptIntention = scriptIntentionServices.findByTopId(topic.getTopId());
		return scriptIntention.getScript();
	}

}
