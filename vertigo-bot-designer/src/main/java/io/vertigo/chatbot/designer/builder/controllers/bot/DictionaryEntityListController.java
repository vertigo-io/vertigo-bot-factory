package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.domain.DictionaryEntity;
import io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/dictionary")
@Secured("Chatbot$botAdm")
public class DictionaryEntityListController extends AbstractBotListEntityController<DictionaryEntity> {

	private static final ViewContextKey<Synonym> synonymEditKey = ViewContextKey.of("synonymEdit");
	private static final ViewContextKey<DictionaryEntityWrapper> dictionaryEntityWrappersKey = ViewContextKey.of("dictionaryEntityWrappers");
	private static final ViewContextKey<DictionaryEntity> dictionaryEntityEditKey = ViewContextKey.of("dictionaryEntityEdit");
	private static final ViewContextKey<FileInfoURI> importDictionaryFileUri = ViewContextKey.of("importDictionaryFileUri");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(dictionaryEntityWrappersKey, DtDefinitions.DictionaryEntityWrapperFields.dictionaryEntityLabel, dictionaryEntityServices.getDictionaryExportByBotId(botId, "; "));
		viewContext.publishDto(dictionaryEntityEditKey, new DictionaryEntity());
		viewContext.publishDto(synonymEditKey, new Synonym());
		viewContext.publishFileInfoURI(importDictionaryFileUri, null);
		super.initBreadCrums(viewContext, DictionaryEntity.class);
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/saveDictionaryEntity")
	@Secured("Chatbot$botAdm")
	public String doSaveDictionaryEntity(final ViewContext viewContext,
										 @ViewAttribute("bot") final Chatbot bot,
										 @ViewAttribute("dictionaryEntityEdit") final DictionaryEntity dictionaryEntityEdit) {

		final DictionaryEntity dictionaryEntitySaved = dictionaryEntityServices.save(bot, dictionaryEntityEdit);

		return "redirect:/bot/" + dictionaryEntitySaved.getBotId() + "/dictionaryEntity/" + dictionaryEntitySaved.getDicEntId();

	}

	@PostMapping("/_deleteDictionaryEntity")
	@Secured("Chatbot$botAdm")
	public ViewContext doDeleteDictionaryEntity(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
												@RequestParam("dicEntId") final Long dicEntId, final UiMessageStack uiMessageStack) {

		dictionaryEntityServices.deleteDictionaryEntity(bot, dicEntId);

		viewContext.publishDtList(dictionaryEntityWrappersKey, dictionaryEntityServices.getDictionaryExportByBotId(bot.getBotId(), "; "));
		listLimitReached(viewContext, uiMessageStack);

		return viewContext;
	}

	@PostMapping("/_exportDictionary")
	@Secured("SuperAdm")
	public VFile doExportDictionary(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {

		final DtList<DictionaryEntityWrapper> listDictionaryEntitiesToExport = dictionaryEntityServices.getDictionaryExportByBotId(bot.getBotId(), "|");

		return dictionaryEntityServices.exportDictionary(bot, listDictionaryEntitiesToExport);

	}

	@PostMapping("/_importDictionary")
	@Secured("SuperAdm")
	public String doImportDictionary(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("importDictionaryFileUri") final FileInfoURI importDictionaryFile) {

		dictionaryEntityServices.importDictionaryFromCSVFile(bot, importDictionaryFile);

		return "redirect:/bot/" + bot.getBotId() + "/dictionary/";
	}

}
