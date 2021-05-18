package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Locale;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.KindTopicDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopic;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.UID;

@Transactional
@Secured("BotUser")
public class KindTopicServices implements Component {

	@Inject
	private KindTopicDAO kindTopicDAO;

	public KindTopic save(@SecuredOperation("botAdm") final Chatbot bot, final KindTopic kto) {
		return kindTopicDAO.save(kto);
	}

	public void delete(@SecuredOperation("botAdm") final Chatbot bot, final UID<KindTopic> ktoCd) {
		kindTopicDAO.delete(ktoCd);
	}

	public KindTopic findKindTopicByCd(final String ktoCd) {
		return kindTopicDAO.get(ktoCd);
	}

	public String getDefaultTextByLocale(final KindTopic kto, final Locale locale) {
		if (Locale.FRANCE.equals(locale)) {
			return kto.getDefaultFrench();
		}
		return kto.getDefaultEnglish();
	}

}
