package io.vertigo.chatbot.executor.webservices;

import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import javax.inject.Inject;
import java.io.InputStream;

public class AntivirusServices implements Component, Activeable {

	private ClamavClient clamavClient;

	@Inject
	private ParamManager paramManager;

	@Override
	public void start() {
		String clamAvHost = paramManager.getParam("CLAMAV_HOST").getValueAsString();
		int clamAvPort = paramManager.getOptionalParam("CLAMAV_PORT")
				.orElse(Param.of("CLAMAV_PORT", "3310")).getValueAsInt();

		this.clamavClient = new ClamavClient(clamAvHost, clamAvPort);
	}

	public ScanResult checkForViruses(InputStream inputStream) {
		return clamavClient.scan(inputStream);
	}

	@Override
	public void stop() {

	}
}
