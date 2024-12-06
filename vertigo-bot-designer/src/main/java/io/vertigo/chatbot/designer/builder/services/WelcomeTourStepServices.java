package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.WelcomeTourStepDAO;
import io.vertigo.chatbot.commons.domain.WelcomeTourStep;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

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

	/**
	 * Regex to extract params from welcome tour step config file
	 * Ex : #[start.title Bienvenue]#", "#[start.text Bienvenue sur notre site de démo. Nous allons vous guider !]#
	 * Group 1 is the step key, group 2 the param and group 3 the default value
	 */
	private static final Pattern extractPattern = Pattern.compile("#\\[([A-Za-z1-9\\-]+)\\.([A-Za-z1-9\\-]+)\\s+(.+)\\]#");

	public WelcomeTourStep findById(final long tourStepId) {
		return welcomeTourStepDAO.get(tourStepId);
	}

	public void delete(final long tourStepId) {
		welcomeTourStepDAO.delete(tourStepId);
	}

	public void deleteAllByTourId(final long tourId) {
		findAllStepsByTourId(tourId).forEach(welcomeTourStep -> delete(welcomeTourStep.getWelStepId()));
	}

	public WelcomeTourStep save (final WelcomeTourStep welcomeTourStep) {
		return welcomeTourStepDAO.save(welcomeTourStep);
	}

	public DtList<WelcomeTourStep> findAllStepsByTourId(final long tourId) {
		return welcomeTourStepDAO.findAll(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.tourId, tourId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public Optional<WelcomeTourStep> findStepByTourIdAndStepId(final long tourId, final String stepId) {
		return welcomeTourStepDAO.findOptional(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.tourId, tourId)
				.and(Criterions.isEqualTo(DtDefinitions.WelcomeTourStepFields.internalStepId, stepId)));
	}

	public List<WelcomeTourStep> readStepsFromConfigString(final String config) {
		final List<WelcomeTourStep> steps = new ArrayList<>();
		final Map<String, Map<String, String>> parsed = parseFile(config);
		Long sequence = 1L;
		for (final Map.Entry<String, Map<String, String>> entry : parsed.entrySet()) {
			final WelcomeTourStep welcomeTourStep = new WelcomeTourStep();
			final Map<String, String> attributes = entry.getValue();
			welcomeTourStep.setInternalStepId(entry.getKey());
			if (!attributes.containsKey("title")) {
				throw new VUserException("Missing 'title' attribute on step '{0}'", entry.getKey());
			}
			welcomeTourStep.setTitle(attributes.get("title"));
			if (!attributes.containsKey("text")) {
				throw new VUserException("Missing 'text' attribute on step '{0}'", entry.getKey());
			}
			welcomeTourStep.setText(attributes.get("text"));
			welcomeTourStep.setEnabled("true".equals(attributes.getOrDefault("enabled", "true")));
			welcomeTourStep.setSequence(sequence);
			steps.add(welcomeTourStep);
			sequence++;
		}
		return steps;
	}

	private Map<String, Map<String, String>> parseFile(final String file) {
		final Matcher matcher = extractPattern.matcher(file);
		//Extracting groups and build a map where the key is the step key and the value is a map of param/default value
		return matcher.results().collect(Collectors.groupingBy(m -> m.group(1),
				Collectors.toMap(m -> m.group(2), m -> m.group(3))));
	}

	public String parseFile(final String file, final long welcomeTourId) {
		final DtList<WelcomeTourStep> steps = findAllStepsByTourId(welcomeTourId);
		final Matcher matcher = extractPattern.matcher(file);
		return matcher.replaceAll(match -> {
			final String shepherdStepId = match.group(1);
			final String attributeName = match.group(2);
			final String attributeValue = match.group(3);
			final Optional<WelcomeTourStep> optWelcomeTourStep = steps.stream().filter(step -> step.getInternalStepId().equals(shepherdStepId)).findFirst();
			if (optWelcomeTourStep.isEmpty()) {
				throw new VSystemException("Step with internal ID '" + shepherdStepId + "' was not found in database. Could not complete training ...");
			}
			final WelcomeTourStep welcomeTourStep = optWelcomeTourStep.get();
			switch (attributeName) {
				case "title":
					return welcomeTourStep.getTitle();
				case "text":
					return welcomeTourStep.getText();
				case "enabled":
					return welcomeTourStep.getEnabled().toString();
				default:
					return attributeValue;
			}
		});
	}


}
