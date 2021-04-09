package io.vertigo.chatbot.engine;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.vertigo.ai.impl.nlu.NluEnginePlugin;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;

public class MockNluEnginePlugin implements NluEnginePlugin {

	public MockNluEnginePlugin() {
		// mock
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void train(final Map<VIntent, List<String>> trainingData) {
		// Mock
	}

	/** {@inheritDoc} */
	@Override
	public VRecognitionResult recognize(final String sentence) {
		return new VRecognitionResult(sentence, Collections.emptyList());
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
