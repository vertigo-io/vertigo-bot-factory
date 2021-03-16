package io.vertigo.ai.nlu;

import io.vertigo.core.node.component.Manager;

/**
 * Natural Language Understanding manager.
 *
 * @author skerdudou
 */
public interface NluManager extends Manager {

	/**
	 * Register a new intent in the NLU model.
	 *
	 * @param code identifies the intent
	 * @param description optional, can describe the purpose of the intent
	 * @return the created intent
	 */
	VIntent registerIntent(String code, String description);

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
	void trainAll();

	/**
	 * Use the previously trained model to classify a new and unknown sentence. trainAll() must have been called at least once before.
	 *
	 * @param sentence the sentence we wants to classify.
	 * @return the result of the analysis
	 */
	VIntentResult recognize(String sentence);
}
