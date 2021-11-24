package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.domain.DictionaryEntity;
import io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper;
import io.vertigo.chatbot.domain.DtDefinitions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.dictionaryEntities.DictionaryEntityMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.topic.DictionaryEntityServices;
import io.vertigo.chatbot.designer.builder.services.topic.SynonymServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.filestore.util.VFileUtil;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.QueryParam;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import liquibase.util.csv.CSVReader;

@Controller
@RequestMapping("/bot/{botId}/dictionary")
@Secured("botAdm")
public class DictionaryEntityListController extends AbstractBotListController<DictionaryEntity> {

	private static final ViewContextKey<Synonym> synonymEditKey = ViewContextKey.of("synonymEdit");
	private static final ViewContextKey<DictionaryEntityWrapper> dictionaryEntityWrappersKey = ViewContextKey.of("dictionaryEntityWrappers");
	private static final ViewContextKey<DictionaryEntity> dictionaryEntityEditKey = ViewContextKey.of("dictionaryEntityEdit");

	@Inject
	private DictionaryEntityServices dictionaryEntityServices;

	@Inject
	private SynonymServices synonymServices;

	@Inject
	private FileServices fileServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(dictionaryEntityWrappersKey, DtDefinitions.DictionaryEntityWrapperFields.dictionaryEntityLabel, dictionaryEntityServices.getDictionaryExportByBotId(botId, "; "));
		viewContext.publishDto(dictionaryEntityEditKey, new DictionaryEntity());
		viewContext.publishDto(synonymEditKey, new Synonym());
		super.initBreadCrums(viewContext, DictionaryEntity.class);
	}

	@PostMapping("/saveDictionaryEntity")
	@Secured("botAdm")
	public String doSaveDictionaryEntity(final ViewContext viewContext,
										 @ViewAttribute("bot") final Chatbot bot,
										 @ViewAttribute("dictionaryEntityEdit") final DictionaryEntity dictionaryEntityEdit) {

		dictionaryEntityEdit.setBotId(bot.getBotId());
		final boolean creation = dictionaryEntityEdit.getDicEntId() == null;
		dictionaryEntityEdit.setLabel(dictionaryEntityEdit.getLabel().toLowerCase());

		final DictionaryEntity dictionaryEntitySaved = dictionaryEntityServices.save(bot, dictionaryEntityEdit);
		if (creation) {
			final Synonym synonym = new Synonym();
			synonym.setBotId(bot.getBotId());
			synonym.setDicEntId(dictionaryEntitySaved.getDicEntId());
			synonym.setLabel(dictionaryEntitySaved.getLabel());
			if (dictionaryEntityServices.findDictionaryEntityBySynonymLabelAndBotId(dictionaryEntitySaved.getLabel(), bot.getBotId()) != null) {
				throw new VUserException(DictionaryEntityMultilingualResources.ERR_UNIQUE_SYNONYM);
			}
			synonymServices.save(synonym);
		}

		return "redirect:/bot/" + dictionaryEntitySaved.getBotId() + "/dictionaryEntity/" + dictionaryEntitySaved.getDicEntId();

	}

	@PostMapping("/_deleteDictionaryEntity")
	@Secured("botAdm")
	public ViewContext doDeleteDictionaryEntity(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
												@RequestParam("dicEntId") final Long dicEntId) {

		dictionaryEntityServices.deleteDictionaryEntity(bot, dicEntId);

		viewContext.publishDtList(dictionaryEntityWrappersKey, dictionaryEntityServices.getDictionaryExportByBotId(bot.getBotId(), "; "));

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
			@QueryParam("importDictionaryFileUri") final FileInfoURI importDictionaryFile) throws IOException {

		final VFile fileTmp = fileServices.getFileTmp(importDictionaryFile);
		if (!fileTmp.getMimeType().equals("application/vnd.ms-excel")) {
			throw new VUserException(ExportMultilingualResources.ERR_CSV_FILE);
		}
		try (CSVReader csvReader = new CSVReader(new FileReader(VFileUtil.obtainReadOnlyPath(fileTmp).toString(), Charset.forName("cp1252")), ';', CSVReader.DEFAULT_QUOTE_CHARACTER, 0)) {

			final List<DictionaryEntityWrapper> list = dictionaryEntityServices.transformFileToList(csvReader);

			dictionaryEntityServices.importDictionaryFromList(bot, list);
		} catch (final Exception e) {
			throw e;
		}

		return "redirect:/bot/" + bot.getBotId() + "/dictionary/";
	}

}
