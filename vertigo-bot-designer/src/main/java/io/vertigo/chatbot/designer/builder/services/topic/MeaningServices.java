package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.MeaningDAO;
import io.vertigo.chatbot.designer.dao.SynonymDAO;
import io.vertigo.chatbot.designer.domain.Meaning;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.chatbot.domain.DtDefinitions.MeaningFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
public class MeaningServices implements Component {

	@Inject
	private MeaningDAO meaningDAO;

	@Inject
	private SynonymDAO synonymDAO;

	//	@Inject
	//	private meaningPAO meaningPAO;

	public Meaning findmeaningById(@SecuredOperation("botVisitor") final Long id) {
		return meaningDAO.get(id);
	}

	public Meaning save(final Meaning meaning) {
		return meaningDAO.save(meaning);
	}

	public Meaning save(@SecuredOperation("botContributor") final Meaning meaning,
			final DtList<Synonym> synonyms,
			final DtList<Synonym> synonymsToDelete) {

		saveAllNotBlankSynonym(meaning, synonyms);
		removeSynonym(synonymsToDelete);

		return meaningDAO.save(meaning);
	}

	public void deleteMeaning(@SecuredOperation("botContributor") final Chatbot bot, final Long meaId) {
		meaningDAO.delete(meaId);
	}

	public DtList<Meaning> getAllMeaningByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return meaningDAO.findAll(Criterions.isEqualTo(MeaningFields.botId, bot.getBotId()), DtListState.of(1000));
	}

	protected DtList<Synonym> saveAllNotBlankSynonym(final Meaning meaning, final DtList<Synonym> synonyms) {
		// save nlu textes
		final DtList<Synonym> synToSave = synonyms.stream()
				.filter(syn -> !StringUtil.isBlank(syn.getLabel()))
				.collect(VCollectors.toDtList(Synonym.class));

		for (final Synonym syn : synToSave) {
			syn.setMeaId(meaning.getMeaId());
			syn.setBotId(meaning.getBotId());
			synonymDAO.save(syn);
		}

		return synToSave;
	}

	public void removeSynonym(final DtList<Synonym> synonymsToDelete) {
		synonymsToDelete.stream()
				.filter(itt -> itt.getSynId() != null)
				.forEach(itt -> synonymDAO.delete(itt.getSynId()));
	}

	public Meaning findMeaningByLabelAndBotId(final String label, final Long botId) {
		final Optional<Meaning> result = meaningDAO.getMeaningByLabelAndBotId(botId, label);
		return result.isPresent() ? result.get() : null;
	}

}
