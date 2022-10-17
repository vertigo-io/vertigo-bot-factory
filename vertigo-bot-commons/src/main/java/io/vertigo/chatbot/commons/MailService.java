package io.vertigo.chatbot.commons;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import io.vertigo.connectors.mail.MailSessionConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;

public class MailService implements Component {

	@Inject
	private MailSessionConnector mailSessionConnector;

	public void sendMailFromBot(final String sender, final String destination, final String subject, final String messageBody, final Optional<FileDescriptor> optFileDescriptor) throws MessagingException {
		Assertion.check().isNotNull(sender, "Bot email address must be set to send emails");
		final Session session = mailSessionConnector.getClient();
		final Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sender));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
		message.setSubject(subject);
		final MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(messageBody, "text/html; charset=utf-8");
		final Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		if (optFileDescriptor.isPresent()) {
			final FileDescriptor fileDescriptor = optFileDescriptor.get();
			final MimeBodyPart filePart = new MimeBodyPart();
			filePart.setFileName(fileDescriptor.getFileName());
			filePart.setContent(fileDescriptor.getFileContent(), fileDescriptor.getFileType());
			try {
				final DataSource dataSource = new ByteArrayDataSource(new ByteArrayInputStream(Base64.getDecoder().decode(fileDescriptor.getFileContent())),
						fileDescriptor.getFileType());
				filePart.setDataHandler(new DataHandler(dataSource));
				multipart.addBodyPart(filePart);
			} catch (final IOException e) {
				throw  new VSystemException(e, "Error when sending mail with attachment " + fileDescriptor.getFileName());
			}
		}
		message.setContent(multipart);
		Transport.send(message);
	}

	public void sendAlertingGlobalMail(final String sender, final String destination, final String component, final String state) throws MessagingException {
		Assertion.check().isNotNull(sender, "Alerting email address must be set to send emails");
		final Session session = mailSessionConnector.getClient();
		final Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sender));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
		message.setSubject("[Chatbot - Alerting] " + component + " " + state);
		final MimeBodyPart mimeBodyPart = new MimeBodyPart();
		final String textColor = "OK".equals(state) ? "green" : "red";
		mimeBodyPart.setContent(component + " <span style='font-weight: bold; color: " + textColor + ";'>" + state + "</span>", "text/html; charset=utf-8");
		final Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);
		Transport.send(message);
	}

	public void sendAlertingBotMail(final String sender, final String destination, final String botName, final String nodeName, final String component, final String state) throws MessagingException {
		final Session session = mailSessionConnector.getClient();
		final Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sender));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
		message.setSubject("[Chatbot - Alerting] [" + botName + " - " + nodeName + "] " + component + " " + state);
		final MimeBodyPart mimeBodyPart = new MimeBodyPart();
		final String textColor = "OK".equals(state) ? "green" : "red";
		mimeBodyPart.setContent("<div>Bot : " + botName + "</div>" +
						"<div>Noeud : " + nodeName + "</div>" +
				 		"<div>" + component + " <span style='font-weight: bold; color: " + textColor + ";'>" + state + "</span>" +
						"</div>", "text/html; charset=utf-8");
		final Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);
		Transport.send(message);
	}
}
