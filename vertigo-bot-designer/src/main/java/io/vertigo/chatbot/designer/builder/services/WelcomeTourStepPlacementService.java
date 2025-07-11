package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.WelcomeTourStepPlacementDAO;
import io.vertigo.chatbot.commons.domain.WelcomeTourStepPlacement;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class WelcomeTourStepPlacementService implements Component {

    @Inject
    private WelcomeTourStepPlacementDAO welcomeTourStepPlacementDAO;

    public DtList<WelcomeTourStepPlacement> findAll() {
        return welcomeTourStepPlacementDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }
}
