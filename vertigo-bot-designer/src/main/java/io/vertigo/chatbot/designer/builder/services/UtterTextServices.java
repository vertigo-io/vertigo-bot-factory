package io.vertigo.chatbot.designer.builder.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.utterText.UtterTextPAO;
import io.vertigo.chatbot.designer.utils.HtmlInputUtils;
import io.vertigo.chatbot.domain.DtDefinitions.UtterTextFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
@Secured("BotUser")
public class UtterTextServices implements Component {

	@Inject
	private UtterTextDAO utterTextDAO;

	@Inject
	private UtterTextPAO utterTextPAO;

	public UtterText save(@SecuredOperation("botAdm") final Chatbot bot, final UtterText ut) {
		return utterTextDAO.save(ut);
	}

	public void delete(@SecuredOperation("botAdm") final Chatbot bot, final UID<UtterText> uid) {
		utterTextDAO.delete(uid);
	}

	public UtterText getDefaultTextByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		Assertion.check().isNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdDefault());
	}

	public UtterText getWelcomeTextByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		Assertion.check().isNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdWelcome());
	}

	public DtList<UtterText> getUtterTextList(@SecuredOperation("botVisitor") final Chatbot bot, final SmallTalk smallTalk) {
		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(smallTalk.getSmtId());
		// ---
		return utterTextDAO.findAll(
				Criterions.isEqualTo(UtterTextFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, UtterTextFields.uttId.name(), false));
	}

	public void removeAllUtterTextBySmtId(@SecuredOperation("botContributor") final Chatbot bot, final Long smtId) {
		// save utter textes, remove all + create all
		utterTextPAO.removeAllUtterTextBySmtId(smtId);
	}

	/**
	 * Get the uttertext for Small Talk and create them
	 *
	 * @param smt the smallTalk
	 * @param utterTexts the utterText to save
	 * @return the utterTexts saved
	 */
	public DtList<UtterText> createNoBlankUtterTextBySmallTalk(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smt, final DtList<UtterText> utterTexts) {

		Stream<UtterText> utterStream = utterTexts.stream();
		if (ResponseTypeEnum.RICH_TEXT.equals(smt.responseType().getEnumValue())) {
			utterStream = utterStream.limit(1);
		}

		final DtList<UtterText> uttToSave = utterStream
				.filter(utt -> !StringUtil.isBlank(utt.getText()))
				.collect(VCollectors.toDtList(UtterText.class));

		for (final UtterText utt : uttToSave) {
			utt.setUttId(null); // force creation
			utt.setSmtId(smt.getSmtId());
			utt.setText(HtmlInputUtils.sanatizeHtml(utt.getText()));
			utterTextDAO.save(utt);
		}

		return uttToSave;
	}

	public void deleteUtterTextsBySmallTalk(@SecuredOperation("botContributor") final Chatbot bot, final SmallTalk smallTalk) {
		for (final UtterText ut : getUtterTextList(bot, smallTalk)) {
			delete(bot, ut.getUID());
		}
	}

	public Map<Long, DtList<UtterText>> exportSmallTalkRelativeUtter(@SecuredOperation("botContributor") final Chatbot bot, final List<Long> smallTalkIds) {
		return utterTextDAO.exportSmallTalkRelativeUtter(smallTalkIds)
				.stream()
				.collect(Collectors.groupingBy(UtterText::getSmtId,
						VCollectors.toDtList(UtterText.class)));
	}

	public void removeAllUtterTextByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
		utterTextPAO.removeAllUtterTextByBotId(bot.getBotId());
	}
}
