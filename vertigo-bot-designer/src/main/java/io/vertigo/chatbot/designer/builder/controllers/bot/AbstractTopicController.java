package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicLabel;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.model.topic.SaveTopicObject;
import io.vertigo.chatbot.designer.builder.services.topic.NluTrainingSentenceServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicInterfaceServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicLabelServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

public abstract class AbstractTopicController<D extends Entity, S extends TopicInterfaceServices<D>> extends AbstractBotCreationController<Topic> {

	protected static final ViewContextKey<Topic> topicKey = ViewContextKey.of("topic");
	protected static final ViewContextKey<Topic> topicListKey = ViewContextKey.of("topicList");

	protected static final ViewContextKey<TopicCategory> topicCategoryKey = ViewContextKey.of("topicCategory");
	protected static final ViewContextKey<TopicCategory> topicCategoryListKey = ViewContextKey.of("topicCategoryList");

	protected static final ViewContextKey<String> newNluTrainingSentenceKey = ViewContextKey.of("newNluTrainingSentence");
	protected static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesKey = ViewContextKey
			.of("nluTrainingSentences");
	protected static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesToDeleteKey = ViewContextKey
			.of("nluTrainingSentencesToDelete");

	// Label
	protected static final ViewContextKey<TopicLabel> topicLabelListKey = ViewContextKey
			.of("topicLabelList");
	protected static final ViewContextKey<TopicLabel> initialTopicLabelListKey = ViewContextKey
			.of("initialTopicLabelList");
	protected static final ViewContextKey<TopicLabel> allTopicLabelListKey = ViewContextKey
			.of("allTopicLabelList");

	@Inject
	protected TopicServices topicServices;

	@Inject
	protected S service;

	@Inject
	protected TopicCategoryServices topicCategoryServices;

	@Inject
	protected NluTrainingSentenceServices nluTrainingSentenceServices;

	@Inject
	protected TopicLabelServices topicLabelServices;

	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, final Chatbot bot, final Topic topic) {
		Assertion.check().isTrue(topic.getBotId().equals(bot.getBotId()), "Incoherent parameters");

		viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBot(bot));
		viewContext.publishDto(topicKey, topic);

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		final DtList<NluTrainingSentence> nluSentences = topicServices.getNluTrainingSentenceByTopic(bot, topic);
		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluSentences);
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		viewContext.publishDto(topicCategoryKey, topicCategoryServices.getTopicCategoryById(bot, topic.getTopCatId()));
		viewContext.publishDtList(topicCategoryListKey, topicCategoryServices.getAllActiveCategoriesByBot(bot));

		//Label
		final DtList<TopicLabel> initialList = this.topicLabelServices.getTopicLabelByBotIdAndTopId(bot, topic.getTopId());
		viewContext.publishDtList(initialTopicLabelListKey, initialList);
		viewContext.publishDtListModifiable(topicLabelListKey, new DtList<>(TopicLabel.class));
		viewContext.publishDtList(allTopicLabelListKey, this.topicLabelServices.getTopicLabelByBotId(bot));
		addMessageDeactivate(uiMessageStack, topic, nluSentences, bot);

	}

	public void initContextNew(final ViewContext viewContext, final Chatbot bot) {
		viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBot(bot));
		viewContext.publishDto(topicKey, topicServices.getNewTopic(bot));

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		viewContext.publishDto(topicCategoryKey, new TopicCategory());
		viewContext.publishDtList(topicCategoryListKey, topicCategoryServices.getAllActiveCategoriesByBot(bot));

		//Labels
		viewContext.publishDtList(initialTopicLabelListKey, new DtList<>(TopicLabel.class));
		viewContext.publishDtListModifiable(topicLabelListKey, new DtList<>(TopicLabel.class));
		viewContext.publishDtList(allTopicLabelListKey, this.topicLabelServices.getTopicLabelByBotId(bot));

	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_addTrainingSentence")
	public ViewContext doAddTrainingSentence(final ViewContext viewContext,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentenceIn,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		nluTrainingSentenceServices.addTrainingSentense(newNluTrainingSentenceIn, nluTrainingSentences);

		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);
		viewContext.publishRef(newNluTrainingSentenceKey, "");

		return viewContext;
	}

	@PostMapping("/_editTrainingSentence")
	public ViewContext doEditTrainingSentence(final ViewContext viewContext, @RequestParam("index") final int index,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		if (StringUtil.isBlank(newNluTrainingSentence)) {
			// empty edit, rollback modification
			viewContext.markModifiedKeys(nluTrainingSentencesKey);
			return viewContext;
		}

		int curIdx = 0;
		for (final NluTrainingSentence nts : nluTrainingSentences) {
			if (curIdx == index) {
				nts.setText(newNluTrainingSentence);
			} else if (newNluTrainingSentence.equalsIgnoreCase(nts.getText())) {
				throw new VUserException(TopicsMultilingualResources.NLU_ALREADY_EXISTS);
			}
			curIdx++;
		}

		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);

		return viewContext;
	}

	@PostMapping("/_removeTrainingSentence")
	public ViewContext doRemoveTrainingSentence(final ViewContext viewContext, @RequestParam("index") final int index,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		// remove from list
		final NluTrainingSentence removed = nluTrainingSentences.remove(index);
		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);

		// keep track of deleted persisted NluTrainingSentence
		if (removed.getNtsId() != null) {
			nluTrainingSentencesToDelete.add(removed);
		}
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey, nluTrainingSentencesToDelete);

		return viewContext;
	}

	@PostMapping("/_delete")
	String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot chatbot, @ViewAttribute("object") final D object,
			@ViewAttribute("topic") final Topic topic) {
		final DtList<Topic> listTopicRef = topicServices.getTopicReferencingTopId(topic.getTopId());
		if (!listTopicRef.isEmpty()) {
			final StringBuilder errorMessage = new StringBuilder(MessageText.of(TopicsMultilingualResources.DELETION_REF_ERROR).getDisplay());
			String prefix = "";
			for (final Topic topicRef : listTopicRef) {
				errorMessage.append(prefix);
				errorMessage.append(topicRef.getTitle());
				prefix = ", ";
			}
			errorMessage.append(".");
			throw new VUserException(errorMessage.toString());
		}

		service.delete(chatbot, service.findByTopId(topic.getTopId()), topic);
		topicLabelServices.cleanLabelFromTopic(chatbot, topic.getTopId());
		topicServices.deleteTopic(chatbot, topic);

		return "redirect:/bot/" + topic.getBotId() + "/topics/";

	}

	public void checkCategory(final Topic topic) {
		if (topic.getTopCatId() == null) {
			throw new VUserException(TopicsMultilingualResources.ERROR_CATEGORY);
		}
	}

	public void addMessageDeactivate(final UiMessageStack uiMessageStack, final Topic topic, final DtList<NluTrainingSentence> sentences, final Chatbot chatbot) {
		final boolean hasToBeDeactivate = service.hasToBeDeactivated(service.findByTopId(topic.getTopId()), chatbot);
		if (hasToBeDeactivate || sentences.isEmpty()) {
			uiMessageStack.info(service.getDeactivateMessage());
		}
	}

	@PostMapping("/_save")
	abstract String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("object") final D object,
			@ViewAttribute("topic") final Topic topic,
			@ViewAttribute("bot") final Chatbot chatbot,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			@ViewAttribute("topicLabelList") final DtList<TopicLabel> labels,
			@ViewAttribute("initialTopicLabelList") final DtList<TopicLabel> initialLabels);

	abstract Topic getTopic(final D object);

	public void saveTopic(final Topic topic,
			final String ttoCd,
			final Chatbot chatbot,
			final D object,
			final String newNluTrainingSentence,
			final DtList<NluTrainingSentence> nluTrainingSentences,
			final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			final DtList<TopicLabel> labels,
			final DtList<TopicLabel> initialLabels) {
		saveTopic(topic, ttoCd, chatbot, object, null, null, nluTrainingSentences, newNluTrainingSentence, nluTrainingSentencesToDelete, labels, initialLabels);

	}

	public void saveTopic(final Topic topic,
			final String ttoCd,
			final Chatbot chatbot,
			final D object,
			final DtList<ResponseButton> buttonList,
			final DtList<UtterText> utterTexts,
			final DtList<NluTrainingSentence> nluTrainingSentences,
			final String newNluTrainingSentence,
			final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			final DtList<TopicLabel> labels,
			final DtList<TopicLabel> initialLabels) {
		checkCategory(topic);
		// add training sentence who is not "validated" by enter and still in the input
		final SaveTopicObject<D> objectToSave = new SaveTopicObject<>(topic, chatbot, object, buttonList, utterTexts);
		nluTrainingSentenceServices.addTrainingSentense(newNluTrainingSentence, nluTrainingSentences);
		topicServices.saveTtoCd(topic, ttoCd);
		service.saveFromSaveTopicObject(objectToSave);
		topicServices.save(topic, chatbot, service.isEnabled(object, topic.getIsEnabled(), chatbot), nluTrainingSentences, nluTrainingSentencesToDelete);
		topicLabelServices.manageLabels(chatbot, topic, labels, initialLabels);

	}

}
