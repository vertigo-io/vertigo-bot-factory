package io.vertigo.chatbot.executor.webservices;

import io.vertigo.chatbot.commons.AttachmentInfo;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;

import javax.inject.Inject;

@Transactional
public class FileServices implements Component {

	@Inject
	private FileStoreManager fileStoreManager;


	public VFile getFile(final String urn) {
		FileInfoURI fileInfoURI = FileInfoURI.fromURN(urn);
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(AttachmentInfo.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(fileInfoURI.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return fileStoreManager.read(fileInfoURI).getVFile();
	}

	public FileInfoURI saveAttachment(final VFile file) {
		//apply security check
		final FileInfo fileInfo = fileStoreManager.create(new AttachmentInfo(file));
		return fileInfo.getURI();
	}

	public void deleteAttachment(final FileInfoURI attachmentUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(AttachmentInfo.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(attachmentUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		fileStoreManager.delete(attachmentUri);
	}
}
