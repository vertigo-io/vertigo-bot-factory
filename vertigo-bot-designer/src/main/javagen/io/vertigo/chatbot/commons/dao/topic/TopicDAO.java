package io.vertigo.chatbot.commons.dao.topic;

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
import io.vertigo.chatbot.commons.domain.topic.Topic;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class TopicDAO extends DAO<Topic, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public TopicDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(Topic.class, entityStoreManager, taskManager, smartTypeManager);
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
	 * Execute la tache TkGetAllTopicFromCategory.
	 * @param topCatId Long
	 * @return DtList de Topic tpcs
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetAllTopicFromCategory",
			request = "select top.*\n" + 
 " 			from topic top\n" + 
 " 			join topic_category tpc on (tpc.top_cat_id = top.top_cat_id)\n" + 
 " 			where tpc.top_cat_id = #topCatId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopic", name = "tpcs")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.Topic> getAllTopicFromCategory(@io.vertigo.datamodel.task.proxy.TaskInput(name = "topCatId", smartType = "STyId") final Long topCatId) {
		final Task task = createTaskBuilder("TkGetAllTopicFromCategory")
				.addValue("topCatId", topCatId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetAllTopicRelativeScriptIntentByBotId.
	 * @param botId Long
	 * @return DtList de Topic tpcs
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetAllTopicRelativeScriptIntentByBotId",
			request = "select top.*\n" + 
 " 			from topic top\n" + 
 " 			join script_intention sin on (sin.top_id = top.top_id)\n" + 
 " 			join topic_category tca on tca.top_cat_id = top.top_cat_id\n" + 
 " 			where top.bot_id = #botId# and top.is_enabled = true and tca.is_technical = false",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopic", name = "tpcs")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.Topic> getAllTopicRelativeScriptIntentByBotId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetAllTopicRelativeScriptIntentByBotId")
				.addValue("botId", botId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetAllTopicRelativeSmallTalkByBotId.
	 * @param botId Long
	 * @return DtList de Topic tpcs
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetAllTopicRelativeSmallTalkByBotId",
			request = "select top.*\n" + 
 " 			from topic top\n" + 
 " 			join small_talk smt on (smt.top_id = top.top_id)\n" + 
 " 			join topic_category tca on tca.top_cat_id = top.top_cat_id\n" + 
 " 			where top.bot_id = #botId# and top.is_enabled = true and tca.is_technical = false",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopic", name = "tpcs")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.Topic> getAllTopicRelativeSmallTalkByBotId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetAllTopicRelativeSmallTalkByBotId")
				.addValue("botId", botId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetBasicTopicByBotIdKtoCd.
	 * @param botId Long
	 * @param ktoCd String
	 * @return Option de Topic topic
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetBasicTopicByBotIdKtoCd",
			request = "SELECT 	top.*\n" + 
 " 			from topic top \n" + 
 " 			where top.bot_id = #botId#\n" + 
 " 			and top.kto_cd = #ktoCd#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopic", name = "topic")
	public Optional<io.vertigo.chatbot.commons.domain.topic.Topic> getBasicTopicByBotIdKtoCd(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId, @io.vertigo.datamodel.task.proxy.TaskInput(name = "ktoCd", smartType = "STyCode") final String ktoCd) {
		final Task task = createTaskBuilder("TkGetBasicTopicByBotIdKtoCd")
				.addValue("botId", botId)
				.addValue("ktoCd", ktoCd)
				.build();
		return Optional.ofNullable((io.vertigo.chatbot.commons.domain.topic.Topic) getTaskManager()
				.execute(task)
				.getResult());
	}

	/**
	 * Execute la tache TkGetTopicByCodeBotId.
	 * @param botId Long
	 * @param code String
	 * @return DtList de Topic topic
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetTopicByCodeBotId",
			request = "select top.*\n" + 
 " 			from topic top\n" + 
 " 			where top.bot_id = #botId#\n" + 
 " 			and top.code = #code#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopic", name = "topic")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.Topic> getTopicByCodeBotId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId, @io.vertigo.datamodel.task.proxy.TaskInput(name = "code", smartType = "STyCode") final String code) {
		final Task task = createTaskBuilder("TkGetTopicByCodeBotId")
				.addValue("botId", botId)
				.addValue("code", code)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetTopicFromCode.
	 * @param code String
	 * @param botId Long
	 * @return Option de Topic topic
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetTopicFromCode",
			request = "select top.*\n" + 
 " 			from topic top\n" + 
 " 			where top.code = #code# and top.bot_id = #botId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopic", name = "topic")
	public Optional<io.vertigo.chatbot.commons.domain.topic.Topic> getTopicFromCode(@io.vertigo.datamodel.task.proxy.TaskInput(name = "code", smartType = "STyLabel") final String code, @io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId) {
		final Task task = createTaskBuilder("TkGetTopicFromCode")
				.addValue("code", code)
				.addValue("botId", botId)
				.build();
		return Optional.ofNullable((io.vertigo.chatbot.commons.domain.topic.Topic) getTaskManager()
				.execute(task)
				.getResult());
	}

	/**
	 * Execute la tache TkGetTopicReferencingTopId.
	 * @param topId Long
	 * @return DtList de Topic topic
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetTopicReferencingTopId",
			request = "select top.*\n" + 
 " 			from topic top\n" + 
 " 			join small_talk smt on smt.top_id = top.top_id\n" + 
 " 			inner join response_button rbu on smt.smt_id = rbu.smt_id\n" + 
 " 			where rbu.top_id_response = #topId#",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopic", name = "topic")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.Topic> getTopicReferencingTopId(@io.vertigo.datamodel.task.proxy.TaskInput(name = "topId", smartType = "STyId") final Long topId) {
		final Task task = createTaskBuilder("TkGetTopicReferencingTopId")
				.addValue("topId", topId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

}
