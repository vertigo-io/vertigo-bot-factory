package io.vertigo.chatbot.engine.plugins.bt.jira.model;

import java.util.List;

public class JsmTemporaryAttachmentResult {

	private List<JsmTemporaryAttachment> temporaryAttachments;

	public List<JsmTemporaryAttachment> getTemporaryAttachments() {
		return temporaryAttachments;
	}

	public void setTemporaryAttachments(final List<JsmTemporaryAttachment> temporaryAttachments) {
		this.temporaryAttachments = temporaryAttachments;
	}
}
