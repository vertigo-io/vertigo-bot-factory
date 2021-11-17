package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.SynonymDAO;
import io.vertigo.chatbot.designer.domain.DictionaryExport;
import io.vertigo.chatbot.designer.domain.Meaning;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.chatbot.domain.DtDefinitions.SynonymFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class SynonymServices implements Component {

	@Inject
	private SynonymDAO synonymDAO;

	public Synonym findSynonymById(final Long id) {
		return synonymDAO.get(id);
	}

	public Synonym save(final Synonym synonym) {
		return synonymDAO.save(synonym);
	}

	public void delete(final Long synId) {
		synonymDAO.delete(synId);
	}

	public DtList<Synonym> getAllSynonymByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		return synonymDAO.findAll(Criterions.isEqualTo(SynonymFields.botId, bot.getBotId()), DtListState.of(1000));
	}

	public DtList<Synonym> getAllSynonymByMeaning(@SecuredOperation("botVisitor") final Meaning meaning) {

		return synonymDAO.getSynonymByMeaning(meaning.getMeaId());
	}

	public Optional<Synonym> findSynonymByLabelAndMeaId(final Long meaId, final String label) {
		final Criteria<Synonym> criteria = Criterions.isEqualTo(SynonymFields.meaId, meaId).and(Criterions.isEqualTo(SynonymFields.label, label));
		return synonymDAO.findOptional(criteria);
	}

	public void removeSynonym(final DtList<Synonym> synonymsToDelete) {
		synonymsToDelete.stream()
				.filter(itt -> itt.getSynId() != null)
				.forEach(itt -> delete(itt.getSynId()));
	}

	/*
	 * Return a list of Response from DictionaryExport
	 */
	public DtList<Synonym> extractSynonymsFromDictionaryExport(final DictionaryExport dex, final Meaning meaning) {

		final String[] listSynonyms = dex.getSynonymsList().split("\\|");

		final DtList<Synonym> synonyms = new DtList<>(Synonym.class);
		//When a sentence is added to the list, first the unicity is checked
		for (final String newSynonym : listSynonyms) {
			final Synonym syn = new Synonym();
			syn.setLabel(newSynonym);
			syn.setMeaId(meaning.getMeaId());
			syn.setBotId(meaning.getBotId());
			synonyms.add(syn);
		}
		return synonyms;
	}

}
