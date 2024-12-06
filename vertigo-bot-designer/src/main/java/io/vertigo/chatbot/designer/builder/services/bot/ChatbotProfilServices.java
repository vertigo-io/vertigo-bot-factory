package io.vertigo.chatbot.designer.builder.services.bot;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.chatbot.ChatbotPAO;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.IRecordable;
import io.vertigo.chatbot.designer.dao.admin.ProfilPerChatbotDAO;
import io.vertigo.chatbot.designer.dao.commons.PersonDAO;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.designer.domain.admin.PersonChatbotProfil;
import io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.domain.commons.PersonRoleEnum;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.chatbot.domain.DtDefinitions.PersonFields;
import io.vertigo.chatbot.domain.DtDefinitions.ProfilPerChatbotFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
@Secured("BotUser")
public class ChatbotProfilServices implements Component, IRecordable<ProfilPerChatbot> {

	@Inject
	private ChatbotPAO chatbotPAO;

	@Inject
	private ProfilPerChatbotDAO profilPerChatbotDAO;

	@Inject
	private PersonDAO personDAO;

	@Inject
	private HistoryServices historyServices;

	public DtList<PersonChatbotProfil> getPersonProfilIHMbyChatbotId(@SecuredOperation("botAdm") final Chatbot chatbot) {
		return chatbotPAO.getPersonProfilIHM(chatbot.getBotId());
	}

	public DtList<PersonChatbotProfil> updateChatbotProfils(final String profil, final List<Long> persId, @SecuredOperation("botAdm") final Chatbot bot) {
		final Long botId = bot.getBotId();
		for (final Long perId : persId) {
			Criteria<ProfilPerChatbot> criteria = Criterions.isEqualTo(ProfilPerChatbotFields.botId, botId);
			criteria = criteria.and(Criterions.isEqualTo(ProfilPerChatbotFields.perId, perId));

			final Optional<ProfilPerChatbot> optional = profilPerChatbotDAO.findOptional(criteria);
			//Only one profil by chatbot
			if (optional.isPresent()) {
				final ProfilPerChatbot profilLoad = optional.get();
				profilLoad.setChpCd(profil);
				profilPerChatbotDAO.update(profilLoad);
				record(bot, profilLoad, HistoryActionEnum.UPDATED);
			} else {
				final ProfilPerChatbot newProfil = new ProfilPerChatbot();
				newProfil.setBotId(botId);
				newProfil.setChpCd(profil);
				newProfil.setPerId(perId);
				profilPerChatbotDAO.create(newProfil);
				record(bot, newProfil, HistoryActionEnum.ADDED);
			}
		}
		return getPersonProfilIHMbyChatbotId(bot);
	}

	/**
	 * Get all persons with the user profil
	 *
	 * @return the list of users
	 */
	public DtList<Person> getAllUsers(@SecuredOperation("botAdm") final Chatbot chatbot) {
		return personDAO.findAll(Criterions.isEqualTo(PersonFields.rolCd, PersonRoleEnum.RUser.name()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteProfilForChatbot(@SecuredOperation("botAdm") final Chatbot chatbot, final PersonChatbotProfil persToDelete) {
		ProfilPerChatbot profilPerChatbot = profilPerChatbotDAO.get(persToDelete.getChpId());
		profilPerChatbotDAO.delete(persToDelete.getChpId());
		record(chatbot, profilPerChatbot, HistoryActionEnum.DELETED);
	}

	public DtList<ProfilPerChatbot> getProfilByPerId() {
		final Long perId = UserSessionUtils.getLoggedPerson().getPerId();
		return getProfilByPerId(perId);
	}

	private DtList<ProfilPerChatbot> getProfilByPerId(Long perId) {
		return profilPerChatbotDAO.findAll(Criterions.isEqualTo(ProfilPerChatbotFields.perId, perId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteProfilByPerId(Long perId) {
		getProfilByPerId(perId).forEach(profilPerChatbot -> profilPerChatbotDAO.delete(profilPerChatbot.getChpId()));
	}

	public void deleteAllProfilByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		chatbotPAO.removeAllProfilByBotId(bot.getBotId());
	}

	@Override
	public History record(Chatbot bot, ProfilPerChatbot profilPerChatbot, HistoryActionEnum action) {
		profilPerChatbot.person().load();
		Person person = profilPerChatbot.person().get();
		return historyServices.record(bot, action, profilPerChatbot.getClass().getSimpleName(), person.getName() + " - " + profilPerChatbot.getChpCd());
	}
}
