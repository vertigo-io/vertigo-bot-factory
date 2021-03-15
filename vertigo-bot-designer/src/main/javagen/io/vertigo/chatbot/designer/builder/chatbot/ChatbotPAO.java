package io.vertigo.chatbot.designer.builder.chatbot;

import javax.inject.Inject;

import io.vertigo.core.node.Node;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datastore.impl.dao.StoreServices;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
 @Generated
public final class ChatbotPAO implements StoreServices {
	private final TaskManager taskManager;

	/**
	 * Constructeur.
	 * @param taskManager Manager des Task
	 */
	@Inject
	public ChatbotPAO(final TaskManager taskManager) {
		Assertion.check().isNotNull(taskManager);
		//-----
		this.taskManager = taskManager;
	}

	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Node.getNode().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return Task.builder(taskDefinition);
	}

	/**
	 * Execute la tache TkGetPersonProfilIHM.
	 * @param botId Long
	 * @return DtList de PersonChatbotProfil perIHM
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetPersonProfilIHM",
			request = "select ppc.bot_id," + 
 "					ppc.chp_id," + 
 "					per.name," + 
 "					cp.chp_cd as profil_Label" + 
 "			from profil_per_chatbot ppc " + 
 "			join person per on (per.per_id = ppc.per_id)" + 
 "			join chatbot_profiles cp on (cp.chp_cd = ppc.chp_cd)" + 
 "			where ppc.bot_id = #botId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtPersonChatbotProfil")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.designer.domain.admin.PersonChatbotProfil> getPersonProfilIHM(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetPersonProfilIHM")
				.addValue("botId", botId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkRemoveAllChaPerRightByBotId.
	 * @param botId Long
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkRemoveAllChaPerRightByBotId",
			request = "delete from cha_per_rights" + 
 "			where bot_id  = #botId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeAllChaPerRightByBotId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkRemoveAllChaPerRightByBotId")
				.addValue("botId", botId)
				.build();
		getTaskManager().execute(task);
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}
}
