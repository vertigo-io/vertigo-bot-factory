package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.meanings.MeaningsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.topic.MeaningServices;
import io.vertigo.chatbot.designer.builder.services.topic.SynonymServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.domain.DictionaryExport;
import io.vertigo.chatbot.designer.domain.Meaning;
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
public class MeaningListController extends AbstractBotListController<Meaning> {

	private static final ViewContextKey<Synonym> synonymEditKey = ViewContextKey.of("synonymEdit");
	private static final ViewContextKey<Synonym> synonymsKey = ViewContextKey.of("synonyms");
	private static final ViewContextKey<Meaning> meaningEditKey = ViewContextKey.of("meaningEdit");

	@Inject
	private MeaningServices meaningServices;

	@Inject
	private SynonymServices synonymServices;

	@Inject
	private FileServices fileServices;

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

	@PostMapping("/_exportDictionary")
	@Secured("SuperAdm")
	public VFile doExportDictionary(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {

		final DtList<DictionaryExport> listDictionaryExport = meaningServices.getDictionaryExportByBotId(bot.getBotId());

		return meaningServices.exportDictionary(bot, listDictionaryExport);

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

			final List<DictionaryExport> list = meaningServices.transformFileToList(csvReader);

			meaningServices.importDictionaryFromList(bot, list);
		} catch (final Exception e) {
			throw e;
		}

		return "redirect:/bot/" + bot.getBotId() + "/dictionary/";
	}

}
