package io.vertigo.chatbot.designer.builder.services.bot;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.TypeOperatorDAO;
import io.vertigo.chatbot.designer.domain.TypeOperator;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

@Transactional
public class ContextTypeOperatorServices implements Component {

    @Inject
    TypeOperatorDAO typeOperatorDAO;

    public DtList<TypeOperator> getAllTypeOperators(final Chatbot bot){
        return typeOperatorDAO.findAll(Criterions.alwaysTrue(), DtListState.of(null));
    }
}
