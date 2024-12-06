package io.vertigo.chatbot.commons.domain.topic;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class TopicFileExport implements DataObject {
	private static final long serialVersionUID = 1L;

	private String code;
	private String title;
	private String category;
	private String description;
	private String tag;
	private String dateStart;
	private String dateEnd;
	private String active;
	private String trainingPhrases;
	private String typeTopic;
	private String kindTopic;
	private String script;
	private String response;
	private String buttons;
	private String buttonsUrl;
	private String isEnd;
	private String labels;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Code'.
	 * @return String code <b>Obligatoire</b>
	 */
	@Field(smartType = "STyCode", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Code")
	public String getCode() {
		return code;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Code'.
	 * @param code String <b>Obligatoire</b>
	 */
	public void setCode(final String code) {
		this.code = code;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Title'.
	 * @return String title <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Title")
	public String getTitle() {
		return title;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Title'.
	 * @param title String <b>Obligatoire</b>
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Category'.
	 * @return String category <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Category")
	public String getCategory() {
		return category;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Category'.
	 * @param category String <b>Obligatoire</b>
	 */
	public void setCategory(final String category) {
		this.category = category;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Description'.
	 * @return String description
	 */
	@Field(smartType = "STyLabel", label = "Description")
	public String getDescription() {
		return description;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Description'.
	 * @param description String
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Tag'.
	 * @return String tag
	 */
	@Field(smartType = "STyLabel", label = "Tag")
	public String getTag() {
		return tag;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Tag'.
	 * @param tag String
	 */
	public void setTag(final String tag) {
		this.tag = tag;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Start date'.
	 * @return String dateStart
	 */
	@Field(smartType = "STyLabel", label = "Start date")
	public String getDateStart() {
		return dateStart;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Start date'.
	 * @param dateStart String
	 */
	public void setDateStart(final String dateStart) {
		this.dateStart = dateStart;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'End date'.
	 * @return String dateEnd
	 */
	@Field(smartType = "STyLabel", label = "End date")
	public String getDateEnd() {
		return dateEnd;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'End date'.
	 * @param dateEnd String
	 */
	public void setDateEnd(final String dateEnd) {
		this.dateEnd = dateEnd;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Active'.
	 * @return String active <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Active")
	public String getActive() {
		return active;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Active'.
	 * @param active String <b>Obligatoire</b>
	 */
	public void setActive(final String active) {
		this.active = active;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Training phrases'.
	 * @return String trainingPhrases
	 */
	@Field(smartType = "STyText", label = "Training phrases")
	public String getTrainingPhrases() {
		return trainingPhrases;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Training phrases'.
	 * @param trainingPhrases String
	 */
	public void setTrainingPhrases(final String trainingPhrases) {
		this.trainingPhrases = trainingPhrases;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Topic type'.
	 * @return String typeTopic <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Topic type")
	public String getTypeTopic() {
		return typeTopic;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Topic type'.
	 * @param typeTopic String <b>Obligatoire</b>
	 */
	public void setTypeTopic(final String typeTopic) {
		this.typeTopic = typeTopic;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Topic kind'.
	 * @return String kindTopic <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Topic kind")
	public String getKindTopic() {
		return kindTopic;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Topic kind'.
	 * @param kindTopic String <b>Obligatoire</b>
	 */
	public void setKindTopic(final String kindTopic) {
		this.kindTopic = kindTopic;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Script'.
	 * @return String script
	 */
	@Field(smartType = "STyText", label = "Script")
	public String getScript() {
		return script;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Script'.
	 * @param script String
	 */
	public void setScript(final String script) {
		this.script = script;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Response'.
	 * @return String response
	 */
	@Field(smartType = "STyText", label = "Response")
	public String getResponse() {
		return response;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Response'.
	 * @param response String
	 */
	public void setResponse(final String response) {
		this.response = response;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Buttons'.
	 * @return String buttons
	 */
	@Field(smartType = "STyText", label = "Buttons")
	public String getButtons() {
		return buttons;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Buttons'.
	 * @param buttons String
	 */
	public void setButtons(final String buttons) {
		this.buttons = buttons;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Buttons URL'.
	 * @return String buttonsUrl
	 */
	@Field(smartType = "STyText", label = "Buttons URL")
	public String getButtonsUrl() {
		return buttonsUrl;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Buttons URL'.
	 * @param buttonsUrl String
	 */
	public void setButtonsUrl(final String buttonsUrl) {
		this.buttonsUrl = buttonsUrl;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'End of conversation'.
	 * @return String isEnd <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "End of conversation")
	public String getIsEnd() {
		return isEnd;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'End of conversation'.
	 * @param isEnd String <b>Obligatoire</b>
	 */
	public void setIsEnd(final String isEnd) {
		this.isEnd = isEnd;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'labels'.
	 * @return String labels <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "labels")
	public String getLabels() {
		return labels;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'labels'.
	 * @param labels String <b>Obligatoire</b>
	 */
	public void setLabels(final String labels) {
		this.labels = labels;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
