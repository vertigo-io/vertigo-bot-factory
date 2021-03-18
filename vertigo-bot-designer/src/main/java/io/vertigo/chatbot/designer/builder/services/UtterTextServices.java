package io.vertigo.chatbot.designer.builder.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
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
	private BuilderPAO builderPAO;

	public UtterText save(final UtterText ut) {
		return utterTextDAO.save(ut);
	}

	public void delete(final UID<UtterText> uid) {
		utterTextDAO.delete(uid);
	}

	public UtterText getDefaultTextByBot(final Chatbot bot) {
		Assertion.check().isNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdDefault());
	}

	public UtterText getWelcomeTextByBot(final Chatbot bot) {
		Assertion.check().isNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdWelcome());
	}

	public DtList<UtterText> getUtterTextList(final SmallTalk smallTalk) {
		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(smallTalk.getSmtId());
		// ---
		return utterTextDAO.findAll(
				Criterions.isEqualTo(UtterTextFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, UtterTextFields.uttId.name(), false));
	}

	public void removeAllUtterTextBySmtId(final Long smtId) {
		// save utter textes, remove all + create all
		builderPAO.removeAllUtterTextBySmtId(smtId);
	}

	/**
	 * Get the uttertext for Small Talk and create them
	 *
	 * @param smt the smallTalk
	 * @param utterTexts the utterText to save
	 * @return the utterTexts saved
	 */
	public DtList<UtterText> createNoBlankUtterTextBySmallTalk(final SmallTalk smt, final DtList<UtterText> utterTexts) {

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

	public void deleteUtterTextsBySmallTalk(final SmallTalk smallTalk) {
		for (final UtterText ut : getUtterTextList(smallTalk)) {
			delete(ut.getUID());
		}
	}

	public Map<Long, DtList<UtterText>> exportSmallTalkRelativeUtter(final List<Long> smallTalkIds) {
		return utterTextDAO.exportSmallTalkRelativeUtter(smallTalkIds)
				.stream()
				.collect(Collectors.groupingBy(UtterText::getSmtId,
						VCollectors.toDtList(UtterText.class)));
	}

	public void removeAllUtterTextByBotId(final Chatbot bot) {
		builderPAO.removeAllUtterTextByBotId(bot.getBotId());
	}
}
