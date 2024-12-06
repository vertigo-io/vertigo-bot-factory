package io.vertigo.chatbot.designer.builder.services.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.AttachmentDAO;
import io.vertigo.chatbot.commons.domain.Attachment;
import io.vertigo.chatbot.commons.domain.AttachmentExport;
import io.vertigo.chatbot.commons.domain.AttachmentFileInfo;
import io.vertigo.chatbot.commons.domain.AttachmentTypeEnum;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.attachment.AttachmentMultilingualResources;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class AttachmentServices implements Component {

	@Inject
	private AttachmentDAO attachmentDAO;

	@Inject
	private DesignerFileServices designerFileServices;

	public Attachment findById(final long attachmentId) {
		return attachmentDAO.get(attachmentId);
	}

	@Secured("BotUser")
	public Attachment save(@SecuredOperation("botAdm") final Chatbot bot, final Attachment attachment, final Optional<FileInfoURI> optFileInfoURI, final Long maxSize, final Long attachmentTotalSize) {

		if (attachment.getAttId() == null && optFileInfoURI.isEmpty()) {
			throw new VUserException(AttachmentMultilingualResources.MUST_CONTAINS_A_FILE);
		}

		if (optFileInfoURI.isPresent()) {
			Long oldAttachmentFileId = attachment.getAttFiId();
			Long oldAttachmentSize = 0L;
			if (attachment.getAttId() != null) {
				final Attachment oldAttachment = findById(attachment.getAttId());
				oldAttachmentSize = oldAttachment.getLength();
				oldAttachmentFileId = oldAttachment.getAttFiId();
			}
			final VFile newAttachmentFile = designerFileServices.getFileTmp(optFileInfoURI.get());

			if (isMaxSizeExceeded(maxSize, attachmentTotalSize, oldAttachmentSize, newAttachmentFile.getLength())) {
				throw new VUserException(AttachmentMultilingualResources.MAX_TOTAL_SIZE_EXCEEDED, maxSize);
			}
			final FileInfoURI fileInfoUri = designerFileServices.saveAttachment(newAttachmentFile);
			attachment.setAttFiId((Long) fileInfoUri.getKey());
			attachment.setType(newAttachmentFile.getMimeType());
			attachment.setLength(newAttachmentFile.getLength());
			final Attachment savedAttachment = attachmentDAO.save(attachment);
			if (oldAttachmentFileId != null) {
				designerFileServices.deleteAttachment(oldAttachmentFileId);
			}
			return savedAttachment;
		}
		return attachmentDAO.save(attachment);
	}

	private static boolean isMaxSizeExceeded(final Long maxSize, final Long totalSize, final Long oldAttachmentSize, final Long newAttachmentSize) {
		return maxSize != null && maxSize > 0 && ((totalSize - oldAttachmentSize) + newAttachmentSize) > (1024 * 1024 * maxSize);
	}

	public DtList<Attachment> findAllByBotId(final long botId) {
		return attachmentDAO.findAll(Criterions.isEqualTo(DtDefinitions.AttachmentFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public DtList<Attachment> findAllByBotIdAndType(final Long botId, final String attachmentTypeName) {
		return attachmentDAO.findAll(Criterions.isEqualTo(DtDefinitions.AttachmentFields.botId, botId).and(Criterions.isEqualTo(DtDefinitions.AttachmentFields.attTypeCd, attachmentTypeName)), DtListState.of(null));
	}

	@Secured("BotUser")
	public void delete (@SecuredOperation("botAdm") final Chatbot bot, final long attachmentId) {
		final Attachment attachment = findById(attachmentId);
		attachmentDAO.delete(attachmentId);
		designerFileServices.deleteAttachment(attachment.getAttFiId());
	}

	public DtList<AttachmentExport> exportAttachmentByBot(@SecuredOperation("botAdm") final Chatbot bot, final StringBuilder logs) {
		LogsUtils.addLogs(logs, "Export attachments : ");
		try {
			return findAllByBotId(bot.getBotId()).stream().map(attachment -> {
				attachment.attachmentFileInfo().load();
				final AttachmentFileInfo attachmentFileInfo = attachment.attachmentFileInfo().get();
				final AttachmentExport attachmentExport = new AttachmentExport();
				attachmentExport.setLabel(attachment.getLabel());
				attachmentExport.setFileName(attachmentFileInfo.getFileName());
				attachmentExport.setMimeType(attachmentFileInfo.getMimeType());
				attachmentExport.setLength(attachmentFileInfo.getLength());
				final VFile file = designerFileServices.getAttachment(attachment.getAttFiId());
				try (final InputStream inputStream = file.createInputStream()) {
					attachmentExport.setFileData(Base64.getEncoder().encodeToString(inputStream.readAllBytes()));
				} catch (final IOException e) {
					LogsUtils.logKO(logs);
					LogsUtils.addLogs(logs, e);
				}
				return attachmentExport;
			}).collect(VCollectors.toDtList(AttachmentExport.class));

		} catch (final Exception e) {
			LogsUtils.logKO(logs);
			LogsUtils.addLogs(logs, e);
			throw new VUserException(AttachmentMultilingualResources.EXPORT_UNEXPECTED_ERROR, e);
		}

	}
}
