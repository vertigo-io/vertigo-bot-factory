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
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicFileExport;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
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
import liquibase.util.csv.CSVReader;

@Controller
@RequestMapping("/bot/{botId}/topics")
@Secured("BotUser")
public class TopicsListController extends AbstractBotController {

	private static final ViewContextKey<TopicIhm> topicIhmListKey = ViewContextKey.of("topicsIhm");
	// All the topic types
	private static final ViewContextKey<TypeTopic> typeTopicListKey = ViewContextKey.of("typeTopicList");
	private static final ViewContextKey<TopicCategory> categoryListKey = ViewContextKey.of("categoryList");
	// return of the select
	private static final ViewContextKey<String> selectionList = ViewContextKey.of("selectionList");
	private static final ViewContextKey<TopicCategory> selectionCatList = ViewContextKey.of("selectionCatList");
	private static final ViewContextKey<String> topIdDetail = ViewContextKey.of("topIdDetail");
	private static final ViewContextKey<String> topicImport = ViewContextKey.of("topicImport");

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
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		viewContext.publishDtList(topicIhmListKey, TopicIhmFields.topId, topicServices.getAllNonTechnicalTopicIhmByBot(bot));
		viewContext.publishDtListModifiable(typeTopicListKey, typeTopicServices.getAllTypeTopic());
		viewContext.publishDtList(categoryListKey, categoryServices.getAllActiveCategoriesByBot(bot));
		viewContext.publishRef(selectionList, "");
		viewContext.publishDto(selectionCatList, new TopicCategory());
		viewContext.publishRef(topIdDetail, "");
		viewContext.publishRef(topicImport, "");
		toModeReadOnly();
	}

	@PostMapping("/createTopic")
	@Secured("BotAdm")
	public String doCreateTopic(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("selectionList") final String ttoCd) {
		if (ttoCd.isEmpty()) {
			throw new VUserException("Choose a type of topic");
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

		Long topCatId = null;
		if (!selectCat.isEmpty()) {
			topCatId = Long.valueOf(selectCat);
		}
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
			throw new VUserException("Please use a csv file.");
		}
		try (CSVReader csvReader = new CSVReader(new FileReader(VFileUtil.obtainReadOnlyPath(fileTmp).toString(), Charset.forName("cp1252")), ';', CSVReader.DEFAULT_QUOTE_CHARACTER, 0)) {

			final List<TopicFileExport> list = topicFileExportServices.transformFileToList(csvReader);

			topicFileExportServices.importTopicFromList(bot, list);
		}

		return "redirect:/bot/" + bot.getBotId() + "/topics/";
	}

}
