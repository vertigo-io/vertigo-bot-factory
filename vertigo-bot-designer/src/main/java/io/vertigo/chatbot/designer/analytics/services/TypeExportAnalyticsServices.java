package io.vertigo.chatbot.designer.analytics.services;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.designer.dao.analytics.TypeExportAnalyticsDAO;
import io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

@Transactional
@Secured("BotUser")
public class TypeExportAnalyticsServices implements Component {

	@Inject
	private TypeExportAnalyticsDAO typeExportAnalyticsDAO;

	public DtList<TypeExportAnalytics> getAllTypeExportAnalytics() {
		return typeExportAnalyticsDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

}
