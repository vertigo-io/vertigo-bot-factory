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

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.exceptions.CsvValidationException;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.AttachmentInfo;
import io.vertigo.chatbot.commons.dao.MediaFileInfoDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.MediaFileInfo;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.Component;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.filestore.util.VFileUtil;

import javax.inject.Inject;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.StringUtils.lineError;

@Transactional
public class FileServices implements Component {

	@Inject
	private FileStoreManager fileStoreManager;

	@Inject
	private MediaFileInfoDAO mediaFileInfoDAO;

	@Inject
	protected LocaleManager localeManager;

	public FileInfoURI saveFileTmp(final VFile file) {
		//apply security check
		final FileInfo fileInfo = fileStoreManager.create(new FileInfoTmp(file));
		return fileInfo.getURI();
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

	public VFile getAttachment(final FileInfoURI attachmentUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(AttachmentInfo.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(attachmentUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return fileStoreManager.read(attachmentUri).getVFile();
	}

	public VFile getAttachment(final Long attFiId) {
		return getAttachment(toAttachmentFileInfoUri(attFiId));
	}

	public void deleteAttachment(final Long attFiId) {
		deleteAttachment(toAttachmentFileInfoUri(attFiId));
	}

	public VFile getFileTmp(final FileInfoURI fileTmpUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoTmp.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(fileTmpUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return fileStoreManager.read(fileTmpUri).getVFile();
	}

	public void deleteFileTmp(final FileInfoURI fileTmpUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoTmp.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(fileTmpUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		fileStoreManager.delete(fileTmpUri);
	}

	public FileInfoURI toAttachmentFileInfoUri(final Long attFiId) {
		return new FileInfoURI(FileInfoDefinition.findFileInfoDefinition(AttachmentInfo.class), attFiId);
	}

	public FileInfoURI toStdFileInfoUri(final Long fileId) {
		return new FileInfoURI(FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class), fileId);
	}

	public FileInfoURI saveFile(final VFile file) {
		//apply security check
		final FileInfo fileInfo = fileStoreManager.create(new FileInfoStd(file));
		return fileInfo.getURI();
	}

	public VFile getFile(final Long filId) {
		return getFile(toStdFileInfoUri(filId));
	}

	public VFile getFile(final FileInfoURI fileUri) {
		final FileInfoDefinition fileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class);
		Assertion.check().isTrue(fileInfoDefinition.equals(fileUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return fileStoreManager.read(fileUri).getVFile();
	}

	public void deleteFile(final FileInfoURI fileUri) {
		final FileInfoDefinition fileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoStd.class);
		Assertion.check().isTrue(fileInfoDefinition.equals(fileUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		fileStoreManager.delete(fileUri);
	}

	public void deleteFile(final Long fileId) {
		deleteFile(toStdFileInfoUri(fileId));
	}

	public void deleteChatbotFile(@SecuredOperation("botAdm") final Chatbot bot, final Long filId) {
		mediaFileInfoDAO.delete(filId);
	}

	public boolean isCSVFile(VFile file) {
		return file.getFileName().toLowerCase().endsWith(".csv");
	}

	public <G> List<G> readCsvFile(Class<G> clazz, VFile file, String[] columns) {
		if (!isCSVFile(file)) {
			throw new VUserException(ExportMultilingualResources.ERR_CSV_FILE);
		}
		try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(VFileUtil.obtainReadOnlyPath(file).toString(), Charset.forName("cp1252")))
				.withErrorLocale(localeManager.getCurrentLocale())
				.withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {

			final String[] header = csvReader.readNext();
			if (header.length != columns.length) {
				throw new VUserException(ExportMultilingualResources.ERR_SIZE_FILE, columns.length);
			}
			final CsvToBean<G> csvToBean = new CsvToBean<>();
			final ColumnPositionMappingStrategy<G> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(clazz);
			mappingStrategy.setColumnMapping(columns);
			csvToBean.setMappingStrategy(mappingStrategy);
			csvToBean.setCsvReader(csvReader);
			csvToBean.setThrowExceptions(false);
			final List<G> list = csvToBean.parse();
			if (!csvToBean.getCapturedExceptions().isEmpty()) {
				String errorMessage = csvToBean.getCapturedExceptions().stream().map(exception -> lineError(exception.getLine()[0], exception.getMessage())).collect(Collectors.joining(","));
				throw new VUserException(ExportMultilingualResources.ERR_MAPPING_FILE, errorMessage);
			}
			return list;

		} catch (IOException | RuntimeException e) {
			throw new VUserException(ExportMultilingualResources.ERR_UNEXPECTED, e.getMessage());
		} catch (CsvValidationException csvValidationException) {
			throw new VUserException(ExportMultilingualResources.ERR_MAPPING_FILE, csvValidationException.getMessage());
		}
	}

	public String getFileAsBase64(Long id) {
		try (InputStream fileInputStream = getMediaFileInfoById(id).getFileData().createInputStream()) {
			return Base64.getEncoder().encodeToString(fileInputStream.readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private MediaFileInfo getMediaFileInfoById(Long id) {
		return mediaFileInfoDAO.get(id);
	}
}
