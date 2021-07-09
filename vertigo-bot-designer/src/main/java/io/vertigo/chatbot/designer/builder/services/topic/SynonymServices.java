package io.vertigo.chatbot.designer.builder.services.topic;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.SynonymDAO;
import io.vertigo.chatbot.designer.domain.Meaning;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.chatbot.domain.DtDefinitions.SynonymFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class SynonymServices implements Component {

	@Inject
	private SynonymDAO synonymDAO;

	public Synonym findSynonymById(@SecuredOperation("botVisitor") final Long id) {
		return synonymDAO.get(id);
	}

	public Synonym save(final Synonym synonym) {
		return synonymDAO.save(synonym);
	}

	public void deleteSynonym(@SecuredOperation("botContributor") final Chatbot bot, final Long synId) {
		synonymDAO.delete(synId);
	}

	public DtList<Synonym> getAllSynonymByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return synonymDAO.findAll(Criterions.isEqualTo(SynonymFields.botId, bot.getBotId()), DtListState.of(1000));
	}

	public DtList<Synonym> getAllSynonymByMeaning(@SecuredOperation("botVisitor") final Meaning meaning) {

		return synonymDAO.getSynonymByMeaning(meaning.getMeaId());
	}

}
