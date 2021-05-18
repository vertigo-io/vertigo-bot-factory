package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.topic.TopicDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopic;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.topic.TopicPAO;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.chatbot.domain.DtDefinitions.NluTrainingSentenceFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopicFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

@Transactional
public class TopicServices implements Component {

	@Inject
	private TopicDAO topicDAO;

	@Inject
	private TopicPAO topicPAO;

	@Inject
	private NluTrainingSentenceDAO nluTrainingSentenceDAO;

	@Inject
	private KindTopicServices kindTopicServices;

	public Topic findTopicById(@SecuredOperation("botVisitor") final Long id) {
		return topicDAO.get(id);
	}

	public TopicIhm findTopicIhmById(@SecuredOperation("botVisitor") final Long id) {
		return topicPAO.getTopicIhmById(id);
	}

	public Topic save(final Topic topic) {
		//create code for export
		generateAndSetCode(topic);
		return topicDAO.save(topic);
	}

	public Topic save(@SecuredOperation("botContributor") final Topic topic, final Boolean isEnabled, final DtList<NluTrainingSentence> nluTrainingSentences,
			final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {

		//create code for export
		generateAndSetCode(topic);
		// save and remove NTS
		final DtList<NluTrainingSentence> ntsToSave = saveAllNotBlankNTS(topic, nluTrainingSentences);
		removeNTS(nluTrainingSentencesToDelete);
		topic.setIsEnabled(!ntsToSave.isEmpty() || isEnabled);

		return topicDAO.save(topic);
	}

	private Topic generateAndSetCode(final Topic topic) {
		final Long code = topicPAO.getMaxCodeByBotId(topic.getBotId());
		if (topic.getCode() == null) {
			topic.setCode(code + 1);
		}
		return topic;
	}

	public Topic createTopic(@SecuredOperation("botContributor") final Topic topic) {
		return topicDAO.create(topic);
	}

	public Topic createTopic(@SecuredOperation("botContributor") final String title, final String ttoCd, final String description, final Boolean isEnabled) {
		final Topic toCreate = new Topic();
		toCreate.setTitle(title);
		toCreate.setTtoCd(ttoCd);
		toCreate.setDescription(description);
		toCreate.setIsEnabled(isEnabled);
		return topicDAO.create(toCreate);
	}

	public Topic getNewTopic(@SecuredOperation("botContributor") final Chatbot bot) {
		final Topic topic = new Topic();
		topic.setBotId(bot.getBotId());
		topic.setIsEnabled(true);
		topic.setKtoCd(KindTopicEnum.NORMAL.name());
		return topic;
	}

	public void deleteTopic(@SecuredOperation("botContributor") final Chatbot bot, final Topic topic) {
		// delete sub elements
		for (final NluTrainingSentence its : getNluTrainingSentenceByTopic(bot, topic)) {
			nluTrainingSentenceDAO.delete(its.getUID());
		}

		topicDAO.delete(topic.getTopId());
	}

	public DtList<Topic> getAllTopicByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()), DtListState.of(1000));
	}

	public DtList<Topic> getAllTopicByBotTtoCd(@SecuredOperation("botVisitor") final Chatbot bot, final String ttoCd) {
		return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()).and(Criterions.isEqualTo(TopicFields.ttoCd, ttoCd)), DtListState.of(1000));
	}

	public DtList<TopicIhm> getAllTopicIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicPAO.getAllTopicsIhmFromBot(bot.getBotId(), Optional.empty());
	}

	public DtList<Topic> getAllTopicEnableByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()).and(Criterions.isEqualTo(TopicFields.isEnabled, true)), DtListState.of(1000));
	}

	public void removeAllTopicsFromBot(@SecuredOperation("botAdm") final Chatbot bot) {
		topicPAO.removeAllTopicsFromBot(bot.getBotId());
	}

	public DtList<Topic> getTopicFromTopicCategory(final TopicCategory category) {
		return topicDAO.getAllTopicFromCategory(category.getTopCatId());
	}

	public DtList<Topic> getAllTopicRelativeSmallTalkByBot(final Chatbot bot) {
		return topicDAO.getAllTopicRelativeSmallTalkByBotId(bot.getBotId());
	}

	public DtList<Topic> getAllTopicRelativeScriptIntentionByBot(final Chatbot bot) {
		return topicDAO.getAllTopicRelativeScriptIntentByBotId(bot.getBotId());
	}

	public Topic getBasicTopicByBotIdKtoCd(final Long botId, final String ktoCd) {
		return topicDAO.getBasicTopicByBotIdKtoCd(botId, ktoCd);
	}

	public DtList<TopicIhm> getAllNonTechnicalTopicIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicPAO.getAllTopicsIhmFromBot(bot.getBotId(), Optional.of(KindTopicEnum.NORMAL.name()));
	}

	public DtList<Topic> getTopicReferencingTopId(final Long topId) {
		return topicDAO.getTopicReferencingTopId(topId);
	}

	public void initNewBasicTopic(final ViewContext viewContext, final String ktoCd, final ViewContextKey<Topic> topickey,
			final ViewContextKey<UtterText> uttertextkey) {
		final Locale locale = UserSessionUtils.getUserSession().getLocale();

		final Topic topic = new Topic();
		final KindTopic kto = kindTopicServices.findKindTopicByCd(ktoCd);
		topic.setIsEnabled(true);
		topic.setTitle(getTitle(kto, locale));
		topic.setTtoCd(TypeTopicEnum.SMALLTALK.name());
		topic.setKtoCd(ktoCd);
		topic.setDescription(getDescription(kto, locale));
		viewContext.publishDto(topickey, topic);
		final UtterText utterText = new UtterText();
		utterText.setText(kindTopicServices.getDefaultTextByLocale(kto, locale));
		viewContext.publishDto(uttertextkey, utterText);

	}

	private String getTitle(final KindTopic kto, final Locale locale) {
		if (Locale.FRANCE.equals(locale)) {
			return kto.getTitleFrench();
		} else {
			return kto.getTitleEnglish();
		}
	}

	private String getDescription(final KindTopic kto, final Locale locale) {
		if (Locale.FRANCE.equals(locale)) {
			return kto.getDescriptionFrench();
		} else {
			return kto.getDescriptionEnglish();
		}
	}

	//********* NTS part ********/

	public DtList<NluTrainingSentence> getNluTrainingSentenceByTopic(@SecuredOperation("botVisitor") final Chatbot bot, final Topic topic) {
		Assertion.check()
				.isNotNull(topic.getTopId());
		// ---

		return nluTrainingSentenceDAO.findAll(
				Criterions.isEqualTo(NluTrainingSentenceFields.topId, topic.getTopId()),
				DtListState.of(1000, 0, NluTrainingSentenceFields.ntsId.name(), false));
	}

	public void removeNTS(final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {
		nluTrainingSentencesToDelete.stream()
				.filter(itt -> itt.getNtsId() != null)
				.forEach(itt -> nluTrainingSentenceDAO.delete(itt.getNtsId()));
	}

	protected DtList<NluTrainingSentence> saveAllNotBlankNTS(final Topic topic, final DtList<NluTrainingSentence> nluTrainingSentences) {
		// save nlu textes
		final DtList<NluTrainingSentence> ntsToSave = nluTrainingSentences.stream()
				.filter(nts -> !StringUtil.isBlank(nts.getText()))
				.collect(VCollectors.toDtList(NluTrainingSentence.class));

		for (final NluTrainingSentence nts : ntsToSave) {
			nts.setTopId(topic.getTopId());
			nluTrainingSentenceDAO.save(nts);
		}

		return ntsToSave;
	}

	public void removeAllNTSFromBot(final Chatbot bot) {
		topicPAO.removeAllNluTrainingSentenceByBotId(bot.getBotId());
	}
}
