package io.vertigo.chatbot.engine.plugins.bt.jira.model;

public class JsmTemporaryAttachment {

	private String temporaryAttachmentId;
	private String fileName;

	public String getTemporaryAttachmentId() {
		return temporaryAttachmentId;
	}

	public void setTemporaryAttachmentId(final String temporaryAttachmentId) {
		this.temporaryAttachmentId = temporaryAttachmentId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}
}
