package io.vertigo.chatbot.designer.analytics.services;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.designer.dao.analytics.RatingOptionDAO;
import io.vertigo.chatbot.designer.domain.analytics.RatingOption;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
@Secured("BotUser")
public class RatingOptionServices implements Component {

	@Inject
	private RatingOptionDAO ratingOptionDAO;

	public DtList<RatingOption> getAllRatingOptions() {
		return ratingOptionDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}
}
