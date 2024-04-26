package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.dao.ConfluenceSettingSpaceDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ConfluenceSetting;
import io.vertigo.chatbot.commons.domain.ConfluenceSettingSpace;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;


import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;



@Transactional
@Secured("BotUser")
public class ConfluenceSettingSpaceServices implements Component {

    @Inject
    private ConfluenceSettingSpaceDAO confluenceSettingSpaceDAO;

    @Secured("BotUser")
    public void saveAllFromConSetId(@SecuredOperation("botContributor") Chatbot bot, final DtList<ConfluenceSettingSpace> confluenceSettingSpaces, final long id) {
        deleteAllFromConSetId(bot, id);
        confluenceSettingSpaces.forEach(x -> {
            if (x.getConfluencesettingId() == null){
                x.setConfluencesettingId(id);
            }
            confluenceSettingSpaceDAO.save(x);
        });
    }

    @Secured("BotUser")
    public void deleteAllFromConSetId(@SecuredOperation("botContributor") Chatbot bot, final long id) {
        getConSetSpaceByConSetId(id).forEach(space -> {
            confluenceSettingSpaceDAO.delete(space.getConSetSpaceId());
        });
    }

    public DtList<ConfluenceSettingSpace> getConSetSpaceByConSetId(final long id) {
        return confluenceSettingSpaceDAO.findAll(getConfluenceSettingCriteria(id), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    private static Criteria<ConfluenceSettingSpace> getConfluenceSettingCriteria(final long id) {
        return Criterions.isEqualTo(DtDefinitions.ConfluenceSettingSpaceFields.confluencesettingId, id);
    }
}
