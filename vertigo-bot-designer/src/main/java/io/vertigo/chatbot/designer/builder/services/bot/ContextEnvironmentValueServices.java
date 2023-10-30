package io.vertigo.chatbot.designer.builder.services.bot;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.dao.ContextEnvironmentValueDAO;
import io.vertigo.chatbot.designer.domain.ContextEnvironmentValue;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

/**
 * @author cmarechal
 * @created 27/10/2023 - 11:38
 * @project vertigo-bot-factory
 */
@Transactional
public class ContextEnvironmentValueServices implements Component {

    @Inject
    private ContextEnvironmentValueDAO contextEnvironmentValueDAO;

    public ContextEnvironmentValue save(final ContextEnvironmentValue contextEnvironmentValue) {
        return contextEnvironmentValueDAO.save(contextEnvironmentValue);
    }

    public ContextEnvironmentValue findById(final Long cenvalId) {
        return contextEnvironmentValueDAO.get(cenvalId);
    }

    public void deleteContextEnvironmentValue(final Long cvaId) {
        contextEnvironmentValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextEnvironmentValueFields.cvaId, cvaId), DtListState.of(null))
                .forEach(contextEnvironmentValue -> contextEnvironmentValueDAO.delete(contextEnvironmentValue.getCenvalId()));
    }

    public void deleteAllValueByContextEnvironment(Long cenvId) {
        findAllContextEnvironmentValuesByEnv(cenvId).forEach(contextEnvironmentValue -> contextEnvironmentValueDAO.delete(contextEnvironmentValue.getCenvalId()));
    }

    public DtList<ContextEnvironmentValue> findAllContextEnvironmentValuesByEnv(final Long cenvId) {
        return contextEnvironmentValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextEnvironmentValueFields.cenvId, cenvId), DtListState.of(null));
    }
}
