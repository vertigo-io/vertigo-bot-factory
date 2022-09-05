package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.ui.core.ViewContext;

public abstract class AbstractBotListEntityController<D extends Entity> extends AbstractBotEntityController<D> {

	private static final String LIST_KEY = "_LIST";

	@SuppressWarnings("unchecked")
	@Override
	public String getBreadCrums(final D object) {
		final Class<D> clazz = (Class<D>) object.getClass();
		return getBreadCrums(clazz);
	}

	public String getBreadCrums(final Class<D> clazz) {
		final String keyMessage = clazz.getSimpleName().toUpperCase() + LIST_KEY;
		return MessageText.of(BotMultilingualResources.valueOf(keyMessage)).getDisplay();
	}

	public void initBreadCrums(final ViewContext viewContext, final Class<D> clazz) {
		viewContext.publishRef(breadCrumsKey, getBreadCrums(clazz));
	}

}
