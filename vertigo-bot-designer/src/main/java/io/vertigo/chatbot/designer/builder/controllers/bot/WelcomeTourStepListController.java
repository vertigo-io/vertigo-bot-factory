package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.*;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourServices;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourStepAdvanceEventService;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourStepPlacementService;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourStepServices;
import io.vertigo.chatbot.designer.utils.AbstractChatbotDtObjectValidator;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.datamodel.data.definitions.DataFieldName;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.List;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/welcomeToursSteps/")
@Secured("BotUser")
public class WelcomeTourStepListController extends AbstractBotListEntityController<WelcomeTourStep>{

	private static final ViewContextKey<WelcomeTourStep> stepsKey = ViewContextKey.of("steps");
	private static final ViewContextKey<WelcomeTourStep> newStepKey = ViewContextKey.of("newStep");
	private static final ViewContextKey<WelcomeTour> tourKey = ViewContextKey.of("tour");
	private static final ViewContextKey<WelcomeTourStepPlacement> welcomeTourStepPlacementKey = ViewContextKey.of("welcomeTourStepPlacement");
	private static final ViewContextKey<WelcomeTourStepAdvanceEvent> welcomeTourStepAdvanceEventKey = ViewContextKey.of("welcomeTourStepAdvanceEvent");

	@Inject
	private WelcomeTourStepServices welcomeTourStepServices;

	@Inject
	private WelcomeTourServices welcomeTourServices;

	@Inject
	private WelcomeTourStepPlacementService welcomeTourStepPlacementService;

	@Inject
	private WelcomeTourStepAdvanceEventService welcomeTourStepAdvanceEventService;

	@GetMapping("/{tourId}")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack,
							@PathVariable("botId") final Long botId,
							@PathVariable("tourId") final Long tourId) {

		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(stepsKey, welcomeTourStepServices.findAllStepsByTourId(tourId));
		viewContext.publishDto(tourKey, welcomeTourServices.findById(tourId));
		viewContext.publishDto(newStepKey, new WelcomeTourStep());
		viewContext.publishDtList(welcomeTourStepPlacementKey, welcomeTourStepPlacementService.findAll());
		viewContext.publishDtList(welcomeTourStepAdvanceEventKey, welcomeTourStepAdvanceEventService.findAll());
		super.initBreadCrums(viewContext, WelcomeTourStep.class);
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/_saveWelcomeTourStep")
	public ViewContext saveWelcomeTourStep(final ViewContext viewContext,
										   final UiMessageStack uiMessageStack,
										   @ViewAttribute("bot") final Chatbot bot,
										   @ViewAttribute("tour") final WelcomeTour tour,
										   @ViewAttribute("newStep") @Validate(WelcomeTourStepNotEmptyValidator.class) final WelcomeTourStep welcomeTourStep) {

		welcomeTourStepServices.save(welcomeTourStep);
		viewContext.publishDtList(stepsKey, welcomeTourStepServices.findAllStepsByTourId(tour.getWelId()));
		return viewContext;
	}

	@PostMapping("/_moveStep")
	public ViewContext moveStep(final ViewContext viewContext,
								final UiMessageStack uiMessageStack,
								@ViewAttribute("bot") final Chatbot bot,
								@ViewAttribute("tour") final WelcomeTour tour,
								@RequestParam("stepId") final Long stepId,
								@RequestParam("moveUp") final boolean moveUp) {

		welcomeTourStepServices.moveStep(stepId, moveUp);
		viewContext.publishDtList(stepsKey, welcomeTourStepServices.findAllStepsByTourId(tour.getWelId()));

		return viewContext;
	}

	@PostMapping("/_deleteWelcomeTourStep")
	public ViewContext deleteWelcomeTour(final ViewContext viewContext,
										 final UiMessageStack uiMessageStack,
										 @ViewAttribute("bot") final Chatbot bot,
										 @ViewAttribute("tour") final WelcomeTour tour,
										 @RequestParam("welStepId") final Long welStepId) {
		welcomeTourStepServices.deleteStepAndReorder(tour.getWelId(), welStepId);
		viewContext.publishDtList(stepsKey, welcomeTourStepServices.findAllStepsByTourId(tour.getWelId()));
		return viewContext;
	}

	public static final class WelcomeTourStepNotEmptyValidator extends AbstractChatbotDtObjectValidator<WelcomeTourStep> {
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected List<DataFieldName<WelcomeTourStep>> getFieldsToNullCheck() {
			return List.of(DtDefinitions.WelcomeTourStepFields.tourId,
					DtDefinitions.WelcomeTourStepFields.title,
					DtDefinitions.WelcomeTourStepFields.text,
					DtDefinitions.WelcomeTourStepFields.elementAttachTo,
					DtDefinitions.WelcomeTourStepFields.elementAttachToPlacement,
					DtDefinitions.WelcomeTourStepFields.displayNextButton,
					DtDefinitions.WelcomeTourStepFields.displayPreviousButton);
		}
	}

}
