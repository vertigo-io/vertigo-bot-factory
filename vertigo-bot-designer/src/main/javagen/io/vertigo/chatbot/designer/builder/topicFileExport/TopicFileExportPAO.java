package io.vertigo.chatbot.designer.builder.topicFileExport;

import javax.inject.Inject;

import java.util.Optional;
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
public final class TopicFileExportPAO implements StoreServices {
	private final TaskManager taskManager;

	/**
	 * Constructeur.
	 * @param taskManager Manager des Task
	 */
	@Inject
	public TopicFileExportPAO(final TaskManager taskManager) {
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
	 * Execute la tache TkGetTopicFileExport.
	 * @param botId Long
	 * @param tcaId Long
	 * @return DtList de TopicFileExport topic
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetTopicFileExport",
			request = "select top.code," + 
 "			top.tto_cd as type_topic," + 
 "			top.title," + 
 "			tca.code as category," + 
 "			top.description," + 
 "			null as tag," + 
 "			null as date_start," + 
 "			null as date_end," + 
 "			CASE " + 
 "				WHEN top.is_enabled THEN 'ACTIVE'" + 
 "				ELSE 'INACTIVE'" + 
 "			END	as active," + 
 "			sin.script," + 
 "			tph.agg as training_phrases," + 
 "			utt.text as response," + 
 "			buttons.doublons as buttons," + 
 "			CASE " + 
 "				WHEN smt.is_end THEN 'TRUE'" + 
 "				ELSE 'FALSE'" + 
 "			END	as is_end" + 
 "			from topic top" + 
 "			left join (" + 
 "					select nts.top_id," + 
 "					string_agg(nts.text,'|') agg	" + 
 "					from nlu_training_sentence nts" + 
 "					join topic top on top.top_id = nts.top_id					" + 
 "					group by (nts.top_id)" + 
 "				) as tph  on top.top_id = tph.top_id" + 
 "			left join topic_category tca on tca.top_cat_id = top.top_cat_id	" + 
 "			left join script_intention sin on sin.top_id = top.top_id" + 
 "			left join small_talk smt on smt.top_id = top.top_id" + 
 "			left join utter_text utt on utt.smt_id = smt.smt_id" + 
 "			left join (" + 
 "					select t.top_id, string_agg(concat('[',rbu.text,'¤',tre.code,']'),'|') as doublons" + 
 "					from response_button rbu" + 
 "					join topic tre on tre.top_id = rbu.top_id_response" + 
 "					join small_talk st on st.smt_id = rbu.smt_id " + 
 "					join topic t on t.top_id = st.top_id " + 
 "					group by (t.top_id)" + 
 "				) buttons on buttons.top_id = top.top_id" + 
 "			where top.bot_id = #botId#" + 
 "			and tca.is_technical = false" + 
 "			<%if (tcaId != null) { %>" + 
 "				and tca.top_cat_id = #tcaId#" + 
 "			<% } %>" + 
 "			order by top.code",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtTopicFileExport")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.chatbot.commons.domain.topic.TopicFileExport> getTopicFileExport(@io.vertigo.datamodel.task.proxy.TaskInput(name = "botId", smartType = "STyId") final Long botId, @io.vertigo.datamodel.task.proxy.TaskInput(name = "tcaId", smartType = "STyId") final Optional<Long> tcaId) {
		final Task task = createTaskBuilder("TkGetTopicFileExport")
				.addValue("botId", botId)
				.addValue("tcaId", tcaId.orElse(null))
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}
}
