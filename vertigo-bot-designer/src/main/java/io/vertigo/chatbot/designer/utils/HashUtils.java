package io.vertigo.chatbot.designer.utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.datamodel.structure.model.DtList;

public class HashUtils {
	
	private HashUtils() {
		
	}
	
	public static String generateHashCodeForNluTrainingSentences(DtList<NluTrainingSentence> sentences) {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);
		sentences.stream().forEach(sentence -> {
			hashCodeBuilder.append(sentence.getText());
		});
		return Integer.toString(hashCodeBuilder.toHashCode());
	}
	
	public static String generateHashCodeForUtterTexts(DtList<UtterText> utterTexts) {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);
		utterTexts.stream().forEach(text -> {
			hashCodeBuilder.append(text.getText());
		});
		return Integer.toString(hashCodeBuilder.toHashCode());
	}
	
	public static String generateHashCodeForResponseButtons(DtList<ResponseButton> responseButtons) {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);
		responseButtons.stream().forEach(button -> {
			hashCodeBuilder.append(button.getTopIdResponse());
			hashCodeBuilder.append(button.getText());
		});
		return Integer.toString(hashCodeBuilder.toHashCode());
	}
	
	public static String generateHashCodeForSynonyms(DtList<Synonym> synonyms) {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);
		synonyms.stream().forEach(synonym -> {
			hashCodeBuilder.append(synonym.getLabel());
		});
		return Integer.toString(hashCodeBuilder.toHashCode());
	}

}
