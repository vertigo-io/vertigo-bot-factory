package io.vertigo.chatbot.designer.admin.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.dao.monitoring.MonitoringAlertingSubscriptionDAO;
import io.vertigo.chatbot.designer.domain.monitoring.MonitoringAlertingSubscription;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

/**
 * @author cmarechal
 * @created 13/10/2022 - 16:51
 * @project vertigo-bot-factory
 */
@Transactional
public class MonitoringAlertingSubscriptionServices implements Component {

    @Inject
    private MonitoringAlertingSubscriptionDAO monitoringAlertingSubscriptionDAO;

    public MonitoringAlertingSubscription save (final MonitoringAlertingSubscription monitoringAlertingSubscription) {
        return monitoringAlertingSubscriptionDAO.save(monitoringAlertingSubscription);
    }

    public DtList<MonitoringAlertingSubscription> getAll() {
       return monitoringAlertingSubscriptionDAO.findAll(Criterions.alwaysTrue(), DtListState.of(null));
    }

    public Optional<MonitoringAlertingSubscription> getSubscriptionForUser(final long perId) {
        return monitoringAlertingSubscriptionDAO.findOptional(Criterions.isEqualTo(DtDefinitions.MonitoringAlertingSubscriptionFields.perId, perId));
    }

    public MonitoringAlertingSubscription getCurrentUserSubscriptionOrDefault() {
        return getSubscriptionForUser(UserSessionUtils.getLoggedPerson().getPerId())
                        .orElseGet(this::getDefaultMonitoringAlertingSubscription);
    }

    public MonitoringAlertingSubscription getDefaultMonitoringAlertingSubscription () {
        final MonitoringAlertingSubscription monitoringAlertingSubscription = new MonitoringAlertingSubscription();
        monitoringAlertingSubscription.setPerId(UserSessionUtils.getLoggedPerson().getPerId());
        monitoringAlertingSubscription.setAlertingGlobal(false);
        return monitoringAlertingSubscription;
    }
}
