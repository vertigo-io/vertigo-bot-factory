package io.vertigo.chatbot.commons.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.Entity;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class JiraSetting implements Entity {
	private static final long serialVersionUID = 1L;

	private Long jirSetId;
	private String url;
	private String login;
	private String password;
	private String project;
	private Long numberOfResults;

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AJiraSettingChatbot",
			fkFieldName = "botId",
			primaryDtDefinitionName = "DtChatbot",
			primaryIsNavigable = true,
			primaryRole = "Chatbot",
			primaryLabel = "Chatbot",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtJiraSetting",
			foreignIsNavigable = false,
			foreignRole = "JiraSetting",
			foreignLabel = "JiraSetting",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> botIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.Chatbot.class, "Chatbot");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AJiraSettingNode",
			fkFieldName = "nodId",
			primaryDtDefinitionName = "DtChatbotNode",
			primaryIsNavigable = true,
			primaryRole = "ChatbotNode",
			primaryLabel = "Node",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtJiraSetting",
			foreignIsNavigable = false,
			foreignRole = "JiraSetting",
			foreignLabel = "JiraSetting",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.ChatbotNode> nodIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.ChatbotNode.class, "ChatbotNode");

	/** {@inheritDoc} */
	@Override
	public UID<JiraSetting> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Jira setting id'.
	 * @return Long jirSetId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Jira setting id")
	public Long getJirSetId() {
		return jirSetId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Jira setting id'.
	 * @param jirSetId Long <b>Obligatoire</b>
	 */
	public void setJirSetId(final Long jirSetId) {
		this.jirSetId = jirSetId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Jira URL'.
	 * @return String url <b>Obligatoire</b>
	 */
	@Field(smartType = "STyUrl", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Jira URL")
	public String getUrl() {
		return url;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Jira URL'.
	 * @param url String <b>Obligatoire</b>
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Login'.
	 * @return String login <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Login")
	public String getLogin() {
		return login;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Login'.
	 * @param login String <b>Obligatoire</b>
	 */
	public void setLogin(final String login) {
		this.login = login;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Password'.
	 * @return String password <b>Obligatoire</b>
	 */
	@Field(smartType = "STyText", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Password")
	public String getPassword() {
		return password;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Password'.
	 * @param password String <b>Obligatoire</b>
	 */
	public void setPassword(final String password) {
		this.password = password;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Jira project'.
	 * @return String project <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Jira project")
	public String getProject() {
		return project;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Jira project'.
	 * @param project String <b>Obligatoire</b>
	 */
	public void setProject(final String project) {
		this.project = project;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Number max of results'.
	 * @return Long numberOfResults <b>Obligatoire</b>
	 */
	@Field(smartType = "STyNumber", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Number max of results")
	public Long getNumberOfResults() {
		return numberOfResults;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Number max of results'.
	 * @param numberOfResults Long <b>Obligatoire</b>
	 */
	public void setNumberOfResults(final Long numberOfResults) {
		this.numberOfResults = numberOfResults;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Chatbot'.
	 * @return Long botId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Chatbot", fkDefinition = "DtChatbot", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getBotId() {
		return (Long) botIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Chatbot'.
	 * @param botId Long <b>Obligatoire</b>
	 */
	public void setBotId(final Long botId) {
		botIdAccessor.setId(botId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Node'.
	 * @return Long nodId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Node", fkDefinition = "DtChatbotNode", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getNodId() {
		return (Long) nodIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Node'.
	 * @param nodId Long <b>Obligatoire</b>
	 */
	public void setNodId(final Long nodId) {
		nodIdAccessor.setId(nodId);
	}

 	/**
	 * Association : Chatbot.
	 * @return l'accesseur vers la propriété 'Chatbot'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbot() {
		return botIdAccessor;
	}

 	/**
	 * Association : Node.
	 * @return l'accesseur vers la propriété 'Node'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.ChatbotNode> chatbotNode() {
		return nodIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
