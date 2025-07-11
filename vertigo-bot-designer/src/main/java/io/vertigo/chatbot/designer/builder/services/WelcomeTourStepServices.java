package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.WelcomeTourStepDAO;
import io.vertigo.chatbot.commons.dao.WelcomeTourStepPlacementDAO;
import io.vertigo.chatbot.commons.domain.WelcomeTourStep;
import io.vertigo.chatbot.commons.domain.WelcomeTourStepPlacement;
import io.vertigo.chatbot.designer.builder.welcomeTourStep.WelcomeTourStepPAO;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class WelcomeTourStepServices implements Component {

	@Inject
	private WelcomeTourStepDAO welcomeTourStepDAO;

	@Inject
	private WelcomeTourStepPAO welcomeTourStepPAO;

	public WelcomeTourStep findById(final long tourStepId) {
		return welcomeTourStepDAO.get(tourStepId);
	}

	public void deleteStepAndReorder(final long tourId, final long tourStepId) {
		WelcomeTourStep step = findById(tourStepId);
		Long sequence = step.getSequence();
		delete(tourStepId);
		welcomeTourStepPAO.reorderWelcomeTourStepAfterStepDelete(tourId, sequence);
	}

	public void delete(final long tourStepId) {
		welcomeTourStepDAO.delete(tourStepId);
	}

	public void deleteAllByTourId(final long tourId) {
		findAllStepsByTourId(tourId).forEach(welcomeTourStep -> delete(welcomeTourStep.getWelStepId()));
	}

	public WelcomeTourStep save (final WelcomeTourStep welcomeTourStep) {
		if (welcomeTourStep.getWelStepId() == null) {
			welcomeTourStep.setSequence(welcomeTourStepPAO.getNextWelcomeTourStepSequence(welcomeTourStep.getTourId()));
		}
		return welcomeTourStepDAO.save(welcomeTourStep);
	}

	public DtList<WelcomeTourStep> findAllStepsByTourId(final long tourId) {
		return welcomeTourStepDAO.findAll(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.tourId, tourId), DtListState.of(MAX_ELEMENTS_PLUS_ONE))
				.stream().sorted(Comparator.comparing(WelcomeTourStep::getSequence)).collect(VCollectors.toDtList(WelcomeTourStep.class));
	}

	public void moveStep(final Long stepId, boolean moveUp) {
		WelcomeTourStep step = findById(stepId);
		WelcomeTourStep neighborStep;
		if (moveUp) {
			neighborStep = welcomeTourStepDAO.findWelcomeTourStepPreviousNeighbor(step.getTourId(), step.getSequence());
		} else {
			neighborStep = welcomeTourStepDAO.findWelcomeTourStepNextNeighbor(step.getTourId(), step.getSequence());
		}

		if (neighborStep != null) {
			Long tempSequence = step.getSequence();
			step.setSequence(neighborStep.getSequence());
			neighborStep.setSequence(tempSequence);
			welcomeTourStepDAO.save(step);
			welcomeTourStepDAO.save(neighborStep);
		} else {
			throw new VUserException("Can't move step " + stepId + " because it is out of sequence");
		}
	}

}
