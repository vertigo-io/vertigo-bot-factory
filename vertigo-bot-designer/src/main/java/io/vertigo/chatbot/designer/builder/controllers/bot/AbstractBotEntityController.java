package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

public abstract class AbstractBotEntityController<O extends Entity> extends AbstractBotController {

	protected static final ViewContextKey<String> breadCrumsKey = ViewContextKey.of("breadCrums");

	protected void initBreadCrums(final ViewContext viewContext, final O object) {
		viewContext.publishRef(breadCrumsKey, getBreadCrums(object));
	}

	protected abstract String getBreadCrums(final O object);


}
