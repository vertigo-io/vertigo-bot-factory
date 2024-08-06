package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.dao.DocumentaryResourceTypeDAO;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceType;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class DocumentaryResourceTypeServices implements Component {

    @Inject
    DocumentaryResourceTypeDAO documentaryResourceTypeDAO;

    public DtList<DocumentaryResourceType> getAllDocResTypes() {
        return documentaryResourceTypeDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }
}
