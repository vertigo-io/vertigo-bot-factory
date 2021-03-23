package io.vertigo.ai;

import io.vertigo.ai.impl.nlu.NluManagerImpl;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.plugins.nlu.rasa.RasaNluEnginePlugin;
import io.vertigo.core.node.config.Feature;
import io.vertigo.core.node.config.Features;
import io.vertigo.core.param.Param;

public class AiFeatures extends Features<AiFeatures> {

	/**
	 * Constructor.
	 */
	public AiFeatures() {
		super("ai");
	}

	/**
	 * Activates NLU.
	 *
	 * @return these features
	 */
	@Feature("nlu")
	public AiFeatures withNLU() {
		getModuleConfigBuilder()
				.addComponent(NluManager.class, NluManagerImpl.class);
		return this;
	}

	/**
	 * Activates NLU.
	 *
	 * @return these features
	 */
	@Feature("nlu.rasa")
	public AiFeatures withRasaNLU(final Param... params) {
		getModuleConfigBuilder()
				.addPlugin(RasaNluEnginePlugin.class, params);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	protected void buildFeatures() {
		//
	}
}
