package io.vertigo.chatbot.designer.builder.services;


import javax.inject.Inject;

import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.WelcomeTourDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.WelcomeTour;
import io.vertigo.chatbot.commons.domain.WelcomeTourExport;
import io.vertigo.chatbot.commons.domain.WelcomeTourStep;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.impl.codec.html.HtmlCodec;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;
import org.apache.commons.text.StringEscapeUtils;

import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class WelcomeTourServices implements Component {

	@Inject
	private WelcomeTourDAO welcomeTourDAO;

	@Inject
	private WelcomeTourStepServices welcomeTourStepServices;

	public WelcomeTour findById(final long id)  {
		return welcomeTourDAO.get(id);
	}

	public WelcomeTour save (final WelcomeTour welcomeTour) {
		return welcomeTourDAO.save(welcomeTour);
	}

	public void delete (final long id) {
		welcomeTourStepServices.deleteAllByTourId(id);
		welcomeTourDAO.delete(id);
	}

	public DtList<WelcomeTour> findAllByBotId(final long botId) {
		return welcomeTourDAO.findAll(Criterions.isEqualTo(DtDefinitions.WelcomeTourFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteAllByBotId(final long botId) {
		findAllByBotId(botId).forEach(welcomeTour -> delete(welcomeTour.getWelId()));
	}

	public DtList<WelcomeTourExport> exportBotWelcomeTours(final Chatbot bot, final StringBuilder logs) {
		LogsUtils.addLogs(logs, "Welcome tours export...");
		final DtList<WelcomeTourExport> welcomeTourExports = findAllByBotId(bot.getBotId()).stream().map(welcomeTour -> {
			final WelcomeTourExport welcomeTourExport = new WelcomeTourExport();
			welcomeTourExport.setTechnicalCode(welcomeTour.getTechnicalCode());
			welcomeTourExport.setLabel(welcomeTour.getLabel());
			String config = "{\n" +
						"useModalOverlay: " + welcomeTour.getUseModalOverlay() + ",\n" +
						"defaultStepOptions: {\n" +
						"    cancelIcon: {\n" +
						"       enabled: " + welcomeTour.getUseCancelIcon() + "\n" +
						"    },\n" +
						"    scrollTo: { \n" +
						"		behavior: 'smooth', \n" +
						"       block: 'center' \n" +
						"    }\n" +
						"},\n" +
						"steps: [\n" +
							buildStepsConfig(welcomeTour) + "\n" +
						"]\n" +
					"}";
			welcomeTourExport.setConfig(config);
			return welcomeTourExport;
		}).collect(VCollectors.toDtList(WelcomeTourExport.class));
		LogsUtils.logOK(logs);
		return welcomeTourExports;
	}

	private String buildStepsConfig(WelcomeTour welcomeTour) {
		DtList<WelcomeTourStep> steps = welcomeTourStepServices.findAllStepsByTourId(welcomeTour.getWelId());
		int size = steps.size();
		return steps.stream().map(step -> {
			String stepId =  welcomeTour.getTechnicalCode().toLowerCase() + "_step_" + step.getSequence();
			String stepConfig =  "{\n" +
					"  	id: \"" + stepId + "\",\n" +
					"	title: \"" + StringEscapeUtils.escapeEcmaScript(step.getTitle()) + "\" ,\n" +
					"	text: \"" + StringEscapeUtils.escapeEcmaScript(step.getText()) + "\",\n" +
					"	attachTo: {\n" +
					"		element: document.evaluate('//body//*[text()=\"" + StringEscapeUtils.escapeEcmaScript(step.getElementAttachTo()) + "\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue,\n" +
					"		on: \"" + step.getElementAttachToPlacement().toLowerCase() + "\"\n" +
					"	},\n";
					if (welcomeTour.getStepsCssClasses() != null) {
						stepConfig = stepConfig +
							"	classes: \"" + welcomeTour.getStepsCssClasses() + "\",\n";
					}
					if (step.getAdvanceOn() != null && step.getEventAdvanceOn() != null) {
						stepConfig = stepConfig +
							"	advanceOn: {\n" +
							"		selector: document.evaluate('//*[text()=\"" + step.getAdvanceOn() + "\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue,\n" +
							"		event: \"" + step.getEventAdvanceOn().toLowerCase() + "\"\n" +
							"	},";
					}
					if (step.getDisplayPreviousButton() || step.getDisplayNextButton()) {
						stepConfig = stepConfig + getStepButtons(welcomeTour, step, size);
					}
					stepConfig = stepConfig +
					"	showOn() {\n" +
					"		return " + step.getEnabled() + "\n" +
					"	},\n" +
					"	when: {\n" +
					"		show: function() {\n" +
					"			sessionStorage.currentWelcomeTourStep = \"" + stepId + "\";\n" +
					"		},\n" +
					"		cancel: function () {\n" +
					"			sessionStorage.removeItem(\"currentWelcomeTourStep\");\n" +
					"		}\n" +
					"	}\n" +
					"}";
			return stepConfig;
		}).collect(Collectors.joining(","));
	}


	private String getStepButtons(WelcomeTour welcomeTour, WelcomeTourStep step, int size) {
		String buttons = "buttons: [\n";
		if (step.getDisplayPreviousButton() && step.getSequence() != 1) {
			buttons = buttons + "{\n" +
					"				action() {\n" +
					"					return this.back();\n" +
					"				},\n" +
					"				text: \"" + StringEscapeUtils.escapeEcmaScript(welcomeTour.getPreviousButtonLabel()) + "\"\n" +
					"			},";
		}
		if (step.getDisplayNextButton()) {
			boolean isLastStep = step.getSequence() == size;
			buttons = buttons + "{\n" +
					"				action() {\n" +
					"					return " + (isLastStep ? "this.complete()" : "this.next()") + "\n" +
					"				},\n" +
					"				text: \"" + StringEscapeUtils.escapeEcmaScript(isLastStep ? welcomeTour.getCompleteButtonLabel() : welcomeTour.getNextButtonLabel()) + "\"\n" +
					"			}";
		}
		buttons = buttons + "],";
		return buttons;
	}
}
