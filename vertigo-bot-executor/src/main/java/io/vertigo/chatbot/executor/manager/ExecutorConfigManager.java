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
package io.vertigo.chatbot.executor.manager;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.AttachmentExport;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.WelcomeTourExport;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.chatbot.executor.services.ExecutorFileServices;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Manager;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

public class ExecutorConfigManager implements Manager, Activeable {

	private final ParamManager paramManager;
	private final JsonEngine jsonEngine;

	private File configDataFile;
	private File contextDataFile;
	private ExecutorGlobalConfig executorGlobalConfig;
	private HashMap<String, String> contextMap;
	private final List<ExecutorPlugin> plugins = new ArrayList<>();
	private Map<String, String> mapAttachments;
	private File attachmentDataFile;

	@Inject
	private ExecutorFileServices executorFileServices;

	@Inject
	public ExecutorConfigManager(
			final ParamManager paramManager,
			final JsonEngine jsonEngine) {

		Assertion.check()
				.isNotNull(paramManager)
				.isNotNull(jsonEngine);
		//--
		this.paramManager = paramManager;
		this.jsonEngine = jsonEngine;
	}

	@Override
	public void start() {

		final String configDataFilePath = paramManager.getOptionalParam("CONFIG_DATA_FILE").map(Param::getValueAsString).orElse("/tmp/runnerConfig");

		configDataFile = new File(configDataFilePath);
		StringBuilder logs = new StringBuilder();
		if (configDataFile.exists() && configDataFile.canRead()) {
			try {
				final String json = FileUtils.readFileToString(configDataFile, StandardCharsets.UTF_8);
				executorGlobalConfig = jsonEngine.fromJson(json, ExecutorGlobalConfig.class);
			} catch (final Exception e) {
				throw new VSystemException(e, "Error reading parameter file {0}", configDataFilePath);
			}
			plugins.forEach(executorPlugin -> executorPlugin.refreshConfig(executorGlobalConfig, logs));

			// Migration purpose as 18/02/2020
			if (executorGlobalConfig.getExecutorConfiguration() != null && executorGlobalConfig.getExecutorConfiguration().getNluThreshold() == null) {
				executorGlobalConfig.getExecutorConfiguration().setNluThreshold(BigDecimal.valueOf(0.6));
			}
		} else {
			executorGlobalConfig = new ExecutorGlobalConfig();
		}
		final String contextDataFilePath = paramManager.getOptionalParam("CONTEXT_DATA_FILE").map(Param::getValueAsString).orElse("/tmp/contextConfig");
		contextDataFile = new File(contextDataFilePath);
		if (contextDataFile.exists() && contextDataFile.canRead()) {
			try {
				final String json = FileUtils.readFileToString(contextDataFile, StandardCharsets.UTF_8);
				contextMap = jsonEngine.fromJson(json, HashMap.class);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			contextMap = new HashMap<String, String>();
		}

		final String attachmentDataFilePath = paramManager.getOptionalParam("ATTACHMENT_DATA_FILE")
				.map(Param::getValueAsString).orElse("/tmp/attachmentConfig");
		attachmentDataFile = new File(attachmentDataFilePath);
		if (attachmentDataFile.exists() && attachmentDataFile.canRead()) {
			try {
				final String json = FileUtils.readFileToString(attachmentDataFile, StandardCharsets.UTF_8);
				mapAttachments = jsonEngine.fromJson(json, HashMap.class);
			} catch (final IOException e) {
				throw new VSystemException("Could not retrieve attachments map at startup...", e);
			}

		} else {
			mapAttachments = new HashMap<>();
		}
	}

	@Override
	public void stop() {
		// Nothing
	}

	public synchronized void saveConfig(final ExecutorGlobalConfig executorGlobalConfig, StringBuilder logs) {
		this.executorGlobalConfig = executorGlobalConfig;
		plugins.forEach(executorPlugin -> executorPlugin.refreshConfig(executorGlobalConfig, logs));
		updateGlobalConfig();
	}

	private void updateGlobalConfig() {
		final String json = jsonEngine.toJson(executorGlobalConfig);
		try {
			FileUtils.writeStringToFile(configDataFile, json, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			throw new VSystemException(e, "Error writing parameter file {0}", configDataFile.getPath());
		}
	}

	public void updateWelcomeTour(final DtList<WelcomeTourExport> welcomeTourExports) {
		final StringBuilder jsString = new StringBuilder();
		jsString.append("const welcomeTours = []; \n");
		welcomeTourExports.forEach(welcomeTourExport -> {
			if (welcomeTourExport.getConfig() != null) {
				jsString.append("welcomeTours[\"")
						.append(welcomeTourExport.getTechnicalCode()).append("\"]")
						.append(" = ").append(" new Shepherd.Tour(")
						.append(welcomeTourExport.getConfig()).append("); \n\n");
			}
		});
		jsString.append("window.addEventListener(\n" +
				"          'message',\n" +
				"          function (event) {\n" +
				"            if (event.data.welcomeTour) {\n" +
				"             	welcomeTours[event.data.welcomeTour].start();\n" +
				"            }\n" +
				"		}); \n");

		if (executorGlobalConfig.getWelcomeToursFileURN() != null) {
			executorFileServices.deleteFile(executorGlobalConfig.getWelcomeToursFileURN());
		}
		final byte[] jsBytes = jsString.toString().getBytes(StandardCharsets.UTF_8);
		final StreamFile streamFile = StreamFile.of("welcomeTours.js", "text/javascript",
				Instant.now(), jsBytes.length,
				() -> new ByteArrayInputStream(jsBytes));

		executorGlobalConfig.setWelcomeToursFileURN(executorFileServices.saveFile(streamFile).toURN());
		updateGlobalConfig();
	}

	/**
	 * @return the executorConfiguration
	 */
	public ExecutorGlobalConfig getConfig() {
		return executorGlobalConfig;
	}

	public HashMap<String, String> getContextMap() {
		return contextMap;
	}

	public synchronized void updateMapContext(final BotExport botExport) {

		try {
			FileUtils.writeStringToFile(contextDataFile, botExport.getMapContext(), StandardCharsets.UTF_8);
			contextMap = jsonEngine.fromJson(botExport.getMapContext(), HashMap.class);
		} catch (final IOException e) {
			throw new VSystemException(e, "Error writing parameter file {0}", contextDataFile.getPath());
		}
	}

	public void updateAttachments(final DtList<AttachmentExport> attachmentExports) {
		try {
			mapAttachments.forEach((key, value) -> executorFileServices.deleteFile(FileInfoURI.fromURN(value)));
			final HashMap<String, String> attachmentsMap = new HashMap<>();
			attachmentExports.forEach(attachmentExport -> {
				final StreamFile streamFile = StreamFile.of(attachmentExport.getFileName(), attachmentExport.getMimeType(),
						Instant.now(), attachmentExport.getLength(),
						() -> new ByteArrayInputStream((Base64.getDecoder().decode(attachmentExport.getFileData()))));

				final FileInfoURI fileInfoURI = executorFileServices.saveFile(streamFile);
				attachmentsMap.put(attachmentExport.getLabel(), fileInfoURI.toURN());
			});
			FileUtils.writeStringToFile(attachmentDataFile, jsonEngine.toJson(attachmentsMap), StandardCharsets.UTF_8);
			mapAttachments = attachmentsMap;
		} catch (final IOException e) {
			throw new VSystemException(e, "Error writing parameter file {0}", attachmentDataFile.getPath());
		}
	}

	public VFile getAttachment(final String label) {
		final String urn = mapAttachments.get(label);
		if (urn == null) {
			throw new VSystemException("Attachment with label " + label + " doesn't exist...");
		}
		return executorFileServices.getFile(urn);
	}

	public Optional<VFile> getWelcomeToursFile() {
		final String welcomeToursFileURN = executorGlobalConfig.getWelcomeToursFileURN();
		return welcomeToursFileURN != null ? Optional.of(executorFileServices.getFile(welcomeToursFileURN)) : Optional.empty();
	}

	public void addPlugin(final ExecutorPlugin executorPlugin) {
		plugins.add(executorPlugin);
	}
}
