package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.WelcomeTour;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourServices;
import io.vertigo.chatbot.designer.utils.AbstractChatbotDtObjectValidator;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.datamodel.data.definitions.DataFieldName;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/welcomeTours")
@Secured("BotUser")
public class WelcomeTourController extends AbstractBotListEntityController<WelcomeTour> {

	private static final ViewContextKey<WelcomeTour> welcomeToursKey = ViewContextKey.of("welcomeTours");

	private static final ViewContextKey<WelcomeTour> newWelcomeTourKey = ViewContextKey.of("newWelcomeTour");

	@Inject
	private WelcomeTourServices welcomeTourServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(welcomeToursKey, welcomeTourServices.findAllByBotId(botId));
		viewContext.publishDto(newWelcomeTourKey, new WelcomeTour());
		super.initBreadCrums(viewContext, WelcomeTour.class);
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/_saveWelcomeTour")
	public ViewContext saveNewWelcomeTour(final ViewContext viewContext,
										  final UiMessageStack uiMessageStack,
										  @ViewAttribute("bot") final Chatbot bot,
										  @ViewAttribute("newWelcomeTour") @Validate(WelcomeTourNotEmptyValidator.class) final WelcomeTour newWelcomeTour) {
		welcomeTourServices.save(newWelcomeTour);
		viewContext.publishDto(newWelcomeTourKey, new WelcomeTour());
		viewContext.publishDtList(welcomeToursKey, welcomeTourServices.findAllByBotId(bot.getBotId()));
		return viewContext;
	}

	@PostMapping("/_deleteWelcomeTour")
	public ViewContext deleteWelcomeTour(final ViewContext viewContext,
										 final UiMessageStack uiMessageStack,
										 @ViewAttribute("bot") final Chatbot bot,
										 @RequestParam("welId") final Long welId) {
		welcomeTourServices.delete(welId);
		viewContext.publishDtList(welcomeToursKey, welcomeTourServices.findAllByBotId(bot.getBotId()));
		return viewContext;
	}

	public static final class WelcomeTourNotEmptyValidator extends AbstractChatbotDtObjectValidator<WelcomeTour> {
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected List<DataFieldName<WelcomeTour>> getFieldsToNullCheck() {
			return List.of(DtDefinitions.WelcomeTourFields.label,
					DtDefinitions.WelcomeTourFields.technicalCode,
					DtDefinitions.WelcomeTourFields.useModalOverlay,
					DtDefinitions.WelcomeTourFields.useCancelIcon,
					DtDefinitions.WelcomeTourFields.previousButtonLabel,
					DtDefinitions.WelcomeTourFields.nextButtonLabel,
					DtDefinitions.WelcomeTourFields.completeButtonLabel);
		}
	}

}
