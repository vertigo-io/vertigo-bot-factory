package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.ResponseButtonDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.designer.builder.responsesButton.ResponsesButtonPAO;
import io.vertigo.chatbot.domain.DtDefinitions.ResponseButtonFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
@Secured("BotUser")
public class ResponsesButtonServices implements Component {

	@Inject
	private ResponseButtonDAO responseButtonDAO;

	@Inject
	private ResponsesButtonPAO responsesButtonPAO;

	public DtList<ResponseButton> getResponsesButtonList(@SecuredOperation("botVisitor") final Chatbot bot, final SmallTalk smallTalk) {
		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(smallTalk.getSmtId());
		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.smtId, smallTalk.getSmtId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, ResponseButtonFields.btnId.name(), false));
	}

	public DtList<ResponseButton> getButtonsBySmalltalk(@SecuredOperation("botVisitor") final Chatbot bot, final Long smtId) {
		Assertion.check()
				.isNotNull(smtId);
		// ---

		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.smtId, smtId),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, ResponseButtonFields.btnId.name(), false));
	}

	public void removeAllButtonsBySmtId(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smt) {
		// clear old buttons
		responsesButtonPAO.removeAllButtonsBySmtId(smt.getSmtId());

	}

	/**
	 * Remove all buttons on all smts of bot
	 *
	 * @param bot
	 */
	public void removeAllSMTButtonsByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		responsesButtonPAO.removeAllSMTButtonsByBotId(bot.getBotId());
	}

	public void saveAllButtonsBySmtId(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk savedST, final DtList<ResponseButton> buttonList) {
		// save new buttons
		for (final ResponseButton btn : buttonList) {
			btn.setBtnId(null); // force creation
			btn.setSmtId(savedST.getSmtId());
			responseButtonDAO.save(btn);
		}

	}

	public void deleteResponsesButtonsBySmallTalk(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smallTalk) {

		for (final ResponseButton button : getResponsesButtonList(bot, smallTalk)) {
			responseButtonDAO.delete(button.getUID());
		}
	}

	public Map<Long, DtList<ResponseButton>> exportSmallTalkRelativeButtons(@SecuredOperation("botContributor") final Chatbot bot, final List<Long> smallTalkIds) {
		return responseButtonDAO.exportSmallTalkRelativeButtons(smallTalkIds)
				.stream()
				.collect(Collectors.groupingBy(ResponseButton::getSmtId,
						VCollectors.toDtList(ResponseButton.class)));
	}

}
