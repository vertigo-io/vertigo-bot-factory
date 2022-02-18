package io.vertigo.chatbot.designer.builder.services.topic.export;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topic.export.ExportPAO;
import io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonExport;
import io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonUrlExport;
import io.vertigo.chatbot.designer.domain.topic.export.UtterTextExport;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Transactional
public class SmallTalkExportServices implements TopicExportInterfaceServices<SmallTalk>, Component {

	@Inject
	private TopicServices topicServices;

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private ExportPAO exportPAO;

	@Override
	public TypeTopicEnum getHandleObject() {
		return TypeTopicEnum.SMALLTALK;
	}

	private Map<UtterTextExport, Tuple<List<ResponseButtonExport>, List<ResponseButtonUrlExport>>> getUtterAndResponseForBt(final Long botId) {
		//Utter text have their text aggregated
		// <text1> | <text2> | <text3>
		final List<UtterTextExport> utterExport = exportPAO.exportUtterTextByBotId(botId);
		final List<ResponseButtonExport> responseButtonExport = exportPAO.exportResponseButtonByBotId(botId);
		final List<ResponseButtonUrlExport> responseButtonUrlExport = exportPAO.exportResponseButtonUrlByBotId(botId);

		final Map<UtterTextExport, Tuple<List<ResponseButtonExport>, List<ResponseButtonUrlExport>>> result = new HashMap<>();
		//Map utter and response buttons by topID
		for (final UtterTextExport text : utterExport) {
			final Long topId = text.getTopId();
			final List<ResponseButtonExport> responseList = responseButtonExport.stream().filter(x -> x.getTopId().equals(topId)).collect(Collectors.toList());
			final List<ResponseButtonUrlExport> responseUrlList = responseButtonUrlExport.stream().filter(x -> x.getTopId().equals(topId)).collect(Collectors.toList());
			result.put(text, Tuple.of(responseList, responseUrlList));
			responseButtonExport.removeAll(responseList);
		}
		return result;
	}

	@Override
	public Map<Long, String> mapTopicToBt(final Chatbot bot) {
		final Map<Long, String> result = new HashMap<>();
		//Get UtterText and Responsesbuttons by topId
		final Map<UtterTextExport, Tuple<List<ResponseButtonExport>, List<ResponseButtonUrlExport>>> map = getUtterAndResponseForBt(bot.getBotId());
		//Create map topId and bt associated
		for (final Entry<UtterTextExport, Tuple<List<ResponseButtonExport>, List<ResponseButtonUrlExport>>> entry : map.entrySet()) {
			final UtterTextExport utter = entry.getKey();
			final List<ResponseButtonExport> responses = entry.getValue().getVal1();
			final List<ResponseButtonUrlExport> responsesUrl = entry.getValue().getVal2();
			result.put(utter.getTopId(), createBt(utter, responses, responsesUrl, false));
		}
		return result;
	}

	/*
	 * Begin sequence
	 * create bt
	 * end sequence
	 */
	private String createBt(final UtterTextExport utter, final List<ResponseButtonExport> responses, final List<ResponseButtonUrlExport> responsesUrl, final boolean isStart) {
		final StringBuilder bt = new StringBuilder();
		//create sequence
		bt.append("begin sequence");
		BtBuilderUtils.addLineBreak(bt);

		if (isStart) {
			bt.append(createWelcomeBt(utter));
		} else {
			bt.append(createCurrentBt(responses, responsesUrl, utter));
		}
		bt.append("end sequence");
		return bt.toString();
	}

	private String createButtonBt(final UtterTextExport utter, final List<ResponseButtonExport> responses, final List<ResponseButtonUrlExport> responsesUrl) {
		final StringBuilder bt = new StringBuilder();
		//Utter text are aggregated by |
		final String[] splitUtter = utter.getUtterTexts().split("\\|");
		if (isRandomText(utter)) {
			BtBuilderUtils.createSelectorRandomSequence(splitUtter, responses, responsesUrl, bt);
		} else {
			BtBuilderUtils.createButton(splitUtter[0], responses, responsesUrl, bt);
		}
		return bt.toString();
	}

	private String createSayBt(final UtterTextExport utter) {
		// JIRA 127 - All " have been replaced by &#34; except for links.
		final String[] splitUtter = utter.getUtterTexts().replace("\"", "").split("\\|");
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
			bt.append("topic:idle");
		}
		BtBuilderUtils.addLineBreak(bt);
		return bt.toString();
	}

	private String createWelcomeBt(final UtterTextExport utter) {
		// JIRA 127 - All " have been replaced by &#34; except for links.
		final String[] splitUtter = utter.getUtterTexts().replace("\"", "").split("\\|");
		final StringBuilder bt = new StringBuilder();

		BtBuilderUtils.addLineBreak(bt);
		BtBuilderUtils.askNlu(bt, splitUtter[0]);
		BtBuilderUtils.addLineBreak(bt);

		return bt.toString();

	}

	private String createCurrentBt(final List<ResponseButtonExport> responses, final List<ResponseButtonUrlExport> responsesUrl, final UtterTextExport utter) {
		final StringBuilder bt = new StringBuilder();
		//If no buttons responses return to topic:start
		if (responses.size() == 0 && responsesUrl.size() == 0) {
			bt.append(createSayBt(utter));
		} else {
			//Needs to stock the value and return to the topic selected
			//Use choose:button:nlu to allow text field
			bt.append(createButtonBt(utter, responses, responsesUrl));
		}
		return bt.toString();
	}

	private boolean isRandomText(final UtterTextExport utters) {
		return utters.getResponseType().equals(ResponseTypeEnum.RANDOM_TEXT.name());
	}

	@Override
	public String getBasicBt(final Chatbot bot, final String ktoCd) {
		final Topic topic = topicServices.getBasicTopicByBotIdKtoCd(bot.getBotId(), ktoCd).orElseThrow();
		final SmallTalk smallTalk = smallTalkServices.findByTopId(topic.getTopId()).orElseThrow();
		final UtterText utterText = smallTalkServices.getBasicUtterTextByTopId(topic.getTopId());
		final UtterTextExport utterTextExport = new UtterTextExport();

		utterTextExport.setUtterTexts(utterText.getText());
		utterTextExport.setTopId(topic.getTopId());
		utterTextExport.setResponseType(smallTalk.getRtyId());
		utterTextExport.setIsEnd(smallTalk.getIsEnd());
		utterTextExport.setKtoCd(topic.getKtoCd());

		return createBt(utterTextExport, new ArrayList<ResponseButtonExport>(), new ArrayList<ResponseButtonUrlExport>(), ktoCd.equals(KindTopicEnum.START.name()));
	}

	@Override
	public DtList<Topic> getAllNonTechnicalAndActiveTopicByBot(final Chatbot bot, final TypeTopicEnum typeEnum) {
		return topicServices.getAllNonTechnicalTopicAndActiveByBotTtoCd(bot, typeEnum.name());
	}

}
