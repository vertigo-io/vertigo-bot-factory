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

import javax.inject.Inject;

import io.vertigo.chatbot.designer.domain.DictionaryEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.dictionaryEntities.DictionaryEntityMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.topic.DictionaryEntityServices;
import io.vertigo.chatbot.designer.builder.services.topic.SynonymServices;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/dictionaryEntity")
@Secured("BotAdm")
public class DictionaryEntityDetailController extends AbstractBotCreationController<DictionaryEntity> {

	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");

	private static final ViewContextKey<DictionaryEntity> dictionaryEntityKey = ViewContextKey.of("dictionaryEntity");

	private static final ViewContextKey<Synonym> synonymsKey = ViewContextKey.of("synonyms");
	protected static final ViewContextKey<String> newSynonymKey = ViewContextKey.of("newSynonym");
	protected static final ViewContextKey<Synonym> synonymsToDeleteKey = ViewContextKey
			.of("synonymsToDelete");

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private DictionaryEntityServices dictionaryEntityServices;

	@Inject
	private SynonymServices synonymServices;

	@GetMapping("/{dicEntId}")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
			@PathVariable("dicEntId") final Long dicEntId) {

		super.initCommonContext(viewContext, uiMessageStack, botId);
		final Chatbot chatbot = chatbotServices.getChatbotById(botId);
		viewContext.publishDto(botKey, chatbot);

		final DictionaryEntity dictionaryEntity = dictionaryEntityServices.findDictionaryEntityById(dicEntId);

		viewContext.publishDto(dictionaryEntityKey, dictionaryEntity);
		viewContext.publishDtList(synonymsKey, synonymServices.getAllSynonymByDictionaryEntity(dictionaryEntity));
		viewContext.publishRef(newSynonymKey, "");

		viewContext.publishDtList(synonymsToDeleteKey,
				new DtList<Synonym>(Synonym.class));
		super.initBreadCrums(viewContext, dictionaryEntity);
		toModeReadOnly();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String saveDictionaryEntity(final ViewContext viewContext,
									   final UiMessageStack uiMessageStack,
									   @ViewAttribute("bot") final Chatbot bot,
									   @ViewAttribute("dictionaryEntity") final DictionaryEntity dictionaryEntity,
									   @ViewAttribute("newSynonym") final String newSynonym,
									   @ViewAttribute("synonyms") final DtList<Synonym> synonyms,
									   @ViewAttribute("synonymsToDelete") final DtList<Synonym> synonymsToDelete) {

		addSynonym(newSynonym, synonyms);
		dictionaryEntityServices.save(bot, dictionaryEntity, synonyms, synonymsToDelete);

		if (synonyms.isEmpty()) {
			//If all synonyms are removed, the dictionaryEntity is deleted
			dictionaryEntityServices.deleteDictionaryEntity(bot, dictionaryEntity.getDicEntId());
			return "redirect:/bot/" + dictionaryEntity.getBotId() + "/dictionary/";
		}
		return "redirect:/bot/" + dictionaryEntity.getBotId() + "/dictionaryEntity/" + dictionaryEntity.getDicEntId();
	}

	@PostMapping("/_delete")
	public String deleteDictionaryEntity(final ViewContext viewContext,
										 @ViewAttribute("bot") final Chatbot bot,
										 @ViewAttribute("dictionaryEntity") final DictionaryEntity dictionaryEntity) {
		dictionaryEntityServices.deleteDictionaryEntity(bot, dictionaryEntity.getDicEntId());
		return "redirect:/bot/" + bot.getBotId() + "/dictionaryEntity/";
	}

	@PostMapping("/_addSynonym")
	public ViewContext doAddSynonym(final ViewContext viewContext,
			@ViewAttribute("newSynonym") final String newSynonymIn,
			@ViewAttribute("synonyms") final DtList<Synonym> synonyms) {

		addSynonym(newSynonymIn, synonyms);

		viewContext.publishDtListModifiable(synonymsKey, synonyms);
		viewContext.publishRef(newSynonymKey, "");

		return viewContext;
	}

	@PostMapping("/_editSynonym")
	public ViewContext doEditSynonym(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("newSynonym") final String newSynonym,
			@ViewAttribute("synonyms") final DtList<Synonym> synonyms) {

		if (StringUtil.isBlank(newSynonym)) {
			// empty edit, rollback modification
			viewContext.markModifiedKeys(synonymsKey);
			return viewContext;
		}

		int curIdx = 0;
		for (final Synonym syn : synonyms) {
			if (curIdx == index) {
				syn.setLabel(newSynonym);
			} else if (newSynonym.equalsIgnoreCase(syn.getLabel())) {
				throw new VUserException(DictionaryEntityMultilingualResources.ERR_UNIQUE_SYNONYM);
			}
			curIdx++;
		}

		viewContext.publishDtListModifiable(synonymsKey, synonyms);

		return viewContext;
	}

	@PostMapping("/_removeSynonym")
	public ViewContext doRemoveSynonym(final ViewContext viewContext, @RequestParam("index") final int index,
			@ViewAttribute("synonymsToDelete") final DtList<Synonym> synonymsToDelete,
			@ViewAttribute("synonyms") final DtList<Synonym> synonyms) {

		// remove from list
		final Synonym removed = synonyms.remove(index);
		viewContext.publishDtListModifiable(synonymsKey, synonyms);

		// keep track of deleted persisted synonym
		if (removed.getSynId() != null) {
			synonymsToDelete.add(removed);
		}
		viewContext.publishDtList(synonymsToDeleteKey, synonymsToDelete);

		return viewContext;
	}

	/**
	 * Add a synonym and modify table
	 *
	 * @param newSynonymIn
	 * @param synonyms
	 */
	public void addSynonym(final String newSynonymIn,
			final DtList<Synonym> synonyms) {
		if (StringUtil.isBlank(newSynonymIn)) {
			return;
		}

		final String newSynonym = newSynonymIn.trim();

		final boolean exists = synonyms.stream()
				.anyMatch(its -> its.getLabel().equalsIgnoreCase(newSynonym));
		if (exists) {
			throw new VUserException(DictionaryEntityMultilingualResources.ERR_UNIQUE_SYNONYM);
		}

		final Synonym newText = new Synonym();
		newText.setLabel(newSynonym);

		synonyms.add(newText);
	}

	@Override
	protected String getBreadCrums(final DictionaryEntity object) {
		return object.getLabel();
	}

}
