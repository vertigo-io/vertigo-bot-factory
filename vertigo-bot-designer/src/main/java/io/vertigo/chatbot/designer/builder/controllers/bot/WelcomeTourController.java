package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.WelcomeTour;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.AbstractDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
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

	/**
	 * Check if value field is not empty or meaningless html.
	 */
	public static final class WelcomeTourNotEmptyValidator extends AbstractDtObjectValidator<WelcomeTour> {

		/** {@inheritDoc} */
		@Override
		protected void checkMonoFieldConstraints(final WelcomeTour welcomeTour, final DtField dtField, final DtObjectErrors dtObjectErrors) {
			if (DtDefinitions.WelcomeTourFields.label.name().equals(dtField.getName())
					|| DtDefinitions.WelcomeTourFields.technicalCode.name().equals(dtField.getName())) {
				final String value = (String) dtField.getDataAccessor().getValue(welcomeTour);
				if (value == null || "".equals(value)) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
		}
	}

}
