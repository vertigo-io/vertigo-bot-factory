package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.datamodel.structure.model.DtObject;


@Secured("BotUser")
public interface IRecordable<D extends DtObject> {

	History record(final Chatbot bot, final D object, HistoryActionEnum action);
}
