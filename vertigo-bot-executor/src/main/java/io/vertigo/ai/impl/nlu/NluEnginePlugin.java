package io.vertigo.ai.impl.nlu;

import java.util.List;
import java.util.Map;

import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.core.node.component.Plugin;

/**
 * @author skerdudou
 */
public interface NluEnginePlugin extends Plugin {

	/**
	 * Start the process to train the neural network against registered intents.
	 */
	void train(final Map<VIntent, List<String>> trainingData);

	/**
	 * Use the previously trained model to classify a new and unknown sentence.
	 *
	 * @param sentence the sentence we wants to classify.
	 * @return the result of the analysis
	 */
	VRecognitionResult recognize(String sentence);

	/**
	 * Check if ready to recognize sentences.
	 *
	 * @return true if the engine is ready
	 */
	boolean isReady();

	/**
	 * Plugin name. Default to "main".
	 *
	 * @return the name of the plugin
	 */
	String getName();
}
