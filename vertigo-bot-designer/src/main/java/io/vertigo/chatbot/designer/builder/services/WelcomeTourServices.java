package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.WelcomeTourDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.WelcomeTour;
import io.vertigo.chatbot.commons.domain.WelcomeTourExport;
import io.vertigo.chatbot.commons.domain.WelcomeTourStep;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class WelcomeTourServices implements Component {

	@Inject
	private WelcomeTourDAO welcomeTourDAO;

	@Inject
	private FileServices fileServices;

	@Inject
	private WelcomeTourStepServices welcomeTourStepServices;

	public WelcomeTour findById(final long id)  {
		return welcomeTourDAO.get(id);
	}

	public WelcomeTour save (final WelcomeTour welcomeTour, final Optional<FileInfoURI> configFileUri) {
		List<WelcomeTourStep> steps = new ArrayList<>();
		if (configFileUri.isPresent()) {
			if (welcomeTour.getWelId() != null) {
				welcomeTourStepServices.deleteAllByTourId(welcomeTour.getWelId());
			}
			final VFile configFile = fileServices.getFileTmp(configFileUri.get());
			try (final InputStream inputStream = configFile.createInputStream()) {
				final String configString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				welcomeTour.setConfig(configString);
				steps = welcomeTourStepServices.readStepsFromConfigString(configString);
			} catch (final IOException e) {
				throw new VUserException("Couldn't read welcome tour config file", e);
			}
		}
		final WelcomeTour savedWelcomeTour = welcomeTourDAO.save(welcomeTour);
		steps.forEach(step -> {
			step.setTourId(savedWelcomeTour.getWelId());
			welcomeTourStepServices.save(step);
		});
		return savedWelcomeTour;
	}

	public void delete (final long id) {
		welcomeTourStepServices.deleteAllByTourId(id);
		welcomeTourDAO.delete(id);
	}

	public DtList<WelcomeTour> findAllByBotId(final long botId) {
		return welcomeTourDAO.findAll(Criterions.isEqualTo(DtDefinitions.WelcomeTourFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteAllByBotId(final long botId) {
		findAllByBotId(botId).forEach(welcomeTour -> delete(welcomeTour.getWelId()));
	}

	public DtList<WelcomeTourExport> exportBotWelcomeTours(final Chatbot bot, final StringBuilder logs) {
		LogsUtils.addLogs(logs, " Welcome tours export...");
		final DtList<WelcomeTourExport> welcomeTourExports = findAllByBotId(bot.getBotId()).stream().map(welcomeTour -> {
			final WelcomeTourExport welcomeTourExport = new WelcomeTourExport();
			welcomeTourExport.setTechnicalCode(welcomeTour.getTechnicalCode());
			welcomeTourExport.setLabel(welcomeTour.getLabel());
			welcomeTourExport.setConfig(welcomeTourStepServices.parseFile(welcomeTour.getConfig(), welcomeTour.getWelId()));
			return welcomeTourExport;
		}).collect(VCollectors.toDtList(WelcomeTourExport.class));
		LogsUtils.logOK(logs);
		return welcomeTourExports;
	}
}
