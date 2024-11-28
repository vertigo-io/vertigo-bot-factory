package io.vertigo.chatbot.designer.builder.services.bot;

import java.util.Locale;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.ContextPossibleValueDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextPossibleValue;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceContextServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerContextServices;
import io.vertigo.chatbot.designer.domain.TypeOperator;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
@Secured("BotUser")
public class ContextPossibleValueServices implements Component {

    @Inject
    private ContextPossibleValueDAO contextPossibleValueDAO;

    @Inject
    private DocumentaryResourceContextServices documentaryResourceContextServices;

    @Inject
    private QuestionAnswerContextServices questionAnswerContextServices;

    @Inject
    private LocaleManager localeManager;

    public DtList<ContextPossibleValue> getAllContextPossibleValuesByCvaId(@SecuredOperation("botVisitor") final Chatbot bot,  final Long cvaId) {
        return contextPossibleValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextPossibleValueFields.cvaId, cvaId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<ContextPossibleValue> getAllContextPossibleValuesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        Locale currentLocale = localeManager.getCurrentLocale();
        DtList<ContextPossibleValue> contextPossibleValueList = contextPossibleValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextPossibleValueFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
        contextPossibleValueList.forEach(possibleValue -> setDisplayValueForIhm(possibleValue, currentLocale));
        return contextPossibleValueList;
    }

    private void setDisplayValueForIhm(final ContextPossibleValue possibleValue, final Locale locale) {
        possibleValue.typeOperator().load();
        final TypeOperator typeOperator = possibleValue.typeOperator().get();
        String operatorLabel = typeOperator.getLabel();
        if(Locale.FRANCE.equals(locale)) {
            operatorLabel = typeOperator.getLabelFr();
        }

        possibleValue.setValue(operatorLabel + " : " + possibleValue.getValue());
    }

    public void deleteContextPossibleValue(@SecuredOperation("botContributor") final Chatbot bot, final Long cpvId) {
        documentaryResourceContextServices.setAllDocumentaryResourceContextCpvIdToNullByCpvId(bot, cpvId);
        questionAnswerContextServices.setAllQuestionAnswerContextCpvIdToNullByCpvId(bot, cpvId);
        contextPossibleValueDAO.delete(cpvId);
    }

    public void deleteContextPossibleValuesByCvaId(@SecuredOperation("botContributor") final Chatbot bot, final Long cvaId) {
        contextPossibleValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextPossibleValueFields.cvaId, cvaId), DtListState.of(null))
                .forEach(contextPossibleValue -> deleteContextPossibleValue(bot, contextPossibleValue.getCpvId()));
    }

    public ContextPossibleValue save(@SecuredOperation("botContributor") final Chatbot bot, final ContextPossibleValue contextPossibleValue) {
        return contextPossibleValueDAO.save(contextPossibleValue);
    }
}
