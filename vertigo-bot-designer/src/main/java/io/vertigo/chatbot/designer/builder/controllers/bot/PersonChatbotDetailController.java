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
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotProfilServices;
import io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles;
import io.vertigo.chatbot.designer.domain.admin.PersonChatbotProfil;
import io.vertigo.chatbot.designer.domain.admin.SelectProfilChatbotPerson;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;

@Controller
@RequestMapping("/bot/{botId}/personChatbot")
@Secured("BotUser")
public class PersonChatbotDetailController extends AbstractCommonBotController {

	// for all users
	private static final ViewContextKey<Person> personsListKey = ViewContextKey.of("personList");
	// All the profils of chatbot
	private static final ViewContextKey<PersonChatbotProfil> personsProfilListKey = ViewContextKey.of("personProfilList");
	// All persons with profil on the chatbot
	private static final ViewContextKey<ChatbotProfiles> chatbotProfilList = ViewContextKey.of("chatbotProfilesList");
	// return of the select
	private static final ViewContextKey<SelectProfilChatbotPerson> selectionList = ViewContextKey.of("selectionList");

	@Inject
	private ChatbotProfilServices chatbotProfilServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot chatbot = initCommonContext(viewContext, botId);
		viewContext.publishDtListModifiable(personsProfilListKey, chatbotProfilServices.getPersonProfilIHMbyChatbotId(chatbot));
		viewContext.publishMdl(chatbotProfilList, ChatbotProfiles.class, null);
		viewContext.publishDtListModifiable(personsListKey, chatbotProfilServices.getAllUsers(chatbot));
		viewContext.publishDto(selectionList, new SelectProfilChatbotPerson());
		toModeReadOnly();
	}

	@PostMapping("/_addUsers")
	public void addUsersToProfil(final ViewContext viewContext, @ViewAttribute("selectionList") final SelectProfilChatbotPerson selection, @ViewAttribute("bot") final Chatbot chatbot) {
		final DtList<PersonChatbotProfil> newList = chatbotProfilServices.updateChatbotProfils(selection.getPrfId(), selection.getPerId(), chatbot);
		viewContext.publishDtListModifiable(personsProfilListKey, newList);
		viewContext.publishDto(selectionList, new SelectProfilChatbotPerson());
	}

	@PostMapping("/_delete")
	public ViewContext deleteUser(final ViewContext viewContext, @ViewAttribute("personProfilList") final DtList<PersonChatbotProfil> persons,
			@RequestParam("rowId") final Long chpId, @ViewAttribute("bot") final Chatbot bot) {
		final PersonChatbotProfil persToDelete = persons.stream()
				.filter(pers -> pers.getChpId().equals(chpId))
				.findFirst().orElseThrow(() -> new VSystemException("the person and profil was not found"));
		chatbotProfilServices.deleteProfilForChatbot(bot, persToDelete);
		persons.remove(persToDelete);
		viewContext.publishDtListModifiable(personsProfilListKey, persons);
		return viewContext;
	}

}
