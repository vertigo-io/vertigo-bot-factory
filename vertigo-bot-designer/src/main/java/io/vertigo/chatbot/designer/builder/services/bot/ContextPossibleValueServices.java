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
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
@Secured("BotUser")
public class ContextPossibleValueServices implements Component {

    @Inject
    ContextPossibleValueDAO contextPossibleValueDAO;


    public DtList<ContextPossibleValue> getAllContextPossibleValuesByCvaId(@SecuredOperation("botVisitor") final Chatbot bot,  final Long cvaId) {
        return contextPossibleValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextPossibleValueFields.cvaId, cvaId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<ContextPossibleValue> getAllContextPossibleValuesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return contextPossibleValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextPossibleValueFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public void deleteContextPossibleValue(@SecuredOperation("botContributor") final Chatbot bot, final Long cpvId) {
        contextPossibleValueDAO.delete(cpvId);
    }

    public void deleteContextPossibleValuesByCvaId(@SecuredOperation("botContributor") final Chatbot bot, final Long cvaId) {
        contextPossibleValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextPossibleValueFields.cvaId, cvaId), DtListState.of(null))
                .forEach(contextPossibleValue -> contextPossibleValueDAO.delete(contextPossibleValue.getCpvId()));
    }

    public ContextPossibleValue save(@SecuredOperation("botContributor") final Chatbot bot, final ContextPossibleValue contextPossibleValue) {
        return contextPossibleValueDAO.save(contextPossibleValue);
    }
}
