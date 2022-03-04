package io.vertigo.chatbot.commons.dao;

import javax.inject.Inject;

import java.util.Optional;
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
import io.vertigo.chatbot.commons.domain.UnknownSentenceDetail;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class UnknownSentenceDetailDAO extends DAO<UnknownSentenceDetail, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public UnknownSentenceDetailDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(UnknownSentenceDetail.class, entityStoreManager, taskManager, smartTypeManager);
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
	 * Execute la tache TkFindLatestUnknownSentence.
	 * @param botId Long
	 * @return Option de UnknownSentenceDetail unknownSentenceDetail
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkFindLatestUnknownSentence",
			request = "select" + 
 "                *" + 
 "            from unknown_sentence_detail usd" + 
 "            where usd.bot_id = #botId#" + 
 "            order by usd.unk_se_id desc" + 
 "            limit 1",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtUnknownSentenceDetail")
	public Optional<io.vertigo.chatbot.commons.domain.UnknownSentenceDetail> findLatestUnknownSentence(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkFindLatestUnknownSentence")
				.addValue("botId", botId)
				.build();
		return Optional.ofNullable((io.vertigo.chatbot.commons.domain.UnknownSentenceDetail) getTaskManager()
				.execute(task)
				.getResult());
	}

}
