package io.vertigo.ai.nlu;

import java.util.List;
import java.util.Map;

import io.vertigo.core.node.component.Manager;

/**
 * Natural Language Understanding manager.
 *
 * @author skerdudou
 */
public interface NluManager extends Manager {

	/**
	 * Starts the process to train the default engine with provided data.
	 *
	 * @param trainingData all intents with their training phrases
	 */
	void train(Map<VIntent, List<String>> trainingData);

	/**
	 * Starts the process to train the specified engine with provided data.
	 *
	 * @param trainingData all intents with their training phrases
	 * @param engineName name of the engine to register with
	 */
	void train(Map<VIntent, List<String>> trainingData, String engineName);

	/**
	 * Use the previously trained model on the default engine to classify a new and unknown sentence.
	 *
	 * @param sentence the sentence we wants to classify.
	 * @return the result of the analysis
	 */
	VRecognitionResult recognize(String sentence);

	/**
	 * Use the previously trained model on the specified engine to classify a new and unknown sentence.
	 *
	 * @param sentence the sentence we wants to classify.
	 * @param engineName name of the engine to register with
	 * @return the result of the analysis
	 */
	VRecognitionResult recognize(String sentence, String engineName);

	/**
	 * Check if the default engine is ready to recognize sentences.
	 *
	 * @return true if the engine is ready
	 */
	boolean isReady();

	/**
	 * Check if the specified engine is ready to recognize sentences.
	 *
	 * @param engineName name of the engine to check
	 * @return true if the engine is ready
	 */
	boolean isReady(String engineName);
}
