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
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.AttachmentInfo;
import io.vertigo.chatbot.commons.FileInfoStd;
import io.vertigo.chatbot.commons.FileInfoTmp;
import io.vertigo.chatbot.commons.FileServices;
import io.vertigo.chatbot.commons.dao.AttachmentFileInfoDAO;
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
import io.vertigo.core.node.component.Component;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.definitions.FileInfoDefinition;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.filestore.util.VFileUtil;
import io.vertigo.datastore.impl.filestore.model.StreamFile;
import static io.vertigo.chatbot.designer.utils.StringUtils.lineError;

@Transactional
public class DesignerFileServices implements Component {

	private static final Logger LOGGER = LogManager.getLogger(DesignerFileServices.class);

	/**
	 * Fragment du message porté par {@link VSystemException} levé par
	 * {@code FsFileStorePlugin} lorsqu'un fichier physique référencé en base
	 * est introuvable sur le système de fichiers.
	 */
	private static final String FILE_NOT_FOUND_MESSAGE_FRAGMENT = "Impossible de trouver le fichier";

	@Inject
	private FileServices fileServices;

	@Inject
	private FileStoreManager fileStoreManager;

	@Inject
	private MediaFileInfoDAO mediaFileInfoDAO;

	@Inject
	private AttachmentFileInfoDAO attachmentFileInfoDAO;

	@Inject
	protected LocaleManager localeManager;

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

	/**
	 * Supprime une pièce jointe (fichier physique + métadonnées en base) de manière
	 * défensive.
	 * <p>
	 * Le plugin {@code FsFileStorePlugin} de Vertigo lève {@link VSystemException}
	 * si le fichier physique référencé en base est absent du système de fichiers
	 * (cf. {@code FileActionDelete.<init>}). Dans ce cas, la suppression de l'entité
	 * en base n'a pas lieu, ce qui ferait remonter l'exception jusqu'à l'appelant
	 * (par exemple {@code AttachmentServices.save}) et provoquerait l'annulation de
	 * la transaction métier.
	 * </p>
	 * <p>
	 * On capture donc cette exception spécifique pour :
	 * <ul>
	 *   <li>tracer un avertissement sur le fichier orphelin,</li>
	 *   <li>nettoyer manuellement la ligne {@code ATTACHMENT_FILE_INFO} restée
	 *       orpheline en base, afin que les opérations métier ultérieures
	 *       (sauvegarde, suppression d'attachement, …) puissent se poursuivre.</li>
	 * </ul>
	 * Toute autre {@link VSystemException} (par exemple un fichier non supprimable
	 * pour cause de droits) est relancée pour ne pas masquer un vrai problème.
	 *
	 * @param attFiId identifiant du {@code AttachmentFileInfo} à supprimer
	 */
	public void deleteAttachment(final Long attFiId) {
		try {
			fileStoreManager.delete(toAttachmentFileInfoUri(attFiId));
		} catch (final VSystemException e) {
			if (isFileNotFoundDeletionException(e)) {
				LOGGER.warn("Le fichier physique de l'attachement (attFiId={}) est introuvable. "
						+ "Suppression des métadonnées orphelines en base.", attFiId, e);
				attachmentFileInfoDAO.delete(attFiId);
			} else {
				throw e;
			}
		}
	}

	public VFile getFileTmp(final FileInfoURI fileTmpUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoTmp.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(fileTmpUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return fileStoreManager.read(fileTmpUri).getVFile();
	}

	public FileInfo getFileInfoTmp(final FileInfoURI fileTmpUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoTmp.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(fileTmpUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		return fileStoreManager.read(fileTmpUri);
	}

	/**
	 * Supprime un fichier temporaire de manière défensive.
	 * <p>
	 * Les fichiers temporaires sont stockés sur le système de fichiers via
	 * {@code FsFullFileStorePlugin} et peuvent avoir été purgés automatiquement
	 * (cf. paramètre {@code purgeDelayMinutes} dans la configuration du store).
	 * On absorbe donc l'exception levée lorsque le fichier est introuvable pour
	 * éviter de faire échouer l'opération métier appelante.
	 *
	 * @param fileTmpUri URI du {@code FileInfoTmp} à supprimer
	 */
	public void deleteFileTmp(final FileInfoURI fileTmpUri) {
		final FileInfoDefinition tmpFileInfoDefinition = FileInfoDefinition.findFileInfoDefinition(FileInfoTmp.class);
		Assertion.check().isTrue(tmpFileInfoDefinition.equals(fileTmpUri.getDefinition()), "Can't access this file storage."); //not too much infos for security purpose
		try {
			fileStoreManager.delete(fileTmpUri);
		} catch (final VSystemException e) {
			if (isFileNotFoundDeletionException(e)) {
				LOGGER.warn("Le fichier temporaire {} est introuvable, il a probablement déjà été purgé.", fileTmpUri, e);
			} else {
				throw e;
			}
		}
	}

	private static FileInfoURI toAttachmentFileInfoUri(final Long attFiId) {
		return new FileInfoURI(FileInfoDefinition.findFileInfoDefinition(AttachmentInfo.class), attFiId);
	}

	/**
	 * Détermine si la {@link VSystemException} fournie correspond au cas d'un
	 * fichier physique manquant lors d'une suppression via
	 * {@code FsFileStorePlugin} / {@code FsFullFileStorePlugin}.
	 * <p>
	 * Le contrôle est basé sur le message de l'exception (Vertigo n'expose pas
	 * de type d'exception dédié pour ce cas).
	 *
	 * @param exception exception levée par {@code FileStoreManager#delete}
	 * @return {@code true} si l'exception correspond à un fichier introuvable
	 */
	private static boolean isFileNotFoundDeletionException(final VSystemException exception) {
		final String message = exception.getMessage();
		return message != null && message.contains(FILE_NOT_FOUND_MESSAGE_FRAGMENT);
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

	public VFile zipMultipleFiles(final Map<String, VFile> fileMap, final String zipFileName) {
		try(final ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
			final ZipOutputStream zipOut = new ZipOutputStream(fos);
			fileMap.forEach((fileType, file) -> {
				try(final InputStream fis = file.createInputStream()) {
					final byte[] bytes = fis.readAllBytes();
					final ZipEntry zipEntry = new ZipEntry(fileType + "/" + file.getFileName());
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

	public Map<String, VFile> unzipMultipleFiles(final VFile zipFile) {
		try (final InputStream is = zipFile.createInputStream();
             final ZipInputStream zis = new ZipInputStream(is)) {

			final Map<String, VFile> fileMap = new HashMap<>();
			ZipEntry zipEntry;
			while ((zipEntry = zis.getNextEntry()) != null) {

				final String [] fileTypeAndName = zipEntry.getName().split("/");
				final String fileType = fileTypeAndName[0];
				final String fileName = fileTypeAndName[1];
				final long size = zipEntry.getSize();
				final Instant lastModified = Instant.ofEpochMilli(zipEntry.getTime());
				final String mimeType = "application/octet-stream"; // You may need to adjust this based on your use case

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final byte[] buffer = new byte[1024];
				int len;
				while ((len = zis.read(buffer)) > 0) {
					baos.write(buffer, 0, len);
				}
				final byte[] content = baos.toByteArray();

				final VFile vFile = new StreamFile(fileName, mimeType, lastModified, size, () -> new ByteArrayInputStream(content));
				fileMap.put(fileType, vFile);

				zis.closeEntry();
			}
			return fileMap;
		} catch (final IOException e) {
			throw new VSystemException(e, "Couldn't unzip files");
		}
	}
}
