package io.vertigo.chatbot.designer.builder.services;

import java.util.Comparator;

import io.vertigo.chatbot.commons.dao.WelcomeTourStepDAO;
import io.vertigo.chatbot.commons.domain.WelcomeTourStep;
import io.vertigo.chatbot.designer.builder.welcomeTourStep.WelcomeTourStepPAO;
import io.vertigo.chatbot.designer.utils.SequenceNormalizer;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;
import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

/**
 * Services de gestion des etapes d'un welcome tour, y compris leur ordre d'affichage.
 *
 * @author Chatbot Team
 */
@Transactional
public class WelcomeTourStepServices implements Component {

	@Inject
	private WelcomeTourStepDAO welcomeTourStepDAO;

	@Inject
	private WelcomeTourStepPAO welcomeTourStepPAO;

	public WelcomeTourStep findById(final long tourStepId) {
		return welcomeTourStepDAO.get(tourStepId);
	}

	/**
	 * Supprime une etape puis reecrit les sequences restantes du tour en 1..N.
	 *
	 * @param tourId identifiant du welcome tour
	 * @param tourStepId identifiant de l'etape a supprimer
	 */
	public void deleteStepAndReorder(final long tourId, final long tourStepId) {
		delete(tourStepId);
		normalizeWelcomeTourStepSequences(tourId);
	}

	public void delete(final long tourStepId) {
		welcomeTourStepDAO.delete(tourStepId);
	}

	public void deleteAllByTourId(final long tourId) {
		findAllStepsByTourId(tourId).forEach(welcomeTourStep -> delete(welcomeTourStep.getWelStepId()));
	}

	/**
	 * Enregistre une etape. Une creation recoit la prochaine sequence puis le tour est normalise.
	 *
	 * @param welcomeTourStep etape a enregistrer
	 * @return l'etape persistee
	 */
	public WelcomeTourStep save (final WelcomeTourStep welcomeTourStep) {
		final boolean isNew = welcomeTourStep.getWelStepId() == null;
		if (isNew) {
			welcomeTourStep.setSequence(welcomeTourStepPAO.getNextWelcomeTourStepSequence(welcomeTourStep.getTourId()));
		}
		final WelcomeTourStep saved = welcomeTourStepDAO.save(welcomeTourStep);
		if (isNew) {
			normalizeWelcomeTourStepSequences(saved.getTourId());
		}
		return saved;
	}

	public DtList<WelcomeTourStep> findAllStepsByTourId(final long tourId) {
		return welcomeTourStepDAO.findAll(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.tourId, tourId), DtListState.of(MAX_ELEMENTS_PLUS_ONE))
				.stream().sorted(Comparator.comparing(WelcomeTourStep::getSequence)).collect(VCollectors.toDtList(WelcomeTourStep.class));
	}

	/**
	 * Deplace une etape vers le haut ou le bas en echangeant sa sequence avec celle de son voisin, puis normalise le tour.
	 *
	 * @param stepId identifiant de l'etape a deplacer
	 * @param moveUp true pour remonter l'etape, false pour la descendre
	 * @throws VUserException si l'etape n'a pas de voisin dans la direction demandee
	 */
	public void moveStep(final Long stepId, final boolean moveUp) {
		final WelcomeTourStep step = findById(stepId);
		final WelcomeTourStep neighborStep;
		if (moveUp) {
			neighborStep = welcomeTourStepDAO.findWelcomeTourStepPreviousNeighbor(step.getTourId(), step.getSequence());
		} else {
			neighborStep = welcomeTourStepDAO.findWelcomeTourStepNextNeighbor(step.getTourId(), step.getSequence());
		}

		if (neighborStep != null) {
			final Long tempSequence = step.getSequence();
			step.setSequence(neighborStep.getSequence());
			neighborStep.setSequence(tempSequence);
			welcomeTourStepDAO.save(step);
			welcomeTourStepDAO.save(neighborStep);
			normalizeWelcomeTourStepSequences(step.getTourId());
		} else {
			throw new VUserException("Can't move step " + stepId + " because it is out of sequence");
		}
	}

	/**
	 * Reecrit les sequences des etapes d'un tour en 1..N uniques, en conservant l'ordre relatif
	 * (sequence croissante, puis identifiant).
	 *
	 * @param tourId identifiant du welcome tour
	 */
	public void normalizeWelcomeTourStepSequences(final long tourId) {
		final DtList<WelcomeTourStep> steps = findAllStepsByTourId(tourId);
		final boolean changed = SequenceNormalizer.applyDenseSequences(
				steps,
				WelcomeTourStep::getSequence,
				WelcomeTourStep::getWelStepId,
				WelcomeTourStep::setSequence);
		if (changed) {
			steps.forEach(welcomeTourStepDAO::save);
		}
	}

}
