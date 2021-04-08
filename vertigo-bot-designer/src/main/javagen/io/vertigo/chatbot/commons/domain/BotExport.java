package io.vertigo.chatbot.commons.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class BotExport implements DtObject {
	private static final long serialVersionUID = 1L;

	private io.vertigo.chatbot.commons.domain.Chatbot bot;
	private io.vertigo.chatbot.commons.domain.topic.UtterText fallbackText;
	private io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.ResponseButton> fallbackButtons =  new io.vertigo.datamodel.structure.model.DtList<>(io.vertigo.chatbot.commons.domain.topic.ResponseButton.class);
	private io.vertigo.chatbot.commons.domain.topic.UtterText welcomeText;
	private io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.ResponseButton> welcomeButtons =  new io.vertigo.datamodel.structure.model.DtList<>(io.vertigo.chatbot.commons.domain.topic.ResponseButton.class);
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'chatbot'.
	 * @return Chatbot bot <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDtChatbot", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "chatbot")
	public io.vertigo.chatbot.commons.domain.Chatbot getBot() {
		return bot;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'chatbot'.
	 * @param bot Chatbot <b>Obligatoire</b>
	 */
	public void setBot(final io.vertigo.chatbot.commons.domain.Chatbot bot) {
		this.bot = bot;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'fallback'.
	 * @return UtterText fallbackText <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDtUtterText", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "fallback")
	public io.vertigo.chatbot.commons.domain.topic.UtterText getFallbackText() {
		return fallbackText;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'fallback'.
	 * @param fallbackText UtterText <b>Obligatoire</b>
	 */
	public void setFallbackText(final io.vertigo.chatbot.commons.domain.topic.UtterText fallbackText) {
		this.fallbackText = fallbackText;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'fallback buttons'.
	 * @return DtList de ResponseButton fallbackButtons
	 */
	@Field(smartType = "STyDtResponseButton", cardinality = io.vertigo.core.lang.Cardinality.MANY, label = "fallback buttons")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.ResponseButton> getFallbackButtons() {
		return fallbackButtons;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'fallback buttons'.
	 * @param fallbackButtons DtList de ResponseButton
	 */
	public void setFallbackButtons(final io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.ResponseButton> fallbackButtons) {
		io.vertigo.core.lang.Assertion.check().isNotNull(fallbackButtons);
		//---
		this.fallbackButtons = fallbackButtons;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'welcome'.
	 * @return UtterText welcomeText <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDtUtterText", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "welcome")
	public io.vertigo.chatbot.commons.domain.topic.UtterText getWelcomeText() {
		return welcomeText;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'welcome'.
	 * @param welcomeText UtterText <b>Obligatoire</b>
	 */
	public void setWelcomeText(final io.vertigo.chatbot.commons.domain.topic.UtterText welcomeText) {
		this.welcomeText = welcomeText;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'welcome buttons'.
	 * @return DtList de ResponseButton welcomeButtons
	 */
	@Field(smartType = "STyDtResponseButton", cardinality = io.vertigo.core.lang.Cardinality.MANY, label = "welcome buttons")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.ResponseButton> getWelcomeButtons() {
		return welcomeButtons;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'welcome buttons'.
	 * @param welcomeButtons DtList de ResponseButton
	 */
	public void setWelcomeButtons(final io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.ResponseButton> welcomeButtons) {
		io.vertigo.core.lang.Assertion.check().isNotNull(welcomeButtons);
		//---
		this.welcomeButtons = welcomeButtons;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
