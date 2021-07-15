package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingExport;
import io.vertigo.chatbot.designer.dao.MeaningDAO;
import io.vertigo.chatbot.designer.domain.Meaning;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
public class MeaningServices implements Component {

	@Inject
	private MeaningDAO meaningDAO;

	@Inject
	private SynonymServices synonymServices;

	/**
	 * get Meaning by id
	 *
	 * @param id
	 * @return meaning
	 */
	public Meaning findmeaningById(final Long id) {
		return meaningDAO.get(id);
	}

	/**
	 * Save meaning
	 *
	 * @param meaning
	 * @return meaning
	 */
	public Meaning save(@SecuredOperation("botAdm") final Meaning meaning) {
		return meaningDAO.save(meaning);
	}

	/**
	 * Save meaning with synonyms
	 *
	 * @param meaning
	 * @param synonyms
	 * @param synonymsToDelete
	 * @return meaning
	 */
	public Meaning save(@SecuredOperation("botAdm") final Meaning meaning,
			final DtList<Synonym> synonyms,
			final DtList<Synonym> synonymsToDelete) {

		saveAllNotBlankSynonym(meaning, synonyms);
		synonymServices.removeSynonym(synonymsToDelete);

		return meaningDAO.save(meaning);
	}

	/**
	 * delete meaning
	 *
	 * @param bot
	 * @param meaId
	 */
	public void deleteMeaning(@SecuredOperation("botAdm") final Chatbot bot, final Long meaId) {
		meaningDAO.delete(meaId);
	}

	protected DtList<Synonym> saveAllNotBlankSynonym(final Meaning meaning, final DtList<Synonym> synonyms) {
		// save nlu textes
		final DtList<Synonym> synToSave = synonyms.stream()
				.filter(syn -> !StringUtil.isBlank(syn.getLabel()))
				.collect(VCollectors.toDtList(Synonym.class));

		for (final Synonym syn : synToSave) {
			syn.setMeaId(meaning.getMeaId());
			syn.setBotId(meaning.getBotId());
			synonymServices.save(syn);
		}

		return synToSave;
	}

	/**
	 * get meaning by label and botId
	 *
	 * @param label
	 * @param botId
	 * @return meaning
	 */
	public Meaning findMeaningByLabelAndBotId(final String label, final Long botId) {
		final Optional<Meaning> result = meaningDAO.getMeaningByLabelAndBotId(botId, label);
		return result.isPresent() ? result.get() : null;
	}

	/**
	 * Return sentences generated from original nlu training sentences, with all synonyms
	 *
	 * @param nluOriginal
	 * @param botId
	 * @return
	 */
	public ArrayList<String> generateSentenceWithSynonyms(final NluTrainingExport nluOriginal, final Long botId) {
		// Word are separated by space or ponctuation
		final String[] listWord = nluOriginal.getText().split("([.,!?:;'\"-]|\\s)+");

		// An analyze of each word to find meaning is made, and the results registered in a map
		final Map<Integer, ArrayList<Object>> mapMeaning = analyzeListWord(listWord, botId);

		final ArrayList<String> listText = new ArrayList<String>();
		listText.add(nluOriginal.getText());

		//Generation of all possible combinaison from the original sentences and the synonyms found
		return combine(mapMeaning, listText, mapMeaning.size() - 1);
	}

	/*
	 * Recursive method to generate nlu with synonyms from a list of sentence
	 * For a meaning, a sentence is generated from each sentence and each synonym,
	 * and then it is used to generate more sentences with the next group of synonyms.
	 *
	 * Example : original sentence T W1 T' W2 T''
	 * W1 has as a meaning M1, with synonyms S1 and S'1 (S1 == W1)
	 * W2 has as a meaning M2, with synonyms S2 and S'2 (S2 == W2)
	 * T, T' and T'' are strings with no meaning found
	 *
	 * There are 2 differents meaning in the sentence, so there will be two calls to the method combine.
	 * So first, the folowing sentence will be generated : 	T S1  T' W2 T''
	 * 														T S'1 T' W2 T''
	 *
	 * and then on the second loop, same work done but on the two sentences we just generated, and with the second meaning :
	 * 														T S1  T' S2  T''
	 * 														T S1  T' S'2 T''
	 * 														T S'1 T' S2  T''
	 * 														T S'1 T' S'2 T''
	 *
	 *
	 */
	private ArrayList<String> combine(final Map<Integer, ArrayList<Object>> mapMeaning, final ArrayList<String> listText, final int i) {

		// The loop is stopped when we hit the first portion
		if (i >= 0) {
			DtList<Synonym> listSynonyms = new DtList<>(Synonym.class);
			String word = null;

			if (!mapMeaning.get(i).isEmpty()) {
				final Meaning meaning = (Meaning) mapMeaning.get(i).get(0);
				word = (String) mapMeaning.get(i).get(1);

				if (meaning != null) {
					listSynonyms = synonymServices.getAllSynonymByMeaning(meaning);
				}
			}

			// self call to method combine, but to work on another word with synonyms
			final List<String> listEntry = combine(mapMeaning, listText, i - 1);

			// The size of the listEntry is saved because listEntry is modified in the loops
			final int size = listEntry.size();
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < listSynonyms.size(); k++) {
					Synonym synonym = null;
					if (k != listSynonyms.size()) {
						synonym = listSynonyms.get(k);
					}
					String entry = null;
					if (j != listEntry.size()) {
						entry = listEntry.get(j);
					}
					if (word != null && synonym != null && !word.equals(synonym.getLabel())) {
						// the sentence is generated with the word replaced by a synonym
						listText.add(populateExit(synonym, word, entry));
					}
				}
			}
		}
		return listText;
	}

	/**
	 * Modify entry by replacing occurence of a word with its synonyms
	 *
	 * @param synonym
	 * @param originalWord
	 * @param entry
	 * @return string
	 */
	private String populateExit(final Synonym synonym, final String originalWord, final String entry) {
		return entry.replaceAll(originalWord, synonym.getLabel());
	}

	/**
	 * Return a map with an index and a array with a word and the meaning associated
	 *
	 * @param listWord
	 * @param botId
	 * @return map
	 */
	private Map<Integer, ArrayList<Object>> analyzeListWord(final String[] listWord, final Long botId) {
		int key = 0;

		final Map<Integer, ArrayList<Object>> mapMeaning = new HashMap<>();

		Meaning meaning = null;

		String word = null;

		//loop on every word, to find if there are synonyms to a a word
		for (int i = 0; i < listWord.length; i++) {
			word = listWord[i];
			meaning = findMeaningByLabelAndBotId(word, botId);
			final ArrayList<Object> values = new ArrayList<Object>();
			//If a meaning is found, the word is registered with an index and the meaning associated
			if (meaning != null) {
				values.add(meaning);
				values.add(word);
				mapMeaning.put(key, values);
				key++;
			}
		}
		// If no meaning was found, the map is populated with an ampty array (the sentence can have no synonyms)
		if (meaning == null) {
			mapMeaning.put(key, new ArrayList<Object>());
		}
		return mapMeaning;
	}

}
