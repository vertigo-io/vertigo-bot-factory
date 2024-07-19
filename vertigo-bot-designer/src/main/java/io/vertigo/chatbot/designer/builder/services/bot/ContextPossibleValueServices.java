package io.vertigo.chatbot.designer.builder.services.bot;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.ContextPossibleValueDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextPossibleValue;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("BotUser")
public class ContextPossibleValueServices implements Component {

    @Inject
    ContextPossibleValueDAO contextPossibleValueDAO;


    public DtList<ContextPossibleValue> getAllContextPossibleValuesByCvaId(@SecuredOperation("botVisitor") final Chatbot bot,  final Long cvaId) {
        return contextPossibleValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextPossibleValueFields.cvaId, cvaId), DtListState.of(null));
    }

    public void deleteContextPossibleValue(@SecuredOperation("botContributor") final Chatbot bot, final Long cpvId) {
        contextPossibleValueDAO.delete(cpvId);
    }
    public ContextPossibleValue save(@SecuredOperation("botContributor") final Chatbot bot, final ContextPossibleValue contextPossibleValue) {
        return contextPossibleValueDAO.save(contextPossibleValue);
    }
}
