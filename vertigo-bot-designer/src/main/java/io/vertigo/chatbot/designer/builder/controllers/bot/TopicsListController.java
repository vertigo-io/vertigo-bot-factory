/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicCriteria;
import io.vertigo.chatbot.commons.domain.topic.TopicFileExport;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.services.topic.TypeTopicServices;
import io.vertigo.chatbot.designer.builder.services.topic.export.file.TopicFileExportServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.domain.DtDefinitions.TopicIhmFields;
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
@RequestMapping("/bot/{botId}/topics")
@Secured("BotUser")
public class TopicsListController extends AbstractBotListController<Topic> {

	private static final ViewContextKey<TopicIhm> topicIhmListKey = ViewContextKey.of("topicsIhm");
	// All the topic types
	private static final ViewContextKey<TypeTopic> typeTopicListKey = ViewContextKey.of("typeTopicList");
	private static final ViewContextKey<TopicCategory> categoryListKey = ViewContextKey.of("categoryList");
	// return of the select
	private static final ViewContextKey<String> selectionListKey = ViewContextKey.of("selectionList");
	private static final ViewContextKey<TopicCategory> selectionCatListKey = ViewContextKey.of("selectionCatList");
	private static final ViewContextKey<String> topIdDetailKey = ViewContextKey.of("topIdDetail");
	private static final ViewContextKey<String> topicImportKey = ViewContextKey.of("topicImport");
	private static final ViewContextKey<TopicCriteria> criteriaKey = ViewContextKey.of("criteria");

	@Inject
	private TopicServices topicServices;
	@Inject
	private TypeTopicServices typeTopicServices;
	@Inject
	private FileServices fileServices;
	@Inject
	private TopicCategoryServices categoryServices;

	@Inject
	private TopicFileExportServices topicFileExportServices;

	@GetMapping("/")
	@Secured("BotUser")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(topicIhmListKey, TopicIhmFields.topId, topicServices.getAllNonTechnicalTopicIhmByBot(bot, localeManager.getCurrentLocale().toString()));
		viewContext.publishDtListModifiable(typeTopicListKey, typeTopicServices.getAllTypeTopic());
		viewContext.publishDtList(categoryListKey, categoryServices.getAllActiveCategoriesByBot(bot));
		viewContext.publishDto(criteriaKey, new TopicCriteria());
		viewContext.publishRef(selectionListKey, "");
		viewContext.publishDto(selectionCatListKey, new TopicCategory());
		viewContext.publishRef(topIdDetailKey, "");
		viewContext.publishRef(topicImportKey, "");
		super.initBreadCrums(viewContext, Topic.class);
		toModeReadOnly();
	}

	@PostMapping("/createTopic")
	@Secured("BotAdm")
	public String doCreateTopic(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("selectionList") final String ttoCd) {
		if (ttoCd.isEmpty()) {
			throw new VUserException(TopicsMultilingualResources.CHOOSE_TYPE_ERROR);
		}
		final Long botId = bot.getBotId();
		if (TypeTopicEnum.SMALLTALK.name().equals(ttoCd)) {
			return "redirect:/bot/" + botId + "/smallTalk/new";
		} else if (TypeTopicEnum.SCRIPTINTENTION.name().equals(ttoCd)) {
			return "redirect:/bot/" + botId + "/scriptIntention/new";
		}
		return "redirect:/bot/" + botId + "/topics/";
	}

	@PostMapping("/_exportTopicFile")
	@Secured("SuperAdm")
	public VFile doExportTopicFile(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("selectionCatList") final String selectCat) {

		final Long topCatId = !selectCat.isEmpty() ? Long.valueOf(selectCat) : null;

		final DtList<TopicFileExport> listTopics = topicFileExportServices.getTopicFileExport(bot.getBotId(), topCatId);

		return topicFileExportServices.exportTopicFile(bot, listTopics);

	}

	@PostMapping("/_importTopic")
	@Secured("SuperAdm")
	public String doImportTopic(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@QueryParam("importTopicFileUri") final FileInfoURI importTopicFile) throws IOException {

		final VFile fileTmp = fileServices.getFileTmp(importTopicFile);
		if (!fileTmp.getMimeType().equals("application/vnd.ms-excel")) {
			throw new VUserException(ExportMultilingualResources.ERR_CSV_FILE);
		}
		try (CSVReader csvReader = new CSVReader(new FileReader(VFileUtil.obtainReadOnlyPath(fileTmp).toString(), Charset.forName("cp1252")), ';', CSVReader.DEFAULT_QUOTE_CHARACTER, 0)) {

			final List<TopicFileExport> list = topicFileExportServices.transformFileToList(csvReader);

			topicFileExportServices.importTopicFromList(bot, list);
		} catch (final Exception e) {
			throw e;
		}

		return "redirect:/bot/" + bot.getBotId() + "/topics/";
	}

}
