package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingExport;
import io.vertigo.chatbot.commons.domain.topic.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.services.topic.MeaningServices;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topic.export.ExportPAO;
import io.vertigo.chatbot.designer.builder.topic.export.ResponseButtonExport;
import io.vertigo.chatbot.designer.builder.topic.export.UtterTextExport;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class SmallTalkExportServices implements TopicsExportServices, Component {

	@Inject
	private TopicServices topicServices;

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private MeaningServices meaningServices;

	@Inject
	private ExportPAO exportPAO;

	@Override
	public DtList<TopicExport> exportTopics(final Chatbot bot) {
		final DtList<Topic> topics = topicServices.getAllTopicRelativeSmallTalkByBot(bot);
		final DtList<NluTrainingExport> nlus = generateNLUSynonyms(bot.getBotId());
		System.out.println(nlus);
		//Create bt by topics and map to topId
		final Map<Long, String> mapBtTopic = mapTopicToBt(bot);
		//Map Topic to NLU and BT
		final DtList<TopicExport> result = TopicsExportUtils.mapTopicsToNluTrainingSentences(topics, nlus, mapBtTopic);
		return result;
	}

	private Map<UtterTextExport, List<ResponseButtonExport>> getUtterAndResponseForBt(final Long botId) {
		//Utter text have their text aggregated
		// <text1> | <text2> | <text3>
		final List<UtterTextExport> utterExport = exportPAO.exportUtterTextByBotId(botId);
		final List<ResponseButtonExport> responseButtonExport = exportPAO.exportResponseButtonByBotId(botId);

		final Map<UtterTextExport, List<ResponseButtonExport>> result = new HashMap<>();
		//Map utter and response buttons by topID
		for (final UtterTextExport text : utterExport) {
			final Long topId = text.getTopId();
			final List<ResponseButtonExport> responseList = responseButtonExport.stream().filter(x -> x.getTopId().equals(topId)).collect(Collectors.toList());
			result.put(text, responseList);
			responseButtonExport.removeAll(responseList);
		}
		return result;
	}

	/*
	 * Generate all combinaison of nlu training sentences with synonyms
	 */
	final DtList<NluTrainingExport> generateNLUSynonyms(final Long botId) {
		final DtList<NluTrainingExport> listNluExit = new DtList<>(NluTrainingExport.class);
		for (final NluTrainingExport nluOriginal : exportPAO.exportSmallTalkRelativeTrainingSentence(botId)) {

			//Creation des combinaisons
			final List<String> listText = meaningServices.generateSentenceWithSynonyms(nluOriginal, botId);

			for (final String text : listText) {
				final NluTrainingExport nlu = new NluTrainingExport();
				nlu.setText(text.trim());
				nlu.setTopId(nluOriginal.getTopId());
				listNluExit.add(nlu);
			}

		}
		return listNluExit;

	}

	@Override
	public Map<Long, String> mapTopicToBt(final Chatbot bot) {
		final Map<Long, String> result = new HashMap<>();
		//Get UtterText and Responsesbuttons by topId
		final Map<UtterTextExport, List<ResponseButtonExport>> map = getUtterAndResponseForBt(bot.getBotId());
		//Create map topId and bt associated
		for (final Entry<UtterTextExport, List<ResponseButtonExport>> entry : map.entrySet()) {
			final UtterTextExport utter = entry.getKey();
			final List<ResponseButtonExport> responses = entry.getValue();
			result.put(utter.getTopId(), createBt(utter, responses, false));
		}
		return result;
	}

	/*
	 * Begin sequence
	 * create bt
	 * end sequence
	 */
	private String createBt(final UtterTextExport utter, final List<ResponseButtonExport> responses, final boolean isStart) {
		final StringBuilder bt = new StringBuilder();
		//create sequence
		bt.append("begin sequence");
		BtBuilderUtils.addLineBreak(bt);

		if (isStart) {
			bt.append(createWelcomeBt(utter));
		} else {
			bt.append(createCurrentBt(responses, utter));
		}
		bt.append("end sequence");
		return bt.toString();
	}

	private String createButtonBt(final UtterTextExport utter, final List<ResponseButtonExport> responses) {
		final StringBuilder bt = new StringBuilder();
		//Utter text are aggregated by |
		final String[] splitUtter = utter.getUtterTexts().split("\\|");
		if (isRandomText(utter)) {
			BtBuilderUtils.createButtonRandomText(splitUtter, responses, bt);
		} else {
			BtBuilderUtils.createButton(splitUtter[0], responses, bt);
		}
		return bt.toString();
	}

	private String createSayBt(final UtterTextExport utter) {
		final String[] splitUtter = utter.getUtterTexts().split("\\|");
		final StringBuilder bt = new StringBuilder();
		//create random text with begin random
		if (isRandomText(utter)) {
			BtBuilderUtils.createRandomSequence(splitUtter, bt);
		} else {
			BtBuilderUtils.createRichtext(splitUtter, bt);
		}
		if (utter.getIsEnd()) {
			bt.append("topic:end");
		} else if (!KindTopicEnum.END.name().equals(utter.getKtoCd())) {
			bt.append("topic:start");
		}
		BtBuilderUtils.addLineBreak(bt);
		return bt.toString();
	}

	private String createWelcomeBt(final UtterTextExport utter) {
		final String[] splitUtter = utter.getUtterTexts().split("\\|");
		final StringBuilder bt = new StringBuilder();

		BtBuilderUtils.addLineBreak(bt);
		BtBuilderUtils.askNlu(bt, splitUtter[0]);
		BtBuilderUtils.addLineBreak(bt);

		return bt.toString();

	}

	private String createCurrentBt(final List<ResponseButtonExport> responses, final UtterTextExport utter) {
		final StringBuilder bt = new StringBuilder();
		//If no buttons responses return to topic:start
		if (responses.size() == 0) {
			bt.append(createSayBt(utter));
		} else {
			//Needs to stock the value and return to the topic selected
			//Use choose:button:nlu to allow text field
			bt.append(createButtonBt(utter, responses));
		}
		return bt.toString();
	}

	private boolean isRandomText(final UtterTextExport utters) {
		return utters.getResponseType().equals(ResponseTypeEnum.RANDOM_TEXT.name());
	}

	public String getBasicBt(final Chatbot bot, final String ktoCd) {
		final Topic topic = topicServices.getBasicTopicByBotIdKtoCd(bot.getBotId(), ktoCd);
		final SmallTalk smallTalk = smallTalkServices.findByTopId(topic.getTopId());
		final UtterText utterText = utterTextServices.getUtterTextByTopId(topic.getTopId());
		final UtterTextExport utterTextExport = new UtterTextExport();

		utterTextExport.setUtterTexts(utterText.getText());
		utterTextExport.setTopId(topic.getTopId());
		utterTextExport.setResponseType(smallTalk.getRtyId());
		utterTextExport.setIsEnd(smallTalk.getIsEnd());
		utterTextExport.setKtoCd(topic.getKtoCd());

		return createBt(utterTextExport, new ArrayList<ResponseButtonExport>(), ktoCd.equals(KindTopicEnum.START.name()));
	}

}
