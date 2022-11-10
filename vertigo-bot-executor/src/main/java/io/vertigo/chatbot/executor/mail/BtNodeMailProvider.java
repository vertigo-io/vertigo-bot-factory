package io.vertigo.chatbot.executor.mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLConnection;
import java.util.Optional;

import javax.inject.Inject;
import javax.mail.MessagingException;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.commons.FileDescriptor;
import io.vertigo.chatbot.commons.MailService;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;

public class BtNodeMailProvider implements Component {

	@Inject
	private MailService mailService;

	@Inject
	private ExecutorManager executorManager;

	private static final Logger LOGGER = LogManager.getLogger(BtNodeMailProvider.class);

	public BTNode sendMail(final BlackBoard bb, final String subjectKey, final String messageBodyKey, final Optional<String> attachmentKey, final String destinationsKey) {
		return () -> {
				Optional<FileDescriptor> optFileDescriptor = Optional.empty();
				final int recipientsCount = bb.listSize(BBKey.of(destinationsKey));
				final String[] recipients = new String[recipientsCount];
				for (int i = 0; i < recipientsCount; i++) {
					recipients[i] = bb.listGet(BBKey.of(destinationsKey), i);
				}
				if (attachmentKey.isPresent()) {
					final FileDescriptor fileDescriptor = new FileDescriptor();
					final BBKey rootFileKey = BBKey.of(attachmentKey.get());
					fileDescriptor.setFileName(bb.getString(BBKey.of(rootFileKey, "/filename")));
					final String[] fileData = bb.getString(BBKey.of(rootFileKey, "/filecontent")).split(",");
					Assertion.check().isTrue(fileData.length == 2, "Attachment " + fileDescriptor.getFileName() + " is not Base64 encoded");
					fileDescriptor.setFileContent(fileData[1]);
					fileDescriptor.setFileType(URLConnection.getFileNameMap().getContentTypeFor(fileDescriptor.getFileName()));
					optFileDescriptor = Optional.of(fileDescriptor);
				}
				try {
					mailService.sendMailFromBot(executorManager.getBotEmailAddress(), recipients, bb.getString(BBKey.of(subjectKey)), bb.getString(BBKey.of(messageBodyKey)), optFileDescriptor);
					return BTStatus.Succeeded;
				} catch (final MessagingException messagingException) {
					LOGGER.error("Error when sending mail to " + recipients + " with mail subject " + bb.getString(BBKey.of(subjectKey)), messagingException);
					return BTStatus.Failed;
				}
			};

	}
}
