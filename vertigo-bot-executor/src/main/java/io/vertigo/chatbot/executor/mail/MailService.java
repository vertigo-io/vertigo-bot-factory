package io.vertigo.chatbot.executor.mail;

import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.connectors.mail.MailSessionConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;

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

public class MailService implements Component {

	@Inject
	private MailSessionConnector mailSessionConnector;

	@Inject
	private ExecutorManager executorManager;

	public void sendMail(String destination, String subject, String messageBody) throws MessagingException {
		String botEmailAddress = executorManager.getBotEmailAddress();
		Assertion.check().isNotNull(botEmailAddress, "Bot email address must be set to send emails");
		Session session = mailSessionConnector.getClient();
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(botEmailAddress));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
		message.setSubject(subject);
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(messageBody, "text/html; charset=utf-8");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);
		Transport.send(message);
	}
}
