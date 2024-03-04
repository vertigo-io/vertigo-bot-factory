package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/categories")
@Secured("BotUser")
public class TopicCategoryListController extends AbstractBotListEntityController<TopicCategory> {

	private static final ViewContextKey<TopicCategory> topicCategorieskKey = ViewContextKey.of("topicCategories");
	private static final ViewContextKey<FileInfoURI> importCategoriesFileUri = ViewContextKey.of("importCategoriesFileUri");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(topicCategorieskKey, topicCategoryServices.getAllCategoriesByBot(bot));
		viewContext.publishFileInfoURI(importCategoriesFileUri, null);
		super.initBreadCrums(viewContext, TopicCategory.class);
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/_exportCategories")
	public VFile exportCategories(final ViewContext viewContext, final UiMessageStack uiMessageStack, @ViewAttribute("bot") final Chatbot bot) {
		return topicCategoryServices.exportCategories(bot, topicCategoryServices.getAllNonTechnicalCategoriesByBot(bot));
	}

	@PostMapping("/_importCategories")
	public String doImportDictionary(final ViewContext viewContext,
									 @ViewAttribute("bot") final Chatbot bot,
									 @ViewAttribute("importCategoriesFileUri") final FileInfoURI importCategoriesFileUri) {

		if (importCategoriesFileUri == null) {
			throw new VUserException(UtilsMultilingualResources.IMPORT_FILE_MUST_NOT_BE_EMPTY);
		}
		topicCategoryServices.importCategoriesFromCSVFile(bot, importCategoriesFileUri);

		return "redirect:/bot/" + bot.getBotId() + "/categories/";
	}

}
