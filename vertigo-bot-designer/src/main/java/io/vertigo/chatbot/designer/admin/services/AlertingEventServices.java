package io.vertigo.chatbot.designer.admin.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.dao.monitoring.AlertingEventDAO;
import io.vertigo.chatbot.designer.domain.monitoring.AlertingEvent;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;

/**
 * @author cmarechal
 * @created 17/10/2022 - 11:35
 * @project vertigo-bot-factory
 */
@Transactional
public class AlertingEventServices implements Component {

    @Inject
    private AlertingEventDAO alertingEventDAO;

    public AlertingEvent save(final AlertingEvent alertingEvent) {
        return alertingEventDAO.save(alertingEvent);
    }

    public Optional<AlertingEvent> getAlertingGlobalEventByComponentNameAndStatus(final String componentName, final boolean alive) {
        return alertingEventDAO.findOptional(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.componentName, componentName)
                .and(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.alive, alive)));
    }

    public Optional<AlertingEvent> getAlertingEventByBotIdNodeIdComponentNameAndStatus(final long botId, final long nodeId, final String componentName, final boolean alive) {
        return alertingEventDAO.findOptional(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.botId, botId)
                .and(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.nodeId, nodeId))
                .and(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.componentName, componentName))
                .and(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.alive, alive)));
    }
}
