package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.dao.ConfluenceSettingDAO;
import io.vertigo.chatbot.commons.domain.*;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class ConfluenceSettingServices implements Component {

    @Inject
    private ConfluenceSettingDAO confluenceSettingDAO;

    @Inject
    private PasswordEncryptionServices passwordEncryptionServices;

    @Inject
    private ConfluenceSettingSpaceServices spaceServices;

    public ConfluenceSetting findById(final long id) {
        return confluenceSettingDAO.get(id);
    }

    @Secured("BotUser")
    public void save(@SecuredOperation("botAdm") final Chatbot bot, final ConfluenceSetting confluenceSetting, final DtList<ConfluenceSettingSpace> confluenceSettingSpaces) {
        if (confluenceSetting.getPassword() != null && !confluenceSetting.getPassword().isEmpty()) {
            confluenceSetting.setPassword(passwordEncryptionServices.encryptPassword(confluenceSetting.getPassword()));
        } else if (confluenceSetting.getConSetId() != null) {
            confluenceSetting.setPassword(confluenceSettingDAO.get(confluenceSetting.getConSetId()).getPassword());
        }
        ConfluenceSetting newConfluenceSetting = confluenceSettingDAO.save(confluenceSetting);
        spaceServices.saveAllFromConSetId(bot, confluenceSettingSpaces, newConfluenceSetting.getConSetId());
    }

    @Secured("BotUser")
    public void deleteWithSpaces(@SecuredOperation("botAdm") final Chatbot bot, final long id) {
        spaceServices.deleteAllFromConSetId(bot, id);
        confluenceSettingDAO.delete(id);
    }

    @Secured("BotUser")
    public DtList<ConfluenceSetting> findAllByBotId(@SecuredOperation("botContributor") final Chatbot bot) {
        return confluenceSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE))
                .stream().peek(confluenceSetting -> confluenceSetting.setPassword("")).collect(VCollectors.toDtList(ConfluenceSetting.class));
    }

    @Secured("BotUser")
    public DtList<ConfluenceSettingIhm> findAllWithSpaces(@SecuredOperation("botContributor") final Chatbot bot) {
        DtList<ConfluenceSetting> confluenceSettings = findAllByBotId(bot);

        DtList<ConfluenceSettingIhm> newConfluenceSettingIhms = new DtList<>(ConfluenceSettingIhm.class);

        confluenceSettings.forEach(confluenceSetting -> {
            ConfluenceSettingIhm confluenceSettingIhm = new ConfluenceSettingIhm();

            confluenceSettingIhm.setConSetId(confluenceSetting.getConSetId());
            confluenceSettingIhm.setUrl(confluenceSetting.getUrl());
            confluenceSettingIhm.setLogin(confluenceSetting.getLogin());
            confluenceSettingIhm.setPassword(confluenceSetting.getPassword());
            confluenceSettingIhm.setNumberOfResults(confluenceSetting.getNumberOfResults());
            confluenceSettingIhm.setBotId(confluenceSetting.getBotId());
            confluenceSettingIhm.setNodId(confluenceSetting.getNodId());

            StringBuilder spaces = new StringBuilder();

            DtList<ConfluenceSettingSpace> fetchedSpaces = spaceServices.getConSetSpaceByConSetId(confluenceSetting.getConSetId());
            if (fetchedSpaces != null && !fetchedSpaces.isEmpty()) {
                fetchedSpaces.forEach(conSetSpace -> spaces.append(conSetSpace.getSpace()).append(","));
                confluenceSettingIhm.setSpaces(spaces.substring(0, spaces.length() - 1));
            } else confluenceSettingIhm.setSpaces(null);

            newConfluenceSettingIhms.add(confluenceSettingIhm);
        });
        return newConfluenceSettingIhms;
    }

    @Secured("BotUser")
    public ConfluenceSetting findSetFromIhm(@SecuredOperation("botContributor") Chatbot bot, final ConfluenceSettingIhm confluenceSettingIhm) {
        ;

        ConfluenceSetting newConfluenceSetting = new ConfluenceSetting();

        newConfluenceSetting.setConSetId(confluenceSettingIhm.getConSetId());
        newConfluenceSetting.setUrl(confluenceSettingIhm.getUrl());
        newConfluenceSetting.setLogin(confluenceSettingIhm.getLogin());
        newConfluenceSetting.setPassword(confluenceSettingIhm.getPassword());
        newConfluenceSetting.setNumberOfResults(confluenceSettingIhm.getNumberOfResults());
        newConfluenceSetting.setBotId(confluenceSettingIhm.getBotId());
        newConfluenceSetting.setNodId(confluenceSettingIhm.getNodId());

        return newConfluenceSetting;
    }

    @Secured("BotUser")
    public DtList<ConfluenceSettingSpace> findSpacesFromIhm(@SecuredOperation("botContributor") Chatbot bot, final ConfluenceSettingIhm confluenceSettingIhm) {
        DtList<ConfluenceSettingSpace> newConfluenceSettingSpaces = new DtList<>(ConfluenceSettingSpace.class);
        if (confluenceSettingIhm.getSpaces() != null) {
            List<String> ihmSpaces = Arrays.stream(confluenceSettingIhm.getSpaces().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            ihmSpaces.forEach(space -> {
                ConfluenceSettingSpace confluenceSettingSpace = new ConfluenceSettingSpace();
                confluenceSettingSpace.setSpace(space);
                confluenceSettingSpace.setConfluencesettingId(confluenceSettingIhm.getConSetId());
                newConfluenceSettingSpaces.add(confluenceSettingSpace);
            });
        }
        return newConfluenceSettingSpaces;
    }


    @Secured("BotUser")
    public void deleteAllByNodeId(@SecuredOperation("botAdm") final Chatbot bot, final long nodeId) {
        confluenceSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.nodId, nodeId),
                DtListState.of(MAX_ELEMENTS_PLUS_ONE)).forEach(confluenceSetting -> this.deleteWithSpaces(bot, confluenceSetting.getConSetId()));
    }

    @Secured("BotUser")
    public void deleteAllByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
        this.findAllByBotId(bot).forEach(confluenceSetting -> this.deleteWithSpaces(bot, confluenceSetting.getConSetId()));
    }

    public Optional<ConfluenceSettingExport> exportConfluenceSetting(final long botId, final long nodId) {
        return confluenceSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.botId, botId)
                .and(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.nodId, nodId))).map(confluenceSetting -> {
            final ConfluenceSettingExport confluenceSettingExport = new ConfluenceSettingExport();
            confluenceSettingExport.setUrl(confluenceSetting.getUrl());
            confluenceSettingExport.setLogin(confluenceSetting.getLogin());
            confluenceSettingExport.setPassword(confluenceSetting.getPassword());
            confluenceSettingExport.setNumberOfResults(confluenceSetting.getNumberOfResults());

            List<String> spaces = new ArrayList<>();
            spaceServices.getConSetSpaceByConSetId(confluenceSetting.getConSetId()).forEach(space -> spaces.add(space.getSpace()));
            confluenceSettingExport.setSpaces(spaces);

            return Optional.of(confluenceSettingExport);
        }).orElseGet(Optional::empty);
    }
}
