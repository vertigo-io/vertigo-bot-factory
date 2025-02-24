package io.vertigo.chatbot.designer.admin.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;
import jakarta.mail.MessagingException;

import io.vertigo.chatbot.commons.AntivirusServices;
import io.vertigo.chatbot.commons.MailService;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.RunnerHealthCheck;
import io.vertigo.chatbot.designer.builder.monitoring.MonitoringPAO;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.training.TrainingServices;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.domain.monitoring.AlertingEvent;
import io.vertigo.chatbot.designer.domain.monitoring.MonitoringAlertingSubscription;
import io.vertigo.chatbot.designer.domain.monitoring.MonitoringBotDetailIHM;
import io.vertigo.chatbot.designer.domain.monitoring.MonitoringDetailIHM;
import io.vertigo.chatbot.designer.domain.monitoring.MonitoringRunnerDetailIHM;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.daemon.DaemonScheduled;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.datamodel.data.model.DtList;

/**
 * @author cmarechal
 * @created 12/10/2022 - 11:44
 * @project vertigo-bot-factory
 */

@Transactional
public class MonitoringServices implements Component, Activeable {

    @Inject
    private ParamManager paramManager;
    @Inject
    private AntivirusServices antivirusServices;

    @Inject
    private ChatbotServices chatbotServices;

    @Inject
    private NodeServices nodeServices;

    @Inject
    private TrainingServices trainingServices;

    @Inject
    private MonitoringAlertingSubscriptionServices monitoringAlertingSubscriptionServices;

    @Inject
    private MonitoringPAO monitoringPAO;

    @Inject
    private AlertingEventServices alertingEventServices;

    @Inject
    private MailService mailService;

    private String alertingEmailAddress;

    private static final Logger LOGGER = LogManager.getLogger(MonitoringServices.class);

    @Override
    public void start() {
       alertingEmailAddress = paramManager.getOptionalParam("ALERTING_EMAIL_ADDRESS").map(Param::getValueAsString).orElse("alerte-chatbot@kleegroup.com");
    }

    public MonitoringDetailIHM monitore() {
        final MonitoringAlertingSubscription monitoringAlertingSubscription = monitoringAlertingSubscriptionServices.getCurrentUserSubscriptionOrDefault();

        final MonitoringDetailIHM monitoringDetail = new MonitoringDetailIHM();
        monitoringDetail.setGlobalAlertingEnabled(monitoringAlertingSubscription.getAlertingGlobal());
        monitoringDetail.setAntivirusAlive(antivirusServices.healthCheck());
        final DtList<MonitoringBotDetailIHM> monitoringBotDetailIHMDtList = new DtList<>(MonitoringBotDetailIHM.class);
        chatbotServices.getMySupervisedChatbots().forEach(chatbot -> {
            final MonitoringBotDetailIHM monitoringBotDetail = new MonitoringBotDetailIHM();
            monitoringBotDetail.setBotId(chatbot.getBotId());
            monitoringBotDetail.setName(chatbot.getName());
            if (monitoringAlertingSubscription.getMasID() != null) {
                monitoringAlertingSubscription.chatbot().load();
                monitoringBotDetail.setAlertingEnabled(monitoringAlertingSubscription.chatbot().get().stream()
                        .anyMatch(monitoredBot -> monitoredBot.getBotId().equals(chatbot.getBotId())));
            } else {
                monitoringBotDetail.setAlertingEnabled(false);
            }
            final DtList<MonitoringRunnerDetailIHM> monitoringRunnerDetailIHMDtList = new DtList<>(MonitoringRunnerDetailIHM.class);
            nodeServices.getNodesByBot(chatbot).forEach(chatbotNode -> {
                final MonitoringRunnerDetailIHM monitoringRunnerDetailIHM = new MonitoringRunnerDetailIHM();
                monitoringRunnerDetailIHM.setName(chatbotNode.getName());
                final RunnerHealthCheck runnerHealthCheck = trainingServices.tryPing(chatbotNode);
                monitoringRunnerDetailIHM.setAlive(runnerHealthCheck.getAlive());
                monitoringRunnerDetailIHM.setRasaAlive(runnerHealthCheck.getNlpReady());
                monitoringRunnerDetailIHMDtList.add(monitoringRunnerDetailIHM);
            });
            monitoringBotDetail.setRunners(monitoringRunnerDetailIHMDtList);
            monitoringBotDetailIHMDtList.add(monitoringBotDetail);
        });
        monitoringDetail.setBots(monitoringBotDetailIHMDtList);
        return monitoringDetail;
    }

    public void subscribeToBot(final long botId, final boolean enabled ) {
        MonitoringAlertingSubscription monitoringAlertingSubscription = monitoringAlertingSubscriptionServices.getCurrentUserSubscriptionOrDefault();
        if (monitoringAlertingSubscription.getMasID() == null) {
            monitoringAlertingSubscription = monitoringAlertingSubscriptionServices.save(monitoringAlertingSubscription);
        }
        if (enabled) {
            monitoringPAO.addInNNAlertingSubscriptionChatbot(monitoringAlertingSubscription.getMasID(), botId);
        } else {
            monitoringPAO.removeFromNNAlertingSubscriptionChatbot(monitoringAlertingSubscription.getMasID(), botId);
        }
    }

    public void subscribeToGlobalAlerts(final MonitoringDetailIHM monitoringDetailIHM) {
        final MonitoringAlertingSubscription monitoringAlertingSubscription = monitoringAlertingSubscriptionServices.getCurrentUserSubscriptionOrDefault();
        monitoringAlertingSubscription.setAlertingGlobal(monitoringDetailIHM.getGlobalAlertingEnabled());
        monitoringAlertingSubscriptionServices.save(monitoringAlertingSubscription);
    }

    @DaemonScheduled(name = "DmnMonitoring", periodInSeconds = 600)
    public void monitoringDaemon() {
        final DtList<MonitoringAlertingSubscription> monitoringAlertingSubscriptions = monitoringAlertingSubscriptionServices.getAll();
        final boolean antivirusHealth = antivirusServices.healthCheck();
        handleAlertingEventForGlobalComponent("Antivirus", antivirusHealth, monitoringAlertingSubscriptions);
        chatbotServices.getAllChatbotsForDaemons().forEach(chatbot -> {
            nodeServices.getAllNodesByBotsForMonitoring(chatbot).forEach(node -> {
                final RunnerHealthCheck runnerHealthCheck = trainingServices.tryPing(node);
                handleAlertingEventForBotNodeComponent(chatbot, node, "Runner", runnerHealthCheck.getAlive(), monitoringAlertingSubscriptions);
                handleAlertingEventForBotNodeComponent(chatbot, node, "Rasa", runnerHealthCheck.getNlpReady(), monitoringAlertingSubscriptions);
            });
        });
    }


    private void handleAlertingEventForGlobalComponent(final String component, final boolean alive,
                                                       final DtList<MonitoringAlertingSubscription> monitoringAlertingSubscriptionDtList) {
        final Optional<AlertingEvent> optAlertingGlobalEvent = alertingEventServices
                .getAlertingGlobalEventByComponentNameAndStatus(component, false);
        if (!alive && optAlertingGlobalEvent.isEmpty()) {
            sendMailForGlobalMonitoringSubscription(monitoringAlertingSubscriptionDtList, component, "KO");
            final AlertingEvent alertingGlobalEvent = new AlertingEvent();
            alertingGlobalEvent.setComponentName(component);
            alertingGlobalEvent.setDate(Instant.now());
            alertingGlobalEvent.setAlive(false);
            alertingEventServices.save(alertingGlobalEvent);
        }
        if (alive && optAlertingGlobalEvent.isPresent()) {
            sendMailForGlobalMonitoringSubscription(monitoringAlertingSubscriptionDtList, component, "OK");
            final AlertingEvent alertingGlobalEvent = optAlertingGlobalEvent.get();
            alertingGlobalEvent.setAlive(true);
            alertingEventServices.save(alertingGlobalEvent);
        }

    }
    private void handleAlertingEventForBotNodeComponent(final Chatbot chatbot, final ChatbotNode node, final String component, final boolean alive,
                                                        final DtList<MonitoringAlertingSubscription> monitoringAlertingSubscriptionDtList) {
        final Optional<AlertingEvent> optAlertingEvent = alertingEventServices
                .getAlertingEventByBotIdNodeIdComponentNameAndStatus(chatbot.getBotId(), node.getNodId(), component, false);
        if (!alive && optAlertingEvent.isEmpty()) {
            final AlertingEvent alertingEventRunner = new AlertingEvent();
            alertingEventRunner.setComponentName(component);
            alertingEventRunner.setDate(Instant.now());
            alertingEventRunner.setBotId(chatbot.getBotId());
            alertingEventRunner.setNodeId(node.getNodId());
            alertingEventRunner.setAlive(false);
            alertingEventServices.save(alertingEventRunner);
            sendMailForBotMonitoringSubscription(monitoringAlertingSubscriptionDtList, component, chatbot, node, "KO");
        }
        if (alive && optAlertingEvent.isPresent()) {
            final AlertingEvent alertingEventRunner = optAlertingEvent.get();
            alertingEventRunner.setAlive(true);
            alertingEventServices.save(alertingEventRunner);
            sendMailForBotMonitoringSubscription(monitoringAlertingSubscriptionDtList, component, chatbot, node, "OK");
        }
    }

    private void sendMailForGlobalMonitoringSubscription(final DtList<MonitoringAlertingSubscription> monitoringAlertingSubscriptionDtList,
                                                         final String componentName, final String state) {
        monitoringAlertingSubscriptionDtList.stream().filter(MonitoringAlertingSubscription::getAlertingGlobal).forEach(monitoringAlertingSubscription -> {
            monitoringAlertingSubscription.person().load();
            final Person person = monitoringAlertingSubscription.person().get();
            if (person.getEmail() != null) {
                try {
                    LOGGER.debug("Sending mail to " + person.getName() + " at " + person.getEmail() + "with " + componentName + " status : " + state);
                    mailService.sendAlertingGlobalMail(alertingEmailAddress, person.getEmail(), componentName, state);
                } catch (final MessagingException e) {
                    throw new VSystemException("Could not send alerting mail to " + person.getName(), e);
                }
            }
        });
    }

    private void sendMailForBotMonitoringSubscription(final DtList<MonitoringAlertingSubscription> monitoringAlertingSubscriptionDtList,
                                                         final String componentName, final Chatbot chatbot, final ChatbotNode node, final String state) {
        monitoringAlertingSubscriptionDtList.stream().filter(monitoringAlertingSubscription -> {
            monitoringAlertingSubscription.chatbot().load();
            return monitoringAlertingSubscription.chatbot().get().stream().anyMatch(monitoredBot -> monitoredBot.getBotId().equals(chatbot.getBotId()));
        }).forEach(monitoringAlertingSubscription -> {
            monitoringAlertingSubscription.person().load();
            final Person person = monitoringAlertingSubscription.person().get();
            if (person.getEmail() != null) {
                try {
                    LOGGER.debug("Sending mail to " + person.getName() + " at " + person.getEmail() + "with " + componentName + " status : " + state);
                    mailService.sendAlertingBotMail(alertingEmailAddress, person.getEmail(), chatbot.getName(), node.getName(), componentName, state);
                } catch (final MessagingException e) {
                    throw new VSystemException("Could not send alerting mail to " + person.getName(), e);
                }
            }
        });
    }

    @Override
    public void stop() {

    }
}
