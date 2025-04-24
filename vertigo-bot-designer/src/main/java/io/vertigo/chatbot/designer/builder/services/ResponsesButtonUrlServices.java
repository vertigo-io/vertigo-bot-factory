package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.ResponseButtonUrlDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.ResponseButtonUrl;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.designer.builder.responsesButtonUrl.ResponsesButtonUrlPAO;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
@Secured("BotUser")
public class ResponsesButtonUrlServices implements Component {

	@Inject
	private ResponseButtonUrlDAO responseButtonUrlDAO;

	@Inject
	private ResponsesButtonUrlPAO responsesButtonUrlPAO;

	public DtList<ResponseButtonUrl> getResponsesButtonUrlList(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smallTalk) {
		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(smallTalk.getSmtId());
		return responseButtonUrlDAO.findAll(
				Criterions.isEqualTo(DtDefinitions.ResponseButtonUrlFields.smtId, smallTalk.getSmtId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, DtDefinitions.ResponseButtonUrlFields.btnId.name(), false));
	}

	public DtList<ResponseButtonUrl> getButtonsUrlBySmalltalk(@SecuredOperation("botVisitor") final Chatbot bot, final Long smtId) {
		Assertion.check()
				.isNotNull(smtId);
		// ---

		return responseButtonUrlDAO.findAll(
				Criterions.isEqualTo(DtDefinitions.ResponseButtonUrlFields.smtId, smtId),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, DtDefinitions.ResponseButtonUrlFields.btnId.name(), false));
	}

	public void removeAllButtonsUrlBySmtId(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smt) {
		// clear old buttons
		responsesButtonUrlPAO.removeAllButtonsUrlBySmtId(smt.getSmtId());

	}

	/**
	 * Remove all buttons on all smts of bot
	 *
	 * @param bot
	 */
	public void removeAllSMTButtonsUrlByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		responsesButtonUrlPAO.removeAllSMTButtonsUrlByBotId(bot.getBotId());
	}

	public void saveAllButtonsUrlBySmtId(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk savedST, final DtList<ResponseButtonUrl> buttonUrlList) {
		// save new buttons
		for (final ResponseButtonUrl btn : buttonUrlList) {
			btn.setBtnId(null); // force creation
			btn.setSmtId(savedST.getSmtId());
			responseButtonUrlDAO.save(btn);
		}

	}

	public void deleteResponsesButtonsUrlBySmallTalk(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smallTalk) {

		for (final ResponseButtonUrl button : getResponsesButtonUrlList(bot, smallTalk)) {
			responseButtonUrlDAO.delete(button.getUID());
		}
	}

}
