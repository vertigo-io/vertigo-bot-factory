package io.vertigo.chatbot.designer.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Tests de normalisation des sequences et de derivation des flèches de deplacement (FAQ et Shepherd).
 *
 * @author Chatbot Team
 */
class SequenceNormalizerTest {

	@Test
	void applyDenseSequences_rewritesDuplicatesAndGaps() {
		final List<Item> items = items(
				item(10L, 1L),
				item(20L, 1L),
				item(30L, 2L));

		assertTrue(SequenceNormalizer.applyDenseSequences(items, Item::sequence, Item::id, Item::setSequence));
		assertEquals(List.of(1L, 2L, 3L), sequences(items));
		assertEquals(List.of(10L, 20L, 30L), idsInSequenceOrder(items));
	}

	@Test
	void applyDenseSequences_isIdempotentWhenAlreadyDense() {
		final List<Item> items = items(
				item(1L, 1L),
				item(2L, 2L),
				item(3L, 3L));

		assertFalse(SequenceNormalizer.applyDenseSequences(items, Item::sequence, Item::id, Item::setSequence));
		assertEquals(List.of(1L, 2L, 3L), sequences(items));
	}

	@Test
	void applyDenseSequences_preservesRelativeOrderAfterSwap() {
		final List<Item> items = items(
				item(1L, 2L),
				item(2L, 1L),
				item(3L, 3L));

		SequenceNormalizer.applyDenseSequences(items, Item::sequence, Item::id, Item::setSequence);
		assertEquals(List.of(2L, 1L, 3L), idsInSequenceOrder(items));
		assertEquals(List.of(1L, 2L, 3L), sequences(items));
	}

	@Test
	void applyDenseSequences_handlesSingleAndEmpty() {
		assertFalse(SequenceNormalizer.applyDenseSequences(List.of(), Item::sequence, Item::id, Item::setSequence));

		final List<Item> single = items(item(99L, 7L));
		assertTrue(SequenceNormalizer.applyDenseSequences(single, Item::sequence, Item::id, Item::setSequence));
		assertEquals(List.of(1L), sequences(single));
	}

	@Test
	void canMove_matchesNeighborModelForDuplicates() {
		final List<Long> sequences = List.of(1L, 1L, 2L);

		assertFalse(SequenceNormalizer.canMoveUp(1L, sequences));
		assertTrue(SequenceNormalizer.canMoveDown(1L, sequences));
		assertTrue(SequenceNormalizer.canMoveUp(2L, sequences));
		assertFalse(SequenceNormalizer.canMoveDown(2L, sequences));
	}

	@Test
	void canMove_firstMiddleLastOnDenseList() {
		final List<Long> sequences = List.of(1L, 2L, 3L);

		assertFalse(SequenceNormalizer.canMoveUp(1L, sequences));
		assertTrue(SequenceNormalizer.canMoveDown(1L, sequences));
		assertTrue(SequenceNormalizer.canMoveUp(2L, sequences));
		assertTrue(SequenceNormalizer.canMoveDown(2L, sequences));
		assertTrue(SequenceNormalizer.canMoveUp(3L, sequences));
		assertFalse(SequenceNormalizer.canMoveDown(3L, sequences));
	}

	@Test
	void canMove_singleElementHasNoArrows() {
		final List<Long> sequences = List.of(1L);
		assertFalse(SequenceNormalizer.canMoveUp(1L, sequences));
		assertFalse(SequenceNormalizer.canMoveDown(1L, sequences));
	}

	private static Item item(final Long id, final Long sequence) {
		return new Item(id, sequence);
	}

	private static List<Item> items(final Item... values) {
		return new ArrayList<>(Arrays.asList(values));
	}

	private static List<Long> sequences(final List<Item> items) {
		return items.stream()
				.sorted(Comparator.comparingLong(left -> left.sequence))
				.map(Item::sequence)
				.collect(Collectors.toList());
	}

	private static List<Long> idsInSequenceOrder(final List<Item> items) {
		return items.stream()
				.sorted(Comparator.comparing(Item::sequence).thenComparing(Item::id))
				.map(Item::id)
				.collect(Collectors.toList());
	}

	private static final class Item {
		private final Long id;
		private Long sequence;

		private Item(final Long id, final Long sequence) {
			this.id = id;
			this.sequence = sequence;
		}

		private Long id() {
			return id;
		}

		private Long sequence() {
			return sequence;
		}

		private void setSequence(final Long sequence) {
			this.sequence = sequence;
		}
	}
}
