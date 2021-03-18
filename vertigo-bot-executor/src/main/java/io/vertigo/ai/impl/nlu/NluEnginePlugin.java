package io.vertigo.ai.impl.nlu;

import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.core.node.component.Plugin;

/**
 * @author skerdudou
 */
public interface NluEnginePlugin extends Plugin {

	/**
	 * Register a new intent.
	 *
	 * @param intent the intent to register
	 */
	void registerIntent(VIntent intent);

	/**
	 * Add a new sentence to the training corpus associated with the intent. It is used by the NLU engine to train the neural network to classify sentences.
	 *
	 * @param intent the resolving intent
	 * @param trainingPhrase example phrase that we associate with the resolving intent
	 */
	void addTrainingPhrase(VIntent intent, String trainingPhrase);

	/**
	 * Start the process to train the neural network against registered intents.
	 */
	void train();

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
