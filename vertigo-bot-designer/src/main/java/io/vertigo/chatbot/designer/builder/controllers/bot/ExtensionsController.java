package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.ConfluenceSetting;
import io.vertigo.chatbot.commons.domain.WelcomeTour;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.ConfluenceSettingServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
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

@Controller
@RequestMapping("/bot/{botId}/extensions")
@Secured("botAdm")
public class ExtensionsController extends AbstractBotController {

	private static final ViewContextKey<WelcomeTour> welcomeToursKey = ViewContextKey.of("welcomeTours");

	private static final ViewContextKey<WelcomeTour> newWelcomeTourKey = ViewContextKey.of("newWelcomeTour");

	private static final ViewContextKey<ConfluenceSetting> confluenceSettingsKey = ViewContextKey.of("confluenceSettings");

	private static final ViewContextKey<ConfluenceSetting> confluenceSettingsFilteredKey = ViewContextKey.of("confluenceSettingsFiltered");

	private static final ViewContextKey<ConfluenceSetting> newConfluenceSettingKey = ViewContextKey.of("newConfluenceSetting");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");

	@Inject
	private WelcomeTourServices welcomeTourServices;

	@Inject
	private ConfluenceSettingServices confluenceSettingServices;

	@Inject
	private NodeServices nodeServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = super.initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(welcomeToursKey, welcomeTourServices.findAllByBotId(botId));
		viewContext.publishDto(newWelcomeTourKey, new WelcomeTour());
		DtList<ConfluenceSetting> confluenceSettings = confluenceSettingServices.findAllByBotId(botId);
		viewContext.publishDtList(confluenceSettingsKey, confluenceSettings);
		viewContext.publishDtList(confluenceSettingsFilteredKey, confluenceSettings);
		viewContext.publishDto(newConfluenceSettingKey, new ConfluenceSetting());
		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		super.initBreadCrums(viewContext, "EXTENSION");
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

	@PostMapping("/_saveConfluenceSetting")
	public ViewContext saveConfluenceSetting(final ViewContext viewContext,
										  final UiMessageStack uiMessageStack,
										  @ViewAttribute("bot") final Chatbot bot,
										  @ViewAttribute("newConfluenceSetting")  @Validate(ConfluenceSettingNotEmptyValidator.class) final ConfluenceSetting confluenceSetting) {

		confluenceSettingServices.save(confluenceSetting);

		DtList<ConfluenceSetting> confluenceSettings = confluenceSettingServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(confluenceSettingsKey, confluenceSettings);
		viewContext.publishDtList(confluenceSettingsFilteredKey, confluenceSettings);
		return viewContext;
	}

	@PostMapping("/_deleteConfluenceSetting")
	public ViewContext deleteConfluenceSetting(final ViewContext viewContext,
										 final UiMessageStack uiMessageStack,
										 @ViewAttribute("bot") final Chatbot bot,
										 @RequestParam("conSetId") final Long conSetId) {
		confluenceSettingServices.delete(conSetId);
		DtList<ConfluenceSetting> confluenceSettings = confluenceSettingServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(confluenceSettingsKey, confluenceSettings);
		viewContext.publishDtList(confluenceSettingsFilteredKey, confluenceSettings);
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

	public static final class ConfluenceSettingNotEmptyValidator extends AbstractDtObjectValidator<ConfluenceSetting> {

		/** {@inheritDoc} */
		@Override
		protected void checkMonoFieldConstraints(final ConfluenceSetting confluenceSetting, final DtField dtField, final DtObjectErrors dtObjectErrors) {
			if (DtDefinitions.ConfluenceSettingFields.url.name().equals(dtField.getName())
					|| DtDefinitions.ConfluenceSettingFields.login.name().equals(dtField.getName())
					|| DtDefinitions.ConfluenceSettingFields.password.name().equals(dtField.getName())
			) {
				final String value = (String) dtField.getDataAccessor().getValue(confluenceSetting);
				if (value == null || value.trim().isEmpty()) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
			if (DtDefinitions.ConfluenceSettingFields.numberOfResults.name().equals(dtField.getName())) {
				final Long value = (Long) dtField.getDataAccessor().getValue(confluenceSetting);
				if (value == null || value <= 0) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.VALUE_SHOULD_BE_GREATER_THAN_ZERO));
				}
			}
			if (DtDefinitions.ConfluenceSettingFields.nodId.name().equals(dtField.getName())) {
				final Long value = (Long) dtField.getDataAccessor().getValue(confluenceSetting);
				if (value == null) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
		}
	}

}
