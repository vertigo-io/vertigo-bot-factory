package io.vertigo.chatbot.designer.builder.services;

import java.time.LocalDate;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.SavedTrainingDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.SavedTraining;
import io.vertigo.chatbot.commons.domain.SavedTrainingCriteria;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class SavedTrainingServices implements Component {

	@Inject
	private SavedTrainingDAO savedTrainingDAO;

	@Secured("BotUser")
	public SavedTraining save(@SecuredOperation("botAdm") final Chatbot bot,  final SavedTraining savedTraining) {
		savedTrainingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.SavedTrainingFields.botId, savedTraining.getBotId())
				.and(Criterions.isEqualTo(DtDefinitions.SavedTrainingFields.traId, savedTraining.getTraId())))
				.ifPresent(it -> {
					throw new VSystemException("A saved training already exist on this bot for the same version");
				});
		return savedTrainingDAO.save(savedTraining);
	}

	@Secured("BotUser")
	public void delete(@SecuredOperation("botAdm") final Chatbot bot, final Long id) {
		savedTrainingDAO.delete(id);
	}

	public DtList<SavedTraining> getAllSavedTrainingByBotId(final Long botId) {
		return savedTrainingDAO.findAll(Criterions.isEqualTo(DtDefinitions.SavedTrainingFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteAllByBotId(final Chatbot bot) {
		getAllSavedTrainingByBotId(bot.getBotId()).forEach(savedTraining -> delete(bot, savedTraining.getSavedTraId()));
	}

	public SavedTraining getById(final Long savedTraId) {
		return savedTrainingDAO.get(savedTraId);
	}

	private DtList<SavedTraining> findAllByBotIdAndBetweenDates(final Long botId, final LocalDate fromDate, final LocalDate toDate) {
		return savedTrainingDAO.getSavedTrainingByBotIdAndWithDateBetween(botId, fromDate, toDate);
	}

	private DtList<SavedTraining> findAllByBotIdAndWithDateAfter(final Long botId, final LocalDate fromDate) {
		return savedTrainingDAO.getSavedTrainingByBotIdAndWithDateAfter(botId, fromDate);
	}

	private DtList<SavedTraining> findAllByBotIdAndWithDateBefore(final Long botId, final LocalDate toDate) {
		return savedTrainingDAO.getSavedTrainingByBotIdAndWithDateBefore(botId, toDate);
	}

	public DtList<SavedTraining> filter(final Chatbot bot, final SavedTrainingCriteria criteria) {
		DtList<SavedTraining> filteredSavedTraining;
		if (criteria.getFromDate() == null && criteria.getToDate() == null) {
			filteredSavedTraining = getAllSavedTrainingByBotId(bot.getBotId());
		} else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
			filteredSavedTraining = findAllByBotIdAndWithDateAfter(bot.getBotId(), criteria.getFromDate());
		} else if (criteria.getFromDate() == null && criteria.getToDate() != null) {
			filteredSavedTraining = findAllByBotIdAndWithDateBefore(bot.getBotId(), criteria.getToDate().plusDays(1));
		} else {
			filteredSavedTraining = findAllByBotIdAndBetweenDates(bot.getBotId(), criteria.getFromDate(), criteria.getToDate().plusDays(1));
		}

		if (criteria.getText() != null) {
			filteredSavedTraining = filteredSavedTraining.stream().filter(savedTraining -> {
				savedTraining.training().load();
				return savedTraining.getName().toLowerCase().contains(criteria.getText().toLowerCase()) ||
						savedTraining.training().get().getVersionNumber().toString().toLowerCase().contains(criteria.getText().toLowerCase());
			}).collect(VCollectors.toDtList(SavedTraining.class));
		}
		return filteredSavedTraining;
	}
}
