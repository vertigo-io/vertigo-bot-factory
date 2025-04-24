package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.FontFamilyDAO;
import io.vertigo.chatbot.commons.domain.FontFamily;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

/**
 * @author cmarechal
 * @created 23/09/2022 - 16:58
 * @project vertigo-bot-factory
 */
@Transactional
public class FontFamilyServices implements Component {

    @Inject
    private FontFamilyDAO fontFamilyDAO;

    public DtList<FontFamily> findAll() {
        return fontFamilyDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public FontFamily findByFofCd(final String fofCd) {
        return fontFamilyDAO.find(Criterions.isEqualTo(DtDefinitions.FontFamilyFields.fofCd, fofCd));
    }
}
