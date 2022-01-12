package io.vertigo.chatbot.executor.mail;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.node.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.mail.MessagingException;

import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.inputString;

public class BtNodeMailProvider implements Component {

	@Inject
	private MailService mailService;

	private static final Logger LOGGER = LogManager.getLogger(BtNodeMailProvider.class);

	public BTNode sendMail(final BlackBoard bb,
						   String destinationKey, String destinationQuestion,
						   String subjectKey, String subjectQuestion,
						   String messageBodyKey, String messageQuestion) {
		return sequence (
			inputString(bb, destinationKey, destinationQuestion),
			inputString(bb, subjectKey, subjectQuestion),
			inputString(bb, messageBodyKey, messageQuestion),
			() -> {
				try {
					mailService.sendMail(bb.getString(BBKey.of(destinationKey)), bb.getString(BBKey.of(subjectKey)), bb.getString(BBKey.of(messageBodyKey)));
					return BTStatus.Succeeded;
				} catch (MessagingException messagingException) {
					LOGGER.error("Error when sending mail to " + bb.getString(BBKey.of(destinationKey)) + " with mail subject " + bb.getString(BBKey.of(subjectKey)), messagingException);
					return BTStatus.Failed;
				}
			}
		);
	}
}
