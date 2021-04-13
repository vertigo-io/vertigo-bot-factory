package io.vertigo.chatbot.designer.builder.topic;

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
public final class TopicPAO implements StoreServices {
	private final TaskManager taskManager;

	/**
	 * Constructeur.
	 * @param taskManager Manager des Task
	 */
	@Inject
	public TopicPAO(final TaskManager taskManager) {
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
	 * Execute la tache TkGetAllTopicsIhmFromBot.
	 * @param botId Long
	 * @return DtList de TopicIhm topicIHM
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetAllTopicsIhmFromBot",
			request = "SELECT 	top.top_id," + 
 "					top.title," + 
 "					smt.smt_id," + 
 "					sin.sin_id," + 
 "					top.is_enabled," + 
 "					tto.label as type" + 
 "			from topic top " + 
 "			left join small_talk smt on smt.top_id = top.top_id" + 
 "			left join script_intention sin on sin.top_id = top.top_id" + 
 "			join type_topic tto on top.tto_cd = tto.tto_cd" + 
 "			where top.bot_id = #botId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopicIhm")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.TopicIhm> getAllTopicsIhmFromBot(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetAllTopicsIhmFromBot")
				.addValue("botId", botId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkRemoveAllNluTrainingSentenceByBotId.
	 * @param botId Long
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkRemoveAllNluTrainingSentenceByBotId",
			request = "delete from nlu_training_sentence nts" + 
 "			using topic top" + 
 "			where nts.top_id = top.top_id and top.bot_id = #botId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeAllNluTrainingSentenceByBotId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkRemoveAllNluTrainingSentenceByBotId")
				.addValue("botId", botId)
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkRemoveAllTopicsFromBot.
	 * @param botId Long
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkRemoveAllTopicsFromBot",
			request = "delete from topic top " + 
 "			where top.bot_id = #botId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeAllTopicsFromBot(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkRemoveAllTopicsFromBot")
				.addValue("botId", botId)
				.build();
		getTaskManager().execute(task);
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}
}
