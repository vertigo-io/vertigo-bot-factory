package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
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

	@Inject
	protected TopicServices topicServices;
	@Inject
	protected TopicCategoryServices topicCategoryServices;

	public void initContext(final ViewContext viewContext, final Chatbot bot, final Topic topic) {
		Assertion.check().isTrue(topic.getBotId().equals(bot.getBotId()), "Paramètres incohérents");

		viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBotTtoCd(bot, TypeTopicEnum.SMALLTALK.name()));
		viewContext.publishDto(topicKey, topic);

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				topicServices.getNluTrainingSentenceByTopic(bot, topic));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		viewContext.publishDto(topicCategoryKey, topicCategoryServices.getTopicCategoryById(bot, topic.getTopCatId()));
		viewContext.publishDtList(topicCategoryListKey, topicCategoryServices.getAllActiveCategoriesByBot(bot));
	}

	public void initContextNew(final ViewContext viewContext, final Chatbot bot) {
		viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBotTtoCd(bot, TypeTopicEnum.SMALLTALK.name()));
		viewContext.publishDto(topicKey, topicServices.getNewTopic(bot));

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		viewContext.publishDto(topicCategoryKey, new TopicCategory());
		viewContext.publishDtList(topicCategoryListKey, topicCategoryServices.getAllActiveCategoriesByBot(bot));
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_addTrainingSentence")
	public ViewContext doAddTrainingSentence(final ViewContext viewContext,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentenceIn,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		addTrainingSentense(newNluTrainingSentenceIn, nluTrainingSentences);

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
				throw new VUserException("This sentense already exists");
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

	public void addTrainingSentense(final String newNluTrainingSentenceIn,
			final DtList<NluTrainingSentence> nluTrainingSentences) {
		if (StringUtil.isBlank(newNluTrainingSentenceIn)) {
			return;
		}

		final String newNluTrainingSentence = newNluTrainingSentenceIn.trim();

		final boolean exists = nluTrainingSentences.stream()
				.anyMatch(its -> its.getText().equalsIgnoreCase(newNluTrainingSentence));
		if (exists) {
			throw new VUserException("This sentense already exists");
		}

		final NluTrainingSentence newText = new NluTrainingSentence();
		newText.setText(newNluTrainingSentence);

		nluTrainingSentences.add(newText);
	}

	abstract Topic getTopic(final D object);

	@PostMapping("/_save")
	abstract String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("object") final D object,
			@ViewAttribute("topic") final Topic topic,
			@ViewAttribute("bot") final Chatbot chatbot,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete);

	@PostMapping("/_delete")
	abstract String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot chatbot, @ViewAttribute("object") final D object,
			@ViewAttribute("topic") final Topic topic);

}
