package io.vertigo.chatbot.commons;

import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.AbstractFileInfo;

public final class AttachmentInfo extends AbstractFileInfo {

	/**
	 * Constructeur par défaut.
	 * @param vFile Données du fichier
	 */
	public AttachmentInfo(final VFile vFile) {
		super(FileInfoDefinition.findFileInfoDefinition(AttachmentInfo.class), vFile);
	}
}
