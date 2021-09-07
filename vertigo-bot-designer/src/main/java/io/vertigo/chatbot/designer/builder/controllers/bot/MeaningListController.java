package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.meanings.MeaningsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.topic.MeaningServices;
import io.vertigo.chatbot.designer.builder.services.topic.SynonymServices;
import io.vertigo.chatbot.designer.domain.Meaning;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.core.lang.VUserException;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/dictionary")
@Secured("botAdm")
public class MeaningListController extends AbstractBotListController<Meaning> {

	private static final ViewContextKey<Synonym> synonymEditKey = ViewContextKey.of("synonymEdit");
	private static final ViewContextKey<Synonym> synonymsKey = ViewContextKey.of("synonyms");
	private static final ViewContextKey<Meaning> meaningEditKey = ViewContextKey.of("meaningEdit");

	@Inject
	private MeaningServices meaningServices;

	@Inject
	private SynonymServices synonymServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(synonymsKey, synonymServices.getAllSynonymByBot(bot));
		viewContext.publishDto(meaningEditKey, new Meaning());
		viewContext.publishDto(synonymEditKey, new Synonym());
		super.initBreadCrums(viewContext, Meaning.class);
	}

	@PostMapping("/saveMeaning")
	@Secured("botAdm")
	public String doSaveMeaning(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("meaningEdit") final Meaning meaningEdit) {

		meaningEdit.setBotId(bot.getBotId());
		final boolean creation = meaningEdit.getMeaId() == null;
		meaningEdit.setLabel(meaningEdit.getLabel().toLowerCase());

		final Meaning meaningSaved = meaningServices.save(bot, meaningEdit);
		if (creation) {
			final Synonym synonym = new Synonym();
			synonym.setBotId(bot.getBotId());
			synonym.setMeaId(meaningSaved.getMeaId());
			synonym.setLabel(meaningSaved.getLabel());
			if (meaningServices.findMeaningBySynonymLabelAndBotId(meaningSaved.getLabel(), bot.getBotId()) != null) {
				throw new VUserException(MeaningsMultilingualResources.ERR_UNIQUE_SYNONYM);
			}
			synonymServices.save(synonym);
		}

		return "redirect:/bot/" + meaningSaved.getBotId() + "/meaning/" + meaningSaved.getMeaId();

	}

	@PostMapping("/_deleteMeaning")
	@Secured("botAdm")
	public ViewContext doDeleteMeaning(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
			@RequestParam("meaId") final Long meaId) {

		meaningServices.deleteMeaning(bot, meaId);

		viewContext.publishDtList(synonymsKey, synonymServices.getAllSynonymByBot(bot));

		return viewContext;
	}

}
