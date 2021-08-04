package io.vertigo.chatbot.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.vertigo.ai.impl.nlu.NluEnginePlugin;
import io.vertigo.ai.nlu.NluIntent;
import io.vertigo.ai.nlu.NluResult;
import io.vertigo.ai.nlu.ScoredIntent;

public class MockNluEnginePlugin implements NluEnginePlugin {

	private Map<NluIntent, List<String>> trainingData;

	public MockNluEnginePlugin() {
		// mock
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void train(final Map<NluIntent, List<String>> trainingData, final StringBuilder logs) {
		this.trainingData = trainingData;
	}

	/** {@inheritDoc} */
	@Override
	public NluResult recognize(final String sentence) {
		final var intents = new ArrayList<ScoredIntent>();
		for (final Entry<NluIntent, List<String>> entry : trainingData.entrySet()) {
			if (entry.getValue().contains(sentence)) {
				intents.add(new ScoredIntent(entry.getKey(), 1.0));
				return new NluResult(sentence, intents);
			}
		}
		// if no direct match, result with all topic at equal accuracy
		for (final NluIntent intent : trainingData.keySet()) {
			intents.add(new ScoredIntent(intent, 1.0 / trainingData.size()));
		}
		return new NluResult(sentence, intents);
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return "main";
	}

	/** {@inheritDoc} */
	@Override
	public boolean isReady() {
		return true;
	}

}
