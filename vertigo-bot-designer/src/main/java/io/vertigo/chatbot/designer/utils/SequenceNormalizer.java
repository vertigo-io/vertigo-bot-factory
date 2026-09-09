package io.vertigo.chatbot.designer.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Normalise des cles d'ordonnancement {@code sequence} en rangs denses 1..N.
 * <p>
 * {@code sequence} est une cle de tri, pas un index d'affichage. Cette classe
 * reconstruit des valeurs uniques et contigues tout en preservant l'ordre relatif
 * (sequence croissante, puis identifiant en cas d'egalite).
 * </p>
 *
 * @author Chatbot Team
 */
public final class SequenceNormalizer {

	private SequenceNormalizer() {
		// utility
	}

	/**
	 * Reecrit les sequences de {@code items} en 1..N dans l'ordre (sequence, id).
	 * Les sequences {@code null} sont placees en fin de liste.
	 *
	 * @param <T> type des elements
	 * @param items elements a normaliser (mutes en place)
	 * @param sequenceGetter lecture de la sequence courante
	 * @param idGetter lecture de l'identifiant (departage des egalites)
	 * @param sequenceSetter ecriture de la nouvelle sequence
	 * @return {@code true} si au moins une sequence a change
	 */
	public static <T> boolean applyDenseSequences(final List<T> items,
			final Function<T, Long> sequenceGetter,
			final Function<T, Long> idGetter,
			final BiConsumer<T, Long> sequenceSetter) {
		if (items == null || items.isEmpty()) {
			return false;
		}
		final List<T> sorted = new ArrayList<>(items);
		sorted.sort(Comparator
				.comparing((final T item) -> nullSafe(sequenceGetter.apply(item), Long.MAX_VALUE))
				.thenComparing((final T item) -> nullSafe(idGetter.apply(item), Long.MAX_VALUE)));
		boolean changed = false;
		long rank = 1L;
		for (final T item : sorted) {
			final Long current = sequenceGetter.apply(item);
			if (current == null || current.longValue() != rank) {
				sequenceSetter.accept(item, rank);
				changed = true;
			}
			rank++;
		}
		return changed;
	}

	/**
	 * Indique si une sequence possede un voisin inferieur dans la liste.
	 *
	 * @param sequence sequence de la ligne courante
	 * @param sequences sequences des elements du meme perimetre
	 * @return {@code true} s'il existe une sequence strictement plus petite
	 */
	public static boolean canMoveUp(final Long sequence, final List<Long> sequences) {
		if (sequence == null || sequences == null) {
			return false;
		}
		return sequences.stream().anyMatch(other -> other != null && other < sequence);
	}

	/**
	 * Indique si une sequence possede un voisin superieur dans la liste.
	 *
	 * @param sequence sequence de la ligne courante
	 * @param sequences sequences des elements du meme perimetre
	 * @return {@code true} s'il existe une sequence strictement plus grande
	 */
	public static boolean canMoveDown(final Long sequence, final List<Long> sequences) {
		if (sequence == null || sequences == null) {
			return false;
		}
		return sequences.stream().anyMatch(other -> other != null && other > sequence);
	}

	private static Long nullSafe(final Long value, final Long fallback) {
		return value == null ? fallback : value;
	}
}
