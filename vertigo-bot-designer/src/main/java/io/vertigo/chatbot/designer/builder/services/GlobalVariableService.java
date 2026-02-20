package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.GlobalVariableDAO;
import io.vertigo.chatbot.designer.domain.GlobalVariable;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;

import javax.inject.Inject;

import java.util.Comparator;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class GlobalVariableService implements Component {

    @Inject
    private GlobalVariableDAO globalVariableDAO;

    public DtList<GlobalVariable> findAllGlobalVariablesByBotId(final Long botId) {
        return globalVariableDAO.findAll(Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.botId, botId),
                        DtListState.of(null)).stream()
                .sorted(Comparator.comparing(globalVariable -> {
                    globalVariable.globalVariableType().load();
                    return globalVariable.globalVariableType().get().getLabel();
                })).collect(VCollectors.toDtList(GlobalVariable.class));
    }

    public void saveGlobalVariable(@SecuredOperation("botContributor") final Chatbot bot, final GlobalVariable globalVariable) {
        globalVariableDAO.save(globalVariable);
    }

    public void deleteGlobalVariableById(@SecuredOperation("botContributor") final Chatbot bot, final long globalVariableId) {
        globalVariableDAO.delete(globalVariableId);
    }

    public DtList<GlobalVariable> findAllByTypeId(@SecuredOperation("botContributor") final Chatbot bot, final long typeId) {
        return globalVariableDAO.findAll(Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.gvtId, typeId),
                DtListState.of(null));
    }

    public DtList<GlobalVariable> findAllByTypeBotId(@SecuredOperation("botContributor") final Chatbot bot) {
        return globalVariableDAO.findAll(Criterions.isEqualTo(DtDefinitions.GlobalVariableFields.botId, bot.getBotId()),
                DtListState.of(null));
    }

    public void deleteAllByTypeId(@SecuredOperation("botContributor") final Chatbot bot, final long typeId) {
        globalVariableDAO.deleteList(findAllByTypeId(bot, typeId).stream().map(GlobalVariable::getUID).collect(Collectors.toList()));
    }

    public void deleteAllByBotId(@SecuredOperation("botContributor") final Chatbot bot) {
        globalVariableDAO.deleteList(findAllByTypeBotId(bot).stream().map(GlobalVariable::getUID).collect(Collectors.toList()));
    }

}
