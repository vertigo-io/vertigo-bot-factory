package io.vertigo.chatbot.designer.utils;

import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.datamodel.structure.definitions.DtFieldName;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.ui.core.AbstractUiListUnmodifiable;
import io.vertigo.ui.core.UiListUnmodifiable;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.vega.engines.webservice.json.AbstractUiListModifiable;
import io.vertigo.vega.webservice.model.UiList;
import io.vertigo.vega.webservice.validation.UiMessageStack;

public final class ListUtils {

	public static final int MAX_ELEMENTS = 1000;
	public static final int MAX_ELEMENTS_PLUS_ONE = MAX_ELEMENTS + 1;

	private ListUtils () {

	}

	public static void listLimitReached(final ViewContext viewContext, final UiMessageStack uiMessageStack) {
		viewContext.asMap().forEach((key, value ) -> {
			if ((value instanceof AbstractUiListModifiable || value instanceof UiListUnmodifiable) &&  ((UiList<?>) value).size() >= MAX_ELEMENTS_PLUS_ONE) {
				if (value instanceof AbstractUiListUnmodifiable) {
					DtList<DtObject> dtList = viewContext.readDtList(ViewContextKey.of(key), uiMessageStack);
					DtFieldName<DtObject> dtFieldName = ((AbstractUiListUnmodifiable<?>) value)::getIdFieldName;
					viewContext.publishDtList(ViewContextKey.of(key),dtFieldName, dtList.stream().limit(MAX_ELEMENTS)
							.collect(VCollectors.toDtList(dtList.getDefinition())));
				} else {
					DtList<DtObject> dtList = viewContext.readDtListModifiable(ViewContextKey.of(key), uiMessageStack);
					viewContext.publishDtList(ViewContextKey.of(key), dtList.stream().limit(MAX_ELEMENTS)
							.collect(VCollectors.toDtList(dtList.getDefinition())));
				}
				uiMessageStack.info(LocaleMessageText.of(UtilsMultilingualResources.LIST_TOO_BIG, MAX_ELEMENTS).getDisplay());
			}
		});
	}
}
