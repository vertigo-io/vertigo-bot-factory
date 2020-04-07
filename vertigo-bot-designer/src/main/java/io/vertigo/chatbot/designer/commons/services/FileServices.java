/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.designer.commons.services;

import javax.inject.Inject;

import io.vertigo.chatbot.fileinfo.FileInfoStd;
import io.vertigo.chatbot.fileinfo.FileInfoTmp;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.FileInfoURI;
import io.vertigo.dynamo.file.metamodel.FileInfoDefinition;
import io.vertigo.dynamo.file.model.FileInfo;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.lang.Assertion;

@Transactional
public class FileServices implements Component {

	@Inject
	private StoreManager storeManager;

	public FileInfoURI saveFileTmp(final VFile file) {
		//apply security check
		final FileInfo fileInfo = storeManager.getFileStore().create(new FileInfoTmp(file));
		return fileInfo.getURI();
	}

	public VFile getFileTmp(final FileInfoURI fileTmpUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoTmp.class);
		Assertion.checkArgument(tmpFileInfoDefinition.equals(fileTmpUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return storeManager.getFileStore().read(fileTmpUri).getVFile();
	}

	public void deleteFileTmp(final FileInfoURI fileTmpUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoTmp.class);
		Assertion.checkArgument(tmpFileInfoDefinition.equals(fileTmpUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		storeManager.getFileStore().delete(fileTmpUri);
	}

	public FileInfoURI toStdFileInfoUri(final Long fileId) {
		return new FileInfoURI(FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class), fileId);
	}

	public FileInfoURI saveFile(final VFile file) {
		//apply security check
		final FileInfo fileInfo = storeManager.getFileStore().create(new FileInfoStd(file));
		return fileInfo.getURI();
	}

	public VFile getFile(final Long filId) {
		return getFile(toStdFileInfoUri(filId));
	}

	public VFile getFile(final FileInfoURI fileUri) {
		final FileInfoDefinition fileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class);
		Assertion.checkArgument(fileInfoDefinition.equals(fileUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return storeManager.getFileStore().read(fileUri).getVFile();
	}

	public void deleteFile(final FileInfoURI fileUri) {
		final FileInfoDefinition fileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class);
		Assertion.checkArgument(fileInfoDefinition.equals(fileUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		storeManager.getFileStore().delete(fileUri);
	}

	public void deleteFile(final Long fileId) {
		deleteFile(toStdFileInfoUri(fileId));
	}

}
