package io.vertigo.chatbot.designer.builder.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.ResponseButtonDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.designer.builder.responsesButton.ResponsesButtonPAO;
import io.vertigo.chatbot.domain.DtDefinitions.ResponseButtonFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
@Secured("BotUser")
public class ResponsesButtonServices implements Component {

	@Inject
	private ResponseButtonDAO responseButtonDAO;

	@Inject
	private ResponsesButtonPAO responsesButtonPAO;

	public DtList<ResponseButton> getResponsesButtonList(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smallTalk) {
		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(smallTalk.getSmtId());
		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, ResponseButtonFields.btnId.name(), false));
	}

	public DtList<ResponseButton> getWelcomeButtonsByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		Assertion.check()
				.isNotNull(bot)
				.isNotNull(bot.getBotId());
		// ---

		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.botIdWelcome, bot.getBotId()),
				DtListState.of(1000, 0, ResponseButtonFields.btnId.name(), false));
	}

	public DtList<ResponseButton> getDefaultButtonsByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		Assertion.check()
				.isNotNull(bot)
				.isNotNull(bot.getBotId());
		// ---

		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.botIdDefault, bot.getBotId()),
				DtListState.of(1000, 0, ResponseButtonFields.btnId.name(), false));
	}

	public DtList<ResponseButton> getButtonsBySmalltalk(@SecuredOperation("botVisitor") final Chatbot bot, final SmallTalk smallTalk) {
		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(smallTalk.getSmtId());
		// ---

		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, ResponseButtonFields.btnId.name(), false));
	}

	public void removeAllButtonsBySmtId(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smt) {
		// clear old buttons
		responsesButtonPAO.removeAllButtonsBySmtId(smt.getSmtId());

	}

	public void removeAllButtonsByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		// clear old buttons
		responsesButtonPAO.removeAllButtonsByBotId(bot.getBotId());
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

	public void saveAllDefaultButtonsByBot(@SecuredOperation("botAdm") final Chatbot bot, final DtList<ResponseButton> buttonList) {
		// save new buttons
		final Long botId = bot.getBotId();
		for (final ResponseButton btn : buttonList) {
			btn.setBtnId(null); // force creation
			btn.setBotIdDefault(botId);
			responseButtonDAO.save(btn);
		}

	}

	public void saveAllWelcomeButtonsByBot(@SecuredOperation("botAdm") final Chatbot bot, final DtList<ResponseButton> buttonList) {
		// save new buttons
		final Long botId = bot.getBotId();
		for (final ResponseButton btn : buttonList) {
			btn.setBtnId(null); // force creation
			btn.setBotIdWelcome(botId);
			responseButtonDAO.save(btn);
		}

	}

}
