package io.vertigo.chatbot.executor.mail;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class BtNodeMailProvider implements Component {

	@Inject
	private MailService mailService;

	private static final Logger LOGGER = LogManager.getLogger(BtNodeMailProvider.class);

	public BTNode sendMail(final BlackBoard bb, String subjectKey, String messageBodyKey, Optional<String> attachmentKey, String[] destinationKeys) {
		return () -> {
				Optional<FileDescriptor> optFileDescriptor = Optional.empty();
				String recipients = Arrays.stream(destinationKeys).map(dest -> bb.getString(BBKey.of(dest))).collect(Collectors.joining(","));
				if (attachmentKey.isPresent()) {
					FileDescriptor fileDescriptor = new FileDescriptor();
					BBKey rootFileKey = BBKey.of(attachmentKey.get());
					fileDescriptor.setFileName(bb.getString(BBKey.of(rootFileKey, "/filename")));
					String[] fileData = bb.getString(BBKey.of(rootFileKey, "/filecontent")).split(",");
					Assertion.check().isTrue(fileData.length == 2, "Attachment " + fileDescriptor.getFileName() + " is not Base64 encoded");
					fileDescriptor.setFileContent(fileData[1]);
					fileDescriptor.setFileType(URLConnection.getFileNameMap().getContentTypeFor(fileDescriptor.getFileName()));
					optFileDescriptor = Optional.of(fileDescriptor);
				}
				try {
					mailService.sendMail(recipients, bb.getString(BBKey.of(subjectKey)), bb.getString(BBKey.of(messageBodyKey)), optFileDescriptor);
					return BTStatus.Succeeded;
				} catch (MessagingException messagingException) {
					LOGGER.error("Error when sending mail to " + recipients + " with mail subject " + bb.getString(BBKey.of(subjectKey)), messagingException);
					return BTStatus.Failed;
				}
			};

	}
}
