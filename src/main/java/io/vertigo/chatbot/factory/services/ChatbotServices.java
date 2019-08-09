package io.vertigo.chatbot.factory.services;

import javax.inject.Inject;

import io.vertigo.chatbot.factory.dao.IntentDAO;
import io.vertigo.chatbot.factory.domain.Intent;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.lang.Assertion;

@Transactional
public class ChatbotServices implements Component {

    @Inject
    private IntentDAO intentDAO;

    public Intent getIntentById(final Long movId) {
        Assertion.checkNotNull(movId);
        //--- 
        return intentDAO.get(movId);
    }

    public DtList<Intent> getAllIntents() {
        return intentDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
    }

    public Intent save(final Intent intent) {
        Assertion.checkNotNull(intent);
        //---
        return intentDAO.save(intent);
    }
}