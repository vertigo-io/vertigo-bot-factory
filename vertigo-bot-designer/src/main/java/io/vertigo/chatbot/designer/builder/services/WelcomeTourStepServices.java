package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.WelcomeTourStepDAO;
import io.vertigo.chatbot.commons.domain.WelcomeTourStep;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class WelcomeTourStepServices implements Component {

	@Inject
	private WelcomeTourStepDAO welcomeTourStepDAO;

	private final Pattern extractPattern = Pattern.compile("#\\[([A-Za-z1-9\\-]+)\\.([A-Za-z1-9\\-]+)\\s+(.+)\\]#");

	public WelcomeTourStep findById(final long tourStepId) {
		return welcomeTourStepDAO.get(tourStepId);
	}

	public void delete(final long tourStepId) {
		welcomeTourStepDAO.delete(tourStepId);
	}

	public void deleteAllByTourId(final long tourId) {
		findAllStepsByTourId(tourId).forEach(sheperdTourStep -> delete(sheperdTourStep.getWelStepId()));
	}

	public WelcomeTourStep save (final WelcomeTourStep welcomeTourStep) {
		return welcomeTourStepDAO.save(welcomeTourStep);
	}

	public DtList<WelcomeTourStep> findAllStepsByTourId(final long tourId) {
		return welcomeTourStepDAO.findAll(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.tourId, tourId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public Optional<WelcomeTourStep> findStepByTourIdAndStepId(final long tourId, final String stepId) {
		return welcomeTourStepDAO.findOptional(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.tourId, tourId)
				.and(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.id, stepId)));
	}

	public List<WelcomeTourStep> readStepsFromConfigString(final String config) {
		final List<WelcomeTourStep> steps = new ArrayList<>();
		final Map<String, Map<String, String>> parsed = parseFile(config);
		Long sequence = 1L;
		for (final Map.Entry<String, Map<String, String>> entry : parsed.entrySet()) {
			final WelcomeTourStep welcomeTourStep = new WelcomeTourStep();
			welcomeTourStep.setTitle(entry.getValue().get("title"));
			welcomeTourStep.setId(entry.getKey());
			welcomeTourStep.setText(entry.getValue().get("text"));
			welcomeTourStep.setEnabled("true".equals(entry.getValue().get("enabled")));
			welcomeTourStep.setSequence(sequence);
			steps.add(welcomeTourStep);
			sequence++;
		}
		return steps;
	}

	private Map<String, Map<String, String>> parseFile(final String file) {
		final Matcher matcher = extractPattern.matcher(file);
		return matcher.results().collect(Collectors.groupingBy(m -> m.group(1),
				Collectors.toMap(m -> m.group(2), m -> m.group(3))));
	}

	public String parseFile(final String file, final long welcomeTourId) {
		final DtList<WelcomeTourStep> steps = findAllStepsByTourId(welcomeTourId);
		final Matcher matcher = extractPattern.matcher(file);
		return matcher.replaceAll(match -> {
			final Optional<WelcomeTourStep> optSheperdTourStep = steps.stream().filter(step -> step.getId().equals(match.group(1))).findFirst();
			if (optSheperdTourStep.isEmpty()) {
				return match.group(3);
			} else {
				final WelcomeTourStep sheperdTourStep = optSheperdTourStep.get();
				if ("title".equals(match.group(2))) {
					return sheperdTourStep.getTitle();
				} else if ("text".equals(match.group(2))) {
					return sheperdTourStep.getText();
				} else if ("enabled".equals(match.group(2))) {
					return sheperdTourStep.getEnabled().toString();
				} else {
					return match.group(3);
				}
			}
		});
	}


}
