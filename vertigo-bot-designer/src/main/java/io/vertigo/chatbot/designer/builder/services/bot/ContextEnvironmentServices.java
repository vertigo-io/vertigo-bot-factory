package io.vertigo.chatbot.designer.builder.services.bot;

import java.util.Comparator;
import java.util.Locale;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TypeOperator;
import io.vertigo.chatbot.designer.dao.ContextEnvironmentDAO;
import io.vertigo.chatbot.designer.domain.ContextEnvironment;
import io.vertigo.chatbot.designer.domain.ContextEnvironmentIhm;
import io.vertigo.chatbot.designer.domain.ContextEnvironmentValue;
import io.vertigo.chatbot.designer.domain.ContextEnvironmentValueIhm;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;
import org.glassfish.jersey.message.internal.LocaleProvider;

import javax.inject.Inject;

/**
 * @author cmarechal
 * @created 27/10/2023 - 09:45
 * @project vertigo-bot-factory
 */
@Transactional
public class ContextEnvironmentServices implements Component {

    @Inject
    private ContextEnvironmentDAO contextEnvironmentDAO;

    @Inject
    private ContextEnvironmentValueServices contextEnvironmentValueServices;

    @Inject
    private ContextValueServices contextValueServices;

    @Inject
    private LocaleManager localeManager;

    @Secured("BotUser")
    public ContextEnvironment save(@SecuredOperation("botContributor") final Chatbot bot, final ContextEnvironment contextEnvironment, boolean creation) {
        ContextEnvironment savedContextEnvironment = contextEnvironmentDAO.save(contextEnvironment);
        if (creation) {
            contextValueServices.getAllContextValueByBotId(bot.getBotId()).stream().map(contextValue -> {
                ContextEnvironmentValue contextEnvironmentValue = new ContextEnvironmentValue();
                contextEnvironmentValue.setCenvId(savedContextEnvironment.getCenvId());
                contextEnvironmentValue.setCvaId(contextValue.getCvaId());
                return contextEnvironmentValue;
            }).forEach(contextEnvironmentValue -> contextEnvironmentValueServices.save(contextEnvironmentValue));
        }
        return savedContextEnvironment;
    }

    public ContextEnvironment findById(Long cenvId) {
        return contextEnvironmentDAO.get(cenvId);
    }

    public void addContextToAllContextEnvironmentForBot(final Long botId, Long cvaId) {
        getAllContextEnvironmentsByBot(botId).forEach(contextEnvironment -> {
            ContextEnvironmentValue contextEnvironmentValue = new ContextEnvironmentValue();
            contextEnvironmentValue.setCenvId(contextEnvironment.getCenvId());
            contextEnvironmentValue.setCvaId(cvaId);
            contextEnvironmentValueServices.save(contextEnvironmentValue);
        });
    }

    @Secured("BotUser")
    public void deleteContextEnvironment(@SecuredOperation("botContributor") final Chatbot bot, final Long cenvId) {
        contextEnvironmentValueServices.deleteAllValueByContextEnvironment(cenvId);
        contextEnvironmentDAO.delete(cenvId);
    }

    public DtList<ContextEnvironmentIhm> getContextEnvironmentIhmByBot(final Long botId) {
        DtList<ContextEnvironmentIhm> contextEnvironments = new DtList<>(ContextEnvironmentIhm.class);
        getAllContextEnvironmentsByBot(botId)
                .forEach(contextEnvironment -> {
                    ContextEnvironmentIhm contextEnvironmentIhm = new ContextEnvironmentIhm();
                    contextEnvironmentIhm.setCenvId(contextEnvironment.getCenvId());
                    contextEnvironmentIhm.setLabel(contextEnvironment.getLabel());
                    contextEnvironmentIhm.setContextEnvironmentValues(contextEnvironmentValueServices.findAllContextEnvironmentValuesByEnv(contextEnvironment.getCenvId()).stream()
                            .map(contextEnvironmentValue -> {
                                contextEnvironmentValue.contextValue().load();
                                contextEnvironmentValue.contextPossibleValue().load();
                                ContextEnvironmentValueIhm contextEnvironmentValueIhm = new ContextEnvironmentValueIhm();
                                contextEnvironmentValueIhm.setCenvalId(contextEnvironmentValue.getCenvalId());
                                if (contextEnvironmentValue.contextPossibleValue().isLoaded()) {
                                    contextEnvironmentValue.contextPossibleValue().get().typeOperator().load();
                                    TypeOperator typeOperator = contextEnvironmentValue.contextPossibleValue().get().typeOperator().get();
                                    String value = contextEnvironmentValue.contextPossibleValue().get().getValue();
                                    contextEnvironmentValueIhm.setValue(String.format("%s : %s", localeManager.getCurrentLocale().equals(Locale.FRANCE)
                                            ? typeOperator.getLabelFr() : typeOperator.getLabel(), value));
                                }
                                contextEnvironmentValueIhm.setCvaId(contextEnvironmentValue.getCvaId());
                                contextEnvironmentValueIhm.setCpvId(contextEnvironmentValue.getCpvId());
                                contextEnvironmentValueIhm.setLabel(contextEnvironmentValue.contextValue().get().getLabel());
                                return contextEnvironmentValueIhm;
                            })
                            .sorted(Comparator.comparing(ContextEnvironmentValueIhm::getLabel))
                            .collect(VCollectors.toDtList(ContextEnvironmentValueIhm.class)));
                    contextEnvironments.add(contextEnvironmentIhm);
                });
        return contextEnvironments;
    }

    public DtList<ContextEnvironment> getAllContextEnvironmentsByBot(final Long botId) {
        return contextEnvironmentDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextEnvironmentFields.botId, botId), DtListState.of(null));
    }

    public ContextEnvironmentValue findContextEnvironmentValueById(final Long cenvalId) {
        return contextEnvironmentValueServices.findById(cenvalId);
    }

    public ContextEnvironmentValue saveContextEnvironmentValue(@SecuredOperation("botContributor") final Chatbot bot, final ContextEnvironmentValue contextEnvironmentValue) {
        return contextEnvironmentValueServices.save(contextEnvironmentValue);
    }

    public void deleteAllContextEnvironmentByBot(@SecuredOperation("botAdministrator") final Chatbot bot){
        getAllContextEnvironmentsByBot(bot.getBotId()).forEach(contextEnvironment -> deleteContextEnvironment(bot, contextEnvironment.getCenvId()));
    }
}
