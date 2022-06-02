package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.WelcomeTourStep;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourStepServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/welcomeToursSteps/")
@Secured("BotUser")
public class WelcomeTourStepListController extends AbstractBotListEntityController<WelcomeTourStep>{

	private static final ViewContextKey<WelcomeTourStep> stepsKey = ViewContextKey.of("steps");
	private static final ViewContextKey<WelcomeTourStep> newStepKey = ViewContextKey.of("newStep");

	@Inject
	private WelcomeTourStepServices welcomeTourStepServices;

	@GetMapping("/{tourId}")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack,
							@PathVariable("botId") final Long botId,
							@PathVariable("tourId") final Long tourId) {

		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(stepsKey, welcomeTourStepServices.findAllStepsByTourId(tourId));
		viewContext.publishDto(newStepKey, new WelcomeTourStep());
		super.initBreadCrums(viewContext, WelcomeTourStep.class);
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/{tourId}/_saveWelcomeTourStep")
	public ViewContext saveWelcomeTourStep(final ViewContext viewContext,
										   final UiMessageStack uiMessageStack,
										   @PathVariable("botId") final Long botId,
										   @PathVariable("tourId") final Long tourId,
										   @RequestParam("welStepId") final Long welStepId,
										   @RequestParam("title") final String title,
										   @RequestParam("text") final String text,
										   @RequestParam("enabled") final Boolean enabled) {

		final WelcomeTourStep welcomeTourStep = welcomeTourStepServices.findById(welStepId);
		welcomeTourStep.setTitle(title);
		welcomeTourStep.setText(text);
		welcomeTourStep.setEnabled(enabled);
		welcomeTourStepServices.save(welcomeTourStep);
		viewContext.publishDtList(stepsKey, welcomeTourStepServices.findAllStepsByTourId(tourId));
		return viewContext;
	}

}
