package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.ui.core.ViewContext;

public abstract class AbstractBotCreationController<D extends Entity> extends AbstractBotEntityController<D> {

	public void initEmptyBreadcrums(final ViewContext viewContext) {
		viewContext.publishRef(breadCrumsKey, "");
	}
}
