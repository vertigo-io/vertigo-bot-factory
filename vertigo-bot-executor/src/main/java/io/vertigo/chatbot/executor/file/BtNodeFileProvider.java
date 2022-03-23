package io.vertigo.chatbot.executor.file;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datastore.filestore.model.VFile;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class BtNodeFileProvider implements Component {

	@Inject
	private ExecutorManager executorManager;

	public BTNode addImageFileNode(final BlackBoard bb, String label) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, formatImageFile(label));
			return BTStatus.Succeeded;
		};
	}

	public BTNode addFileNode(final BlackBoard bb, String label) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, formatFile(label));
			return BTStatus.Succeeded;
		};
	}

	private String formatImageFile(String label) {
		Assertion.check().isNotNull(label);
		VFile file = executorManager.getAttachment(label);
		return "<img src='data:" + getBase64File(file) + "' class='imgClass' />";
	}

	private String formatFile(String label) {
		Assertion.check().isNotNull(label);
		VFile file = executorManager.getAttachment(label);
		return "<a download=" + file.getFileName() + " href='data:" + getBase64File(file) + "' >" + file.getFileName() + "</a>";
	}

	private String getBase64File(VFile file) {
		try(InputStream inputStream = file.createInputStream()) {
			return file.getMimeType() + ";base64," + Base64.getEncoder().encodeToString(inputStream.readAllBytes());

		} catch (IOException e) {
			throw new VSystemException("Error when reading file with label " + file.getFileName(), e);
		}
	}


}
