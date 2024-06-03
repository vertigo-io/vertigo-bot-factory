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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.AntivirusServices;
import io.vertigo.chatbot.commons.AttachmentInfo;
import io.vertigo.chatbot.commons.FileInfoStd;
import io.vertigo.chatbot.commons.FileInfoTmp;
import io.vertigo.chatbot.commons.FileServices;
import io.vertigo.chatbot.commons.dao.MediaFileInfoDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.MediaFileInfo;
import io.vertigo.chatbot.commons.multilingual.AttachmentMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.filestore.util.VFileUtil;
import io.vertigo.datastore.impl.filestore.model.StreamFile;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import static io.vertigo.chatbot.commons.ChatbotUtils.MAX_UPLOAD_SIZE;
import static io.vertigo.chatbot.designer.utils.StringUtils.lineError;

@Transactional
public class DesignerFileServices implements Component, Activeable {

	@Inject
	private FileServices fileServices;

	@Inject
	private FileStoreManager fileStoreManager;

	@Inject
	private MediaFileInfoDAO mediaFileInfoDAO;

	@Inject
	protected LocaleManager localeManager;

	@Inject
	private ParamManager paramManager;

	@Inject
	private AntivirusServices antivirusServices;

	private String[] extensionsWhiteList;

	@Override
	public void start() {
		extensionsWhiteList = paramManager.getOptionalParam("EXTENSIONS_WHITELIST")
				.map(Param::getValueAsString).orElse("png,jpg,jpeg,pdf,csv,js").split(",");
	}

	@Override
	public void stop() {

	}

	public FileInfoURI saveFileTmp(final VFile file) {
		final FileInfo fileInfo = fileStoreManager.create(new FileInfoTmp(file));
		return fileInfo.getURI();
	}

	public void checkFile (final VFile file) {
        try {
            fileServices.checkFile(file.getFileName(), file.getLength(), file.createInputStream());
        } catch (final IOException ioException) {
			throw new VUserException(AttachmentMultilingualResources.COULD_NOT_OPEN_FILE, file.getFileName(), ioException);
		}
    }

	public FileInfoURI saveAttachment(final VFile file) {
		final FileInfo fileInfo = fileStoreManager.create(new AttachmentInfo(file));
		return fileInfo.getURI();
	}

	public VFile getAttachment(final Long attFiId) {
		return fileStoreManager.read(toAttachmentFileInfoUri(attFiId)).getVFile();
	}

	public void deleteAttachment(final Long attFiId) {
		fileStoreManager.delete(toAttachmentFileInfoUri(attFiId));
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

	private static FileInfoURI toAttachmentFileInfoUri(final Long attFiId) {
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

	public boolean isCSVFile(final VFile file) {
		return file.getFileName().toLowerCase().endsWith(".csv");
	}

	public <G> List<G> readCsvFile(final Class<G> clazz, final VFile file, final String[] columns) {
		if (!isCSVFile(file)) {
			throw new VUserException(ExportMultilingualResources.ERR_CSV_FILE);
		}
		try (final CSVReader csvReader = new CSVReaderBuilder(new FileReader(VFileUtil.obtainReadOnlyPath(file).toString(), Charset.forName("cp1252")))
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
				final String errorMessage = csvToBean.getCapturedExceptions().stream().map(exception -> lineError(exception.getLine()[0], exception.getMessage())).collect(Collectors.joining(","));
				throw new VUserException(ExportMultilingualResources.ERR_MAPPING_FILE, errorMessage);
			}
			return list;

		} catch (final IOException | RuntimeException e) {
			throw new VUserException(ExportMultilingualResources.ERR_UNEXPECTED, e.getMessage());
		} catch (final CsvValidationException csvValidationException) {
			throw new VUserException(ExportMultilingualResources.ERR_MAPPING_FILE, csvValidationException.getMessage());
		}
	}

	public String getFileAsBase64(final Long id) {
		try (final InputStream fileInputStream = getMediaFileInfoById(id).getFileData().createInputStream()) {
			return Base64.getEncoder().encodeToString(fileInputStream.readAllBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private MediaFileInfo getMediaFileInfoById(final Long id) {
		return mediaFileInfoDAO.get(id);
	}

	public VFile zipMultipleFiles(final List<VFile> files, final String zipFileName) {
		try(final ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
			final ZipOutputStream zipOut = new ZipOutputStream(fos);
			files.forEach(file -> {
				try(final InputStream fis = file.createInputStream()) {
					final byte[] bytes = fis.readAllBytes();
					final ZipEntry zipEntry = new ZipEntry(file.getFileName());
					zipEntry.setSize(bytes.length);
					zipOut.putNextEntry(zipEntry);
					zipOut.write(bytes);
					zipOut.closeEntry();
				} catch (final IOException e) {
					throw new VSystemException(e, "Couldn't zip file with name {0}", file.getFileName());
				}
			});
			zipOut.close();
			final byte[] bytes = fos.toByteArray();
			return StreamFile.of(zipFileName + ".zip", "application/zip", Instant.now(), bytes.length,
					() -> new ByteArrayInputStream(bytes));
		} catch (final IOException e) {
			throw new VSystemException(e, "Couldn't build zip file");
		}
	}
}
