package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.GlobalVariableDAO;
import io.vertigo.chatbot.designer.domain.GlobalVariable;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import javax.inject.Inject;

import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

/**
 * Manages global variables and separates bounded UI queries from exhaustive
 * business operations.
 *
 * @author Chatbot Team
 */
@Transactional
public class GlobalVariableService implements Component {

    @Inject
    private GlobalVariableDAO globalVariableDAO;

    /**
     * Finds the global variables displayed by the UI, with an optional type
     * filter and one sentinel row used to detect truncation.
     *
     * @param botId bot identifier
     * @param globalVariableTypeId optional global variable type identifier
     * @return bounded global variable list
     */
    public DtList<GlobalVariable> findGlobalVariablesForUi(final Long botId, final Long globalVariableTypeId) {
        Criteria<GlobalVariable> criteria = Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.botId, botId);
        if (globalVariableTypeId != null) {
            criteria = criteria.and(Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.gvtId, globalVariableTypeId));
        }
        final String sortField = globalVariableTypeId == null
                ? DtDefinitions.GlobalVariableFields.gvtId.name()
                : DtDefinitions.GlobalVariableFields.param1.name();
        return globalVariableDAO.findAll(criteria, DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, sortField, false));
    }

    /**
     * Saves a global variable.
     *
     * @param bot secured bot
     * @param globalVariable variable to save
     */
    public void saveGlobalVariable(@SecuredOperation("botContributor") final Chatbot bot, final GlobalVariable globalVariable) {
        globalVariableDAO.save(globalVariable);
    }

    /**
     * Deletes a global variable.
     *
     * @param bot secured bot
     * @param globalVariableId variable identifier
     */
    public void deleteGlobalVariableById(@SecuredOperation("botContributor") final Chatbot bot, final long globalVariableId) {
        globalVariableDAO.delete(globalVariableId);
    }

    /**
     * Finds all variables of a type for exhaustive business operations.
     *
     * @param bot secured bot
     * @param typeId type identifier
     * @return unbounded variable list
     */
    public DtList<GlobalVariable> findAllByTypeId(@SecuredOperation("botContributor") final Chatbot bot, final long typeId) {
        return globalVariableDAO.findAll(
                Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.botId, bot.getBotId())
                        .and(Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.gvtId, typeId)),
                DtListState.of(null));
    }

    /**
     * Finds all variables of a bot for exhaustive business operations such as
     * exports.
     *
     * @param bot secured bot
     * @return unbounded variable list
     */
    public DtList<GlobalVariable> findAllByTypeBotId(@SecuredOperation("botContributor") final Chatbot bot) {
        return globalVariableDAO.findAll(Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.botId, bot.getBotId()),
                DtListState.of(null));
    }

    /**
     * Deletes every variable of a type.
     *
     * @param bot secured bot
     * @param typeId type identifier
     */
    public void deleteAllByTypeId(@SecuredOperation("botContributor") final Chatbot bot, final long typeId) {
        globalVariableDAO.deleteList(findAllByTypeId(bot, typeId).stream().map(GlobalVariable::getUID).collect(Collectors.toList()));
    }

    /**
     * Deletes every variable of a bot.
     *
     * @param bot secured bot
     */
    public void deleteAllByBotId(@SecuredOperation("botContributor") final Chatbot bot) {
        globalVariableDAO.deleteList(findAllByTypeBotId(bot).stream().map(GlobalVariable::getUID).collect(Collectors.toList()));
    }

}
