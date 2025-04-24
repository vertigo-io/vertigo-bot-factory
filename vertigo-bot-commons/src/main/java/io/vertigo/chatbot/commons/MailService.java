package io.vertigo.chatbot.commons;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import javax.inject.Inject;
import jakarta.mail.MessagingException;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;
import io.vertigo.social.mail.Mail;
import io.vertigo.social.mail.MailBuilder;
import io.vertigo.social.mail.MailManager;

public class MailService implements Component {

	@Inject
	private MailManager mailManager;

	public void sendMailFromBot(final String sender, final String[] destination, final String subject, final String messageBody, final Optional<FileDescriptor> optFileDescriptor) throws MessagingException {
		Assertion.check().isNotNull(sender, "Bot email address must be set to send emails");
		final MailBuilder mailBuilder = Mail.builder().from(sender)
				.to(destination)
				.withSubject(subject)
				.withHtmlContent(messageBody);
		if (optFileDescriptor.isPresent()) {
			final FileDescriptor fileDescriptor = optFileDescriptor.get();
			final VFile file = new StreamFile(fileDescriptor.getFileName(),
					fileDescriptor.getFileType(), Instant.now(), fileDescriptor.getFileContent().length(),
					() -> new ByteArrayInputStream(Base64.getDecoder().decode(fileDescriptor.getFileContent().getBytes())));
			mailBuilder.withAttachments(file);
		}
		mailManager.sendMail(mailBuilder.build());
	}

	public void sendAlertingGlobalMail(final String sender, final String destination, final String component, final String state) throws MessagingException {
		Assertion.check().isNotNull(sender, "Alerting email address must be set to send emails");
		final String textColor = "OK".equals(state) ? "green" : "red";
		final String messageBody = component + " <span style='font-weight: bold; color: " + textColor + ";'>" + state + "</span>";
		final Mail mail = Mail.builder().from(sender)
				.to(destination)
				.withSubject("[Chatbot - Alerting] " + component + " " + state)
				.withHtmlContent(messageBody)
				.build();
		mailManager.sendMail(mail);
	}

	public void sendAlertingBotMail(final String sender, final String destination, final String botName, final String nodeName, final String component, final String state) throws MessagingException {
		final String textColor = "OK".equals(state) ? "green" : "red";
		final String messageBody = "<div>Bot : " + botName + "</div>" +
				"<div>Noeud : " + nodeName + "</div>" +
				"<div>" + component + " <span style='font-weight: bold; color: " + textColor + ";'>" + state + "</span>" +
				"</div>";
		final Mail mail = Mail.builder().from(sender)
				.to(destination)
				.withSubject("[Chatbot - Alerting] [" + botName + " - " + nodeName + "] " + component + " " + state)
				.withHtmlContent(messageBody)
				.build();
		mailManager.sendMail(mail);
	}
}
