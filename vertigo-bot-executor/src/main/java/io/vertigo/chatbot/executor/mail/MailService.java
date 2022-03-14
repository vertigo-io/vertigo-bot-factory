package io.vertigo.chatbot.executor.mail;

import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.connectors.mail.MailSessionConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;

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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

public class MailService implements Component {

	@Inject
	private MailSessionConnector mailSessionConnector;

	@Inject
	private ExecutorManager executorManager;

	public void sendMail(String destination, String subject, String messageBody, Optional<FileDescriptor> optFileDescriptor) throws MessagingException {
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
		if (optFileDescriptor.isPresent()) {
			FileDescriptor fileDescriptor = optFileDescriptor.get();
			MimeBodyPart filePart = new MimeBodyPart();
			filePart.setFileName(fileDescriptor.getFileName());
			filePart.setContent(fileDescriptor.getFileContent(), fileDescriptor.getFileType());
			try {
				DataSource dataSource = new ByteArrayDataSource(new ByteArrayInputStream(Base64.getDecoder().decode(fileDescriptor.getFileContent())),
						fileDescriptor.getFileType());
				filePart.setDataHandler(new DataHandler(dataSource));
				multipart.addBodyPart(filePart);
			} catch (IOException e) {
				throw  new VSystemException(e, "Error when sending mail with attachment " + fileDescriptor.getFileName());
			}
		}
		message.setContent(multipart);
		Transport.send(message);
	}
}
