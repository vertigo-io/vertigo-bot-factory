package io.vertigo.chatbot.designer.utils;

import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.datamodel.data.definitions.DataFieldName;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.ui.core.AbstractUiListUnmodifiable;
import io.vertigo.ui.core.UiListUnmodifiable;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.vega.engines.webservice.json.AbstractUiListModifiable;
import io.vertigo.vega.webservice.model.UiList;
import io.vertigo.vega.webservice.validation.UiMessageStack;

/**
 * Utility class for list operations.
 * Provides methods to manage list size limits in the UI context.
 *
 * @author Chatbot Team
 */
public final class ListUtils {

	public static final int MAX_ELEMENTS = 2000;
	public static final int MAX_ELEMENTS_PLUS_ONE = MAX_ELEMENTS + 1;

	private ListUtils () {

	}

	/**
	 * Check if any list in the view context exceeds the maximum size and truncate it if necessary.
	 * Displays an information message when a list is truncated.
	 *
	 * @param viewContext view context containing the lists to check
	 * @param uiMessageStack message stack for displaying information messages
	 */
	public static void listLimitReached(final ViewContext viewContext, final UiMessageStack uiMessageStack) {
		viewContext.asMap().forEach((key, value ) -> {
			if ((value instanceof AbstractUiListModifiable || value instanceof UiListUnmodifiable) &&  ((UiList<?>) value).size() >= MAX_ELEMENTS_PLUS_ONE) {
				if (value instanceof AbstractUiListUnmodifiable) {
					final DtList<DataObject> dtList = viewContext.readDtList(ViewContextKey.of(key), uiMessageStack);
					final DataFieldName<DataObject> dtFieldName = ((AbstractUiListUnmodifiable<?>) value)::getIdFieldName;
					viewContext.publishDtList(ViewContextKey.of(key),dtFieldName, dtList.stream().limit(MAX_ELEMENTS)
							.collect(VCollectors.toDtList(dtList.getDefinition())));
				} else {
					final DtList<DataObject> dtList = viewContext.readDtListModifiable(ViewContextKey.of(key), uiMessageStack);
					viewContext.publishDtList(ViewContextKey.of(key), dtList.stream().limit(MAX_ELEMENTS)
							.collect(VCollectors.toDtList(dtList.getDefinition())));
				}
				uiMessageStack.info(LocaleMessageText.of(UtilsMultilingualResources.LIST_TOO_BIG, MAX_ELEMENTS).getDisplay());
			}
		});
	}
}
