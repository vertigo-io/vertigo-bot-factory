package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingExport;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.designer.builder.services.topic.MeaningServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topic.export.ExportPAO;
import io.vertigo.chatbot.designer.domain.TupleSynonymIhm;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class TopicExportServices implements Component, Activeable {

	@Inject
	private TopicServices topicServices;

	private final Set<TopicExportInterfaceServices> topicExportInterfaceServices = new HashSet();

	@Inject
	private SmallTalkExportServices smallTalkExportServices;

	@Inject
	private ScriptIntentionExportServices scriptIntentionExportServices;

	@Inject
	private MeaningServices meaningServices;

	@Inject
	private ExportPAO exportPAO;

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

	/*
	 * Generate all combinaison of nlu training sentences with synonyms
	 */
	public DtList<NluTrainingExport> generateNLUSynonyms(final Long botId) {
		final DtList<NluTrainingExport> listNluExit = new DtList<>(NluTrainingExport.class);
		for (final NluTrainingExport nluOriginal : exportPAO.exportRelativeTrainingSentence(botId)) {

			//Creation des combinaisons
			final List<String> listText = generateSentencesWithSynonyms(nluOriginal, botId);

			for (final String text : listText) {
				final NluTrainingExport nlu = new NluTrainingExport();
				nlu.setText(text.trim());
				nlu.setTopId(nluOriginal.getTopId());
				listNluExit.add(nlu);
			}

		}
		return listNluExit;

	}

	/**
	 * Return sentences generated from original nlu training sentences, with all synonyms
	 *
	 * @param nluOriginal
	 * @param botId
	 * @return
	 */
	public ArrayList<String> generateSentencesWithSynonyms(final NluTrainingExport nluOriginal, final Long botId) {
		// Word are separated by space or ponctuation

		final List<String> listWord = Stream.of(nluOriginal.getText().split("([.,!?:;'\"-]|\\s)+"))
				.collect(Collectors.toList());

		// get a list of Tuple <word, synonym> from the original sentence
		final DtList<TupleSynonymIhm> listTupleSynonymIhm = meaningServices.getTuplesSynonym(listWord, botId);

		// group the result by original word
		final Map<String, List<TupleSynonymIhm>> tupleSynonymIhmPerWord = listTupleSynonymIhm.stream()
				.collect(Collectors.groupingBy(TupleSynonymIhm::getWord));

		//Generation of all possible combinaison from the original sentences and the synonyms found
		return meaningServices.combine(tupleSynonymIhmPerWord, nluOriginal.getText());
	}

	/**
	 * Export topics to be trained
	 *
	 * @param bot
	 * @return list Topic Export
	 */
	public DtList<TopicExport> exportActiveTopics(final Chatbot bot) {
		final DtList<TopicExport> result = new DtList<>(TopicExport.class);
		final DtList<NluTrainingExport> nlus = generateNLUSynonyms(bot.getBotId());
		for (final TopicExportInterfaceServices services : topicExportInterfaceServices) {
			final DtList<Topic> topics = services.getAllNonTechnicalAndActiveTopicByBot(bot, services.getHandleObject());
			final Map<Long, String> mapTopicBt = services.mapTopicToBt(bot);
			result.addAll(TopicsExportUtils.mapTopicsToNluTrainingSentences(topics, nlus, mapTopicBt));
		}

		return result;
	}

}
