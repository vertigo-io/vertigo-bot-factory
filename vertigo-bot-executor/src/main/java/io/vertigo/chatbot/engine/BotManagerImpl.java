package io.vertigo.chatbot.engine;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoardManager;
import io.vertigo.ai.bt.BehaviorTreeManager;
import io.vertigo.ai.nlu.NluIntent;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.AttachmentExport;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.engine.services.FileServices;
import io.vertigo.commons.codec.CodecManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BotManagerImpl implements BotManager, Activeable {
	private final BlackBoardManager blackBoardManager;
	private final BehaviorTreeManager behaviorTreeManager;
	private final NluManager nluManager;
	private final CodecManager codecManager;
	private final FileServices fileServices;
	private final ParamManager paramManager;
	private final JsonEngine jsonEngine;

	private Map<String, TopicDefinition> topicDefinitionMap; // immutable map of topics
	private Map<String, String> mapAttachments;
	private File attachmentDataFile;

	private final Map<String, String> welcomeTourMap;

	@Inject
	public BotManagerImpl(
			final CodecManager codecManager,
			final BlackBoardManager blackBoardManager,
			final BehaviorTreeManager behaviorTreeManager,
			final NluManager nluManager,
			final FileServices fileServices,
			final ParamManager paramManager,
			final JsonEngine jsonEngine) {
		Assertion.check()
				.isNotNull(codecManager)
				.isNotNull(blackBoardManager)
				.isNotNull(behaviorTreeManager)
				.isNotNull(nluManager)
				.isNotNull(fileServices)
				.isNotNull(paramManager)
				.isNotNull(jsonEngine);
		//---
		this.blackBoardManager = blackBoardManager;
		this.behaviorTreeManager = behaviorTreeManager;
		this.nluManager = nluManager;
		this.codecManager = codecManager;
		this.fileServices = fileServices;
		this.paramManager = paramManager;
		this.jsonEngine = jsonEngine;

		topicDefinitionMap = Collections.emptyMap();
		welcomeTourMap = Collections.emptyMap();
		mapAttachments = Collections.emptyMap();
	}

	@Override
	public void start() {
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
	public BotEngine createBotEngine() {
		return createBotEngine(UUID.randomUUID(), BlackBoardManager.MAIN_STORE_NAME);
	}

	@Override
	public BotEngine createBotEngine(final String storeName) {
		return createBotEngine(UUID.randomUUID(), storeName);
	}

	@Override
	public BotEngine createBotEngine(final UUID convId) {
		return createBotEngine(convId, BlackBoardManager.MAIN_STORE_NAME);
	}

	@Override
	public BotEngine createBotEngine(final UUID convId, final String storeName) {
		final var bb = blackBoardManager.connect(storeName, BBKey.of("/" + codecManager.getHexEncoder().encode(convId.toString().getBytes(StandardCharsets.UTF_8))));
		return new BotEngine(bb, topicDefinitionMap, behaviorTreeManager, nluManager);
	}

	@Override
	public synchronized void updateConfig(final Iterable<TopicDefinition> newTopics,
										  final StringBuilder logs) {
		final var nluTtrainingData = new HashMap<NluIntent, List<String>>();
		final Map<String, TopicDefinition> topicDefinitionTempMap = new HashMap<>();

		for (final TopicDefinition t : newTopics) {
			LogsUtils.addLogs(logs, t.getCode(), " mapping : ");
			if (!t.getTrainingPhrases().isEmpty() && !t.getUnreachable()) {
				LogsUtils.addLogs(logs, t.getTrainingPhrases());
				LogsUtils.breakLine(logs);
				nluTtrainingData.put(NluIntent.of(t.getCode()), t.getTrainingPhrases()); // build NLU training data
			}
			topicDefinitionTempMap.put(t.getCode(), t);
			LogsUtils.addLogs(logs, t.getCode(), " mapping ");
			LogsUtils.logOK(logs);
		}
		LogsUtils.addLogs(logs, "Rasa training mapping ");
		if (!generateTopicDefinitionMapHash(topicDefinitionMap).equals(generateTopicDefinitionMapHash(topicDefinitionTempMap))) {
			nluManager.train(nluTtrainingData, NluManager.DEFAULT_ENGINE_NAME); // the new NLU model is effectively running after this line
			LogsUtils.logOK(logs);
		} else {
			LogsUtils.addLogs(logs, "Topic definition map is the same as before, no nlu training necessary.");
		}
		topicDefinitionMap = Collections.unmodifiableMap(topicDefinitionTempMap);
	}

	@Override
	public void updateAttachments(final DtList<AttachmentExport> attachmentExports) {
		try {
			mapAttachments.forEach((key, value) -> fileServices.deleteAttachment(FileInfoURI.fromURN(value)));
			final HashMap<String, String> attachmentsMap = new HashMap<>();
			attachmentExports.forEach(attachmentExport -> {
				final StreamFile streamFile = StreamFile.of(attachmentExport.getFileName(), attachmentExport.getMimeType(),
						Instant.now(), attachmentExport.getLength(),
						() -> new ByteArrayInputStream((Base64.getDecoder().decode(attachmentExport.getFileData()))));

				final FileInfoURI fileInfoURI = fileServices.saveAttachment(streamFile);
				attachmentsMap.put(attachmentExport.getLabel(), fileInfoURI.toURN());
			});
			FileUtils.writeStringToFile(attachmentDataFile, jsonEngine.toJson(attachmentsMap), StandardCharsets.UTF_8);
			mapAttachments = attachmentsMap;
		} catch (final IOException e) {
			throw new VSystemException(e, "Error writing parameter file {0}", attachmentDataFile.getPath());
		}
	}

	private String generateTopicDefinitionMapHash(final Map<String, TopicDefinition> topicDefinitionMap) {
		final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);
		topicDefinitionMap.forEach((key, topicDefinition) -> {
			hashCodeBuilder.append(key);
			topicDefinition.getTrainingPhrases().forEach(hashCodeBuilder::append);
		});
		return Integer.toString(hashCodeBuilder.toHashCode());
	}

	@Override
	public VFile getAttachment(final String label) {
		final String urn = mapAttachments.get(label);
		if (urn == null) {
			throw new VSystemException("Attachment with label " + label + " doesn't exist...");
		}
		return fileServices.getFile(urn);
	}

	@Override
	public void stop() {

	}
}
