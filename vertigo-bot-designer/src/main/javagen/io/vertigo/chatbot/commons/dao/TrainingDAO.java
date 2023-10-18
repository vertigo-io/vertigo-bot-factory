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
import io.vertigo.chatbot.commons.domain.Training;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class TrainingDAO extends DAO<Training, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public TrainingDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(Training.class, entityStoreManager, taskManager, smartTypeManager);
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
	 * Execute la tache TkGetCurrentTrainingByBotId.
	 * @param botId Long
	 * @return DtList de Training tra
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetCurrentTrainingByBotId",
			request = "select tra.*\n" + 
 "             from training tra\n" + 
 "             where tra.bot_id = #botId# and tra.str_cd = 'TRAINING' order by tra.start_time desc",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTraining", name = "tra")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.Training> getCurrentTrainingByBotId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetCurrentTrainingByBotId")
				.addValue("botId", botId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetDeployedTrainingByBotId.
	 * @param botId Long
	 * @return Option de Training tra
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetDeployedTrainingByBotId",
			request = "select tra.*\n" + 
 "             from training tra\n" + 
 "             join chatbot_node cn on cn.tra_id = tra.tra_id\n" + 
 "             where tra.bot_id = #botId# and cn.is_dev = true",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTraining", name = "tra")
	public Optional<io.vertigo.chatbot.commons.domain.Training> getDeployedTrainingByBotId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetDeployedTrainingByBotId")
				.addValue("botId", botId)
				.build();
		return Optional.ofNullable((io.vertigo.chatbot.commons.domain.Training) getTaskManager()
				.execute(task)
				.getResult());
	}

}
