package io.vertigo.chatbot.designer.commons.services;

import java.io.InputStream;

import javax.inject.Inject;

import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

public class AntivirusServices implements Component, Activeable {

	private ClamavClient clamavClient;

	@Inject
	private ParamManager paramManager;

	@Override
	public void start() {
		final String clamAvHost = paramManager.getParam("CLAMAV_HOST").getValueAsString();
		final int clamAvPort = paramManager.getOptionalParam("CLAMAV_PORT")
				.map(Param::getValueAsInt).orElse(3310);

		clamavClient = new ClamavClient(clamAvHost, clamAvPort);
	}

	public ScanResult checkForViruses(final InputStream inputStream) {
		return clamavClient.scan(inputStream);
	}

	@Override
	public void stop() {

	}
}
