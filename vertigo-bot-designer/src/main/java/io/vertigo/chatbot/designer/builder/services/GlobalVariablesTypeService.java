package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.GlobalVariableTypeDAO;
import io.vertigo.chatbot.designer.domain.GlobalVariableType;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;

import javax.inject.Inject;

import java.util.Comparator;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class GlobalVariablesTypeService implements Component {

    @Inject
    private GlobalVariableTypeDAO globalVariableTypeDAO;

    @Inject
    private GlobalVariableService globalVariableService;

    public DtList<GlobalVariableType> finAllGlobalVariableTypesByBot(Long botId) {
        return globalVariableTypeDAO.findAll(Criterions.isEqualTo(DtDefinitions.GlobalVariableTypeFields.botId, botId),
                DtListState.of(MAX_ELEMENTS_PLUS_ONE)).stream()
                .sorted(Comparator.comparing(GlobalVariableType::getLabel))
                .collect(VCollectors.toDtList(GlobalVariableType.class));
    }

    public GlobalVariableType saveGlobalVariableType(@SecuredOperation("botContributor") final Chatbot bot, GlobalVariableType globalVariableType) {
        return globalVariableTypeDAO.save(globalVariableType);
    }

    public void deleteGlobalVariableTypeById(@SecuredOperation("botContributor") final Chatbot bot, Long globalVariableTypeId) {
        globalVariableService.deleteAllByTypeId(bot, globalVariableTypeId);
        globalVariableTypeDAO.delete(globalVariableTypeId);
    }

    public void deleteGlobalVariableTypeByBot(@SecuredOperation("botContributor") final Chatbot bot) {
        globalVariableService.deleteAllByBotId(bot);
        globalVariableTypeDAO.deleteList(globalVariableTypeDAO.findAll(Criterions.isEqualTo(DtDefinitions.GlobalVariableTypeFields.botId, bot.getBotId()),
                DtListState.of(null)).stream().map(GlobalVariableType::getUID).toList());
    }
}
