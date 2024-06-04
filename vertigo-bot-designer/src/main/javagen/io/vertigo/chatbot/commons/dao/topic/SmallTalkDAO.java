package io.vertigo.chatbot.commons.dao.topic;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.impl.dao.DAO;
import io.vertigo.datastore.impl.dao.StoreServices;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class SmallTalkDAO extends DAO<SmallTalk, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public SmallTalkDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(SmallTalk.class, entityStoreManager, taskManager, smartTypeManager);
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
	 * Execute la tache TkGetAllActiveSmallTalkByBot.
	 * @param botId Long
	 * @return DtList de SmallTalk smts
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetAllActiveSmallTalkByBot",
			request = "select smt.*\n" + 
 " 			from topic top\n" + 
 " 			join small_talk smt on (smt.top_id = top.top_id)\n" + 
 " 			where top.bot_id = #botId#\n" + 
 " 			AND top.is_enabled = true",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtSmallTalk", name = "smts")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.SmallTalk> getAllActiveSmallTalkByBot(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetAllActiveSmallTalkByBot")
				.addValue("botId", botId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

}
