package io.vertigo.chatbot.designer.analytics.services;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.designer.dao.analytics.TypeExportAnalyticsDAO;
import io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
@Secured("BotUser")
public class TypeExportAnalyticsServices implements Component {

	@Inject
	private TypeExportAnalyticsDAO typeExportAnalyticsDAO;

	public DtList<TypeExportAnalytics> getAllTypeExportAnalytics() {
		return typeExportAnalyticsDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}
	public DtList<TypeExportAnalytics> getNonBotRelatedTypeExportAnalytics() {
		return typeExportAnalyticsDAO.findAll(Criterions.isEqualTo(DtDefinitions.TypeExportAnalyticsFields.botRelated, false), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public DtList<TypeExportAnalytics> getBotRelatedTypeExportAnalytics() {
		return typeExportAnalyticsDAO.findAll(Criterions.isEqualTo(DtDefinitions.TypeExportAnalyticsFields.botRelated, true), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}


}
