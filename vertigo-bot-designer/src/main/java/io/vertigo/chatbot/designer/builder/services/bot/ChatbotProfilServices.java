package io.vertigo.chatbot.designer.builder.services.bot;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.chatbot.ChatbotPAO;
import io.vertigo.chatbot.designer.dao.admin.ProfilPerChatbotDAO;
import io.vertigo.chatbot.designer.dao.commons.PersonDAO;
import io.vertigo.chatbot.designer.domain.admin.PersonChatbotProfil;
import io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.domain.commons.PersonRoleEnum;
import io.vertigo.chatbot.domain.DtDefinitions.PersonFields;
import io.vertigo.chatbot.domain.DtDefinitions.ProfilPerChatbotFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("AdmBot")
public class ChatbotProfilServices implements Component {

	@Inject
	private ChatbotPAO chatbotPAO;

	@Inject
	private ProfilPerChatbotDAO profilPerChatbotDAO;

	@Inject
	private PersonDAO personDAO;

	public DtList<PersonChatbotProfil> getPersonProfilIHMbyChatbotId(@SecuredOperation("admFct") final Chatbot chatbot) {
		return chatbotPAO.getPersonProfilIHM(chatbot.getBotId());
	}

	public DtList<PersonChatbotProfil> updateChatbotProfils(final String profil, final List<Long> persId, @SecuredOperation("admFct") final Chatbot bot) {
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
			} else {
				final ProfilPerChatbot newProfil = new ProfilPerChatbot();
				newProfil.setBotId(botId);
				newProfil.setChpCd(profil);
				newProfil.setPerId(perId);
				profilPerChatbotDAO.create(newProfil);
			}
		}
		return getPersonProfilIHMbyChatbotId(bot);
	}

	/**
	 * Get all persons with the user profil
	 *
	 * @return the list of users
	 */
	public DtList<Person> getAllUsers(@SecuredOperation("admFct") final Chatbot chatbot) {
		return personDAO.findAll(Criterions.isEqualTo(PersonFields.rolCd, PersonRoleEnum.RUser.name()), DtListState.of(100));
	}

	public void deleteProfilForChatbot(@SecuredOperation("admFct") final Chatbot chatbot, final PersonChatbotProfil persToDelete) {
		profilPerChatbotDAO.delete(persToDelete.getChpId());
	}

	public DtList<ProfilPerChatbot> getProfilByPerId(final Long perId) {
		return profilPerChatbotDAO.findAll(Criterions.isEqualTo(ProfilPerChatbotFields.perId, perId), DtListState.of(100));
	}

	public void deleteAllProfilByBot(@SecuredOperation("admFct") final Chatbot bot) {
		chatbotPAO.removeAllProfilByBotId(bot.getBotId());
	}
}
