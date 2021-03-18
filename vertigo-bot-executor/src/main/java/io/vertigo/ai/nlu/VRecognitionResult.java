package io.vertigo.ai.nlu;

import java.util.List;

import io.vertigo.core.lang.Assertion;

/**
 * Result of the analysis of a sentence by the NLU engine.
 *
 * @author skerdudou
 */
public class VRecognitionResult {

	private final String rawSentence;

	private final List<VIntentClassification> intentClassificationList;

	public VRecognitionResult(final String rawSentence, final List<VIntentClassification> intentClassificationList) {
		Assertion.check().isNotBlank(rawSentence);
		Assertion.check().isNotNull(intentClassificationList);
		//--
		this.rawSentence = rawSentence;
		this.intentClassificationList = intentClassificationList;
	}

	/**
	 * @return the rawSentence
	 */
	public String getRawSentence() {
		return rawSentence;
	}

	/**
	 * @return the intentClassificationList
	 */
	public List<VIntentClassification> getIntentClassificationList() {
		return intentClassificationList;
	}

}
