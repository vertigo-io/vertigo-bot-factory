package io.vertigo.chatbot.commons.domain;

import io.vertigo.dynamo.domain.model.DtObject;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class SmallTalkExport implements DtObject {
	private static final long serialVersionUID = 1L;

	private io.vertigo.chatbot.commons.domain.SmallTalk smallTalk;
	private io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.NluTrainingSentence> nluTrainingSentences;
	private io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.UtterText> utterTexts;
	private io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.ResponseButton> buttons;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'SmallTalk'.
	 * @return SmallTalk smallTalk <b>Obligatoire</b>
	 */
	@Field(domain = "DoDtSmallTalkDto", required = true, label = "SmallTalk")
	public io.vertigo.chatbot.commons.domain.SmallTalk getSmallTalk() {
		return smallTalk;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'SmallTalk'.
	 * @param smallTalk SmallTalk <b>Obligatoire</b>
	 */
	public void setSmallTalk(final io.vertigo.chatbot.commons.domain.SmallTalk smallTalk) {
		this.smallTalk = smallTalk;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'nluTrainingSentences'.
	 * @return DtList de NluTrainingSentence nluTrainingSentences <b>Obligatoire</b>
	 */
	@Field(domain = "DoDtNluTrainingSentenceDtc", required = true, label = "nluTrainingSentences")
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.NluTrainingSentence> getNluTrainingSentences() {
		return nluTrainingSentences;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'nluTrainingSentences'.
	 * @param nluTrainingSentences DtList de NluTrainingSentence <b>Obligatoire</b>
	 */
	public void setNluTrainingSentences(final io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.NluTrainingSentence> nluTrainingSentences) {
		this.nluTrainingSentences = nluTrainingSentences;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'response texts'.
	 * @return DtList de UtterText utterTexts <b>Obligatoire</b>
	 */
	@Field(domain = "DoDtUtterTextDtc", required = true, label = "response texts")
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.UtterText> getUtterTexts() {
		return utterTexts;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'response texts'.
	 * @param utterTexts DtList de UtterText <b>Obligatoire</b>
	 */
	public void setUtterTexts(final io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.UtterText> utterTexts) {
		this.utterTexts = utterTexts;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'buttons'.
	 * @return DtList de ResponseButton buttons <b>Obligatoire</b>
	 */
	@Field(domain = "DoDtResponseButtonDtc", required = true, label = "buttons")
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.ResponseButton> getButtons() {
		return buttons;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'buttons'.
	 * @param buttons DtList de ResponseButton <b>Obligatoire</b>
	 */
	public void setButtons(final io.vertigo.dynamo.domain.model.DtList<io.vertigo.chatbot.commons.domain.ResponseButton> buttons) {
		this.buttons = buttons;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
