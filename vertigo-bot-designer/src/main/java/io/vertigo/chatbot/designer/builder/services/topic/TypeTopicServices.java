package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.chatbot.commons.dao.topic.TypeTopicDAO;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
public class TypeTopicServices implements Component {

	@Inject
	private TypeTopicDAO typeTopicDAO;

	public DtList<TypeTopic> getAllTypeTopic() {
		return typeTopicDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public TypeTopic save(final TypeTopic typeTopic) {
		return typeTopicDAO.save(typeTopic);
	}

	public void deleteTypeTopic(final TypeTopic typeTopic) {
		typeTopicDAO.delete(typeTopic.getTtoCd());
	}
}
