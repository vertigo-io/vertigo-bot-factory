package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicLabel;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
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

public abstract class AbstractTopicController<D extends Entity> extends AbstractBotController {

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
	protected List<TopicInterfaceServices> topicInterfaceServices;
	@Inject
	protected TopicCategoryServices topicCategoryServices;

	@Inject
	protected NluTrainingSentenceServices nluTrainingSentenceServices;

	@Inject
	protected TopicLabelServices topicLabelServices;

	public void initContext(final ViewContext viewContext, final Chatbot bot, final Topic topic) {
		Assertion.check().isTrue(topic.getBotId().equals(bot.getBotId()), "Incoherent parameters");

		viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBot(bot));
		viewContext.publishDto(topicKey, topic);

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				topicServices.getNluTrainingSentenceByTopic(bot, topic));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		viewContext.publishDto(topicCategoryKey, topicCategoryServices.getTopicCategoryById(bot, topic.getTopCatId()));
		viewContext.publishDtList(topicCategoryListKey, topicCategoryServices.getAllActiveCategoriesByBot(bot));

		//Label
		final DtList<TopicLabel> initialList = this.topicLabelServices.getTopicLabelByBotIdAndTopId(bot, topic.getTopId());
		viewContext.publishDtList(initialTopicLabelListKey, initialList);
		viewContext.publishDtListModifiable(topicLabelListKey, new DtList<>(TopicLabel.class));
		viewContext.publishDtList(allTopicLabelListKey, this.topicLabelServices.getTopicLabelByBotId(bot));

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

	abstract Topic getTopic(final D object);

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

		for (final TopicInterfaceServices services : topicInterfaceServices) {
			if (services.handleObject(topic)) {
				services.delete(chatbot, services.findByTopId(topic.getTopId()), topic);
				topicLabelServices.cleanLabelFromTopic(chatbot, topic.getTopId());
				topicServices.deleteTopic(chatbot, topic);
			}
		}

		return "redirect:/bot/" + topic.getBotId() + "/topics/";
	}

	public void checkCategory(final Topic topic) {
		if (topic.getTopCatId() == null) {
			throw new VUserException(TopicsMultilingualResources.ERROR_CATEGORY);
		}
	}

}
