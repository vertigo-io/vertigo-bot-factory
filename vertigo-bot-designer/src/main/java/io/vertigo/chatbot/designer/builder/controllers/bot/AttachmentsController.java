package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Attachment;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.designer.builder.services.bot.AttachmentServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.QueryParam;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/attachments")
@Secured("Chatbot$botAdm")
public class AttachmentsController extends AbstractBotListEntityController<Attachment> {

	@Inject
	private AttachmentServices attachmentServices;

	@Inject
	private ChatbotCustomConfigServices chatbotCustomConfigServices;

	private static final ViewContextKey<Attachment> attachmentsKey = ViewContextKey.of("attachments");
	private static final ViewContextKey<Attachment> newAttachmentKey = ViewContextKey.of("newAttachment");
	private static final ViewContextKey<Long> maxSizeKey = ViewContextKey.of("maxSize");
	private static final ViewContextKey<Long> attachmentTotalSizeKey = ViewContextKey.of("attachmentTotalSize");
	private static final ViewContextKey<FileInfoURI> importTopicFileUri = ViewContextKey.of("importAttachmentFileUri");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId);
		final DtList<Attachment> attachments = attachmentServices.findAllByBotId(botId);
		viewContext.publishDtList(attachmentsKey, attachments);
		viewContext.publishDto(newAttachmentKey, new Attachment());
		viewContext.publishRef(maxSizeKey, chatbotCustomConfig.getTotalMaxAttachmentSize());
		viewContext.publishRef(attachmentTotalSizeKey, computeAttachmentTotalSize(attachments));
		viewContext.publishFileInfoURI(importTopicFileUri, null);
		super.initBreadCrums(viewContext, Attachment.class);
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/_saveAttachment")
	public ViewContext saveAttachment(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@QueryParam("importAttachmentFileUri") final Optional<FileInfoURI> attachmentFile,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("maxSize") final Long maxSize,
			@ViewAttribute("attachmentTotalSize") final Long attachmentTotalSize,
			@RequestParam("label") final String label,
			@RequestParam("attId") final Optional<Long> attId) {

		final Attachment attachment = attId.isEmpty() ? new Attachment() : attachmentServices.findById(attId.get());
		attachment.setLabel(label);
		attachment.setBotId(bot.getBotId());
		attachmentServices.save(attachment, attachmentFile, maxSize, attachmentTotalSize);
		final DtList<Attachment> attachments = attachmentServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(attachmentsKey, attachments);
		viewContext.publishRef(attachmentTotalSizeKey, computeAttachmentTotalSize(attachments));
		return viewContext;
	}

	@PostMapping("/_deleteAttachment")
	public ViewContext deleteAttachment(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@RequestParam("attId") final Long attId) {

		attachmentServices.delete(attId);
		final DtList<Attachment> attachments = attachmentServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(attachmentsKey, attachments);
		viewContext.publishRef(attachmentTotalSizeKey, computeAttachmentTotalSize(attachments));
		return viewContext;
	}

	private Long computeAttachmentTotalSize(final DtList<Attachment> attachments) {
		return attachments.stream().mapToLong(Attachment::getLength).sum();
	}
}
