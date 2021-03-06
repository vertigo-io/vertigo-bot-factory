package io.vertigo.chatbot.commons.dao;

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
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class NluTrainingSentenceDAO extends DAO<NluTrainingSentence, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public NluTrainingSentenceDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(NluTrainingSentence.class, entityStoreManager, taskManager, smartTypeManager);
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
	 * Execute la tache TkExportSmallTalkRelativeTrainingSentence.
	 * @param stIds List de Long
	 * @return DtList de NluTrainingSentence trainingSentences
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkExportSmallTalkRelativeTrainingSentence",
			request = "select " + 
 "            	*" + 
 "			from nlu_training_sentence nts" + 
 "			where nts.smt_id in (#stIds.rownum#)",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtNluTrainingSentence")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.NluTrainingSentence> exportSmallTalkRelativeTrainingSentence(@io.vertigo.datamodel.task.proxy.TaskInput(name = "stIds", smartType = "STyNumber") final java.util.List<Long> stIds) {
		final Task task = createTaskBuilder("TkExportSmallTalkRelativeTrainingSentence")
				.addValue("stIds", stIds)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

}
