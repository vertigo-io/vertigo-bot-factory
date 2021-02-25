package io.vertigo.chatbot.designer.admin.services.bot;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.dao.admin.ProfilPerChatbotDAO;
import io.vertigo.chatbot.designer.domain.admin.PersonChatbotProfil;
import io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot;
import io.vertigo.chatbot.domain.DtDefinitions.ProfilPerChatbotFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class ChatbotProfilServices implements Component {

	@Inject
	private ProfilPerChatbotDAO profilPerChatbotDAO;

	public DtList<PersonChatbotProfil> getPersonProfilIHMbyChatbotId(Long botId) {
		DtList<ProfilPerChatbot> profilsPerChatbot = this.profilPerChatbotDAO.findAll(Criterions.isEqualTo(ProfilPerChatbotFields.botId, botId), DtListState.of(1000));
		DtList<PersonChatbotProfil> personIHMList = new DtList<PersonChatbotProfil>(PersonChatbotProfil.class);
		for (ProfilPerChatbot profil : profilsPerChatbot) {
			profil.person().load();
			PersonChatbotProfil persoIHM = new PersonChatbotProfil();
			persoIHM.setBotId(botId);
			persoIHM.setChpId(profil.getChpId());
			persoIHM.setName(profil.person().get().getName());
			persoIHM.setProfilLabel(profil.getChpCd());
			personIHMList.add(persoIHM);
		}
		return personIHMList;
	}

	public DtList<PersonChatbotProfil> updateChatbotProfils(String profil, List<Long> persId, Long botId) {
		for (Long perId : persId) {
			Criteria<ProfilPerChatbot> criteria = Criterions.isEqualTo(ProfilPerChatbotFields.botId, botId);
			criteria = criteria.and(Criterions.isEqualTo(ProfilPerChatbotFields.perId, perId));

			Optional<ProfilPerChatbot> optional = this.profilPerChatbotDAO.findOptional(criteria);
			//Only one profil by chatbot
			if (optional.isPresent()) {
				ProfilPerChatbot profilLoad = optional.get();
				profilLoad.setChpCd(profil);
				this.profilPerChatbotDAO.update(profilLoad);
			} else {
				ProfilPerChatbot newProfil = new ProfilPerChatbot();
				newProfil.setBotId(botId);
				newProfil.setChpCd(profil);
				newProfil.setPerId(perId);
				this.profilPerChatbotDAO.create(newProfil);
			}
		}
		return this.getPersonProfilIHMbyChatbotId(botId);
	}

	public void deleteProfilForChatbot(PersonChatbotProfil persToDelete) {
		this.profilPerChatbotDAO.delete(persToDelete.getChpId());
	}
}
