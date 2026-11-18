package io.vertigo.chatbot.designer.builder.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.JiraCustomFieldSettingDAO;
import io.vertigo.chatbot.commons.dao.JiraValueListDAO;
import io.vertigo.chatbot.commons.dao.JiraValueListItemDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.JiraValueList;
import io.vertigo.chatbot.commons.domain.JiraValueListExport;
import io.vertigo.chatbot.commons.domain.JiraValueListItem;
import io.vertigo.chatbot.commons.domain.JiraValueListItemExport;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS;
import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

/**
 * Listes de valeurs d'un chatbot, utilisées pour fermer la saisie d'un champ Jira.
 * Le code de la liste est repris dans le script. Le libellé est proposé à l'utilisateur, le code de l'entrée est envoyé à JSM.
 */
@Transactional
@Secured("BotUser")
public class JiraValueListServices implements Component {

	/** Taille des colonnes code et label (domaines DoCode et DoLabel en VARCHAR(100)). */
	private static final int MAX_FIELD_LENGTH = 100;

	@Inject
	private JiraValueListDAO jiraValueListDAO;

	@Inject
	private JiraValueListItemDAO jiraValueListItemDAO;

	@Inject
	private JiraCustomFieldSettingDAO jiraCustomFieldSettingDAO;

	@Inject
	private DesignerFileServices designerFileServices;


	 /** Recherche une liste par ID et par bot **/
	public Optional<JiraValueList> findOptionalByIdAndBot(final long id, final Chatbot bot) {
		return jiraValueListDAO.findOptional(
				Criterions.isEqualTo(DtDefinitions.JiraValueListFields.jirValListId, id)
						.and(Criterions.isEqualTo(DtDefinitions.JiraValueListFields.botId, bot.getBotId())));
	}

	/** Listes définies pour ce chatbot. */
	public DtList<JiraValueList> findAllByBotId(final Chatbot bot) {
		return jiraValueListDAO.findAll(
				Criterions.isEqualTo(DtDefinitions.JiraValueListFields.botId, bot.getBotId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	/** Entrées d'une liste, triées dans l'ordre d'affichage. */
	public DtList<JiraValueListItem> findItems(final JiraValueList valueList) {
		return jiraValueListItemDAO.findAll(
				Criterions.isEqualTo(DtDefinitions.JiraValueListItemFields.jirValListId, valueList.getJirValListId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, DtDefinitions.JiraValueListItemFields.sequence.name(), false));
	}

	/** Toutes les entrées du chatbot. Sert à compter les valeurs de chaque liste. */
	public DtList<JiraValueListItem> findAllItemsByBot(final Chatbot bot) {
		final DtList<JiraValueListItem> items = new DtList<>(JiraValueListItem.class);
		findAllByBotId(bot).forEach(valueList -> items.addAll(findItems(valueList)));
		return items;
	}

	/**
	 * Enregistre la liste .
	 * Le code de liste est unique pour le chatbot, le code de chaque entrée est unique dans la liste.
	 */
	public JiraValueList save(@SecuredOperation("botAdm") final Chatbot bot, final JiraValueList valueList, final List<JiraValueListItem> items) {
		if (valueList.getJirValListId() != null) {
			findOptionalByIdAndBot(valueList.getJirValListId(), bot)
					.orElseThrow(() -> new SecurityException("Access denied: value list does not belong to this bot"));
		}
		valueList.setBotId(bot.getBotId());
		valueList.setCode(valueList.getCode().trim());
		valueList.setLabel(valueList.getLabel().trim());
		checkLength(valueList.getCode());
		checkLength(valueList.getLabel());
		final boolean duplicateCode = jiraValueListDAO.findOptional(
				Criterions.isEqualTo(DtDefinitions.JiraValueListFields.code, valueList.getCode())
						.and(Criterions.isEqualTo(DtDefinitions.JiraValueListFields.botId, bot.getBotId())))
				.filter(existing -> !existing.getJirValListId().equals(valueList.getJirValListId()))
				.isPresent();
		if (duplicateCode) {
			throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_CODE_ALREADY_EXISTS));
		}
		final List<JiraValueListItem> sanitizedItems = sanitizeItems(items);
		if (sanitizedItems.size() > MAX_ELEMENTS) {
			throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_TOO_MANY_ITEMS, MAX_ELEMENTS));
		}
		final JiraValueList saved = jiraValueListDAO.save(valueList);
		deleteItems(saved);
		long sequence = 1L;
		for (final JiraValueListItem item : sanitizedItems) {
			item.setJirValItemId(null);
			item.setJirValListId(saved.getJirValListId());
			item.setSequence(sequence++);
			jiraValueListItemDAO.save(item);
		}
		return saved;
	}

	/** Ajoute ou modifie une entrée d'une liste existante. Le code de l'entrée reste unique dans la liste. */
	public JiraValueListItem saveItem(@SecuredOperation("botAdm") final Chatbot bot, final long jirValListId, final JiraValueListItem item) {
		final JiraValueList valueList = findOptionalByIdAndBot(jirValListId, bot)
				.orElseThrow(() -> new SecurityException("Access denied: value list does not belong to this bot"));
		final String code = item.getCode() == null ? "" : item.getCode().trim();
		final String label = item.getLabel() == null ? "" : item.getLabel().trim();
		if (code.isEmpty() || label.isEmpty()) {
			throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
		}
		checkLength(code);
		checkLength(label);
		final DtList<JiraValueListItem> existingItems = findItems(valueList);
		final boolean isNew = item.getJirValItemId() == null;
		if (!isNew && existingItems.stream().noneMatch(existing -> existing.getJirValItemId().equals(item.getJirValItemId()))) {
			throw new SecurityException("Access denied: value list item does not belong to this value list");
		}
		final boolean duplicateCode = existingItems.stream()
				.anyMatch(existing -> existing.getCode().equals(code) && !existing.getJirValItemId().equals(item.getJirValItemId()));
		if (duplicateCode) {
			throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_ITEM_CODE_DUPLICATE));
		}
		if (isNew && existingItems.size() >= MAX_ELEMENTS) {
			throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_TOO_MANY_ITEMS, MAX_ELEMENTS));
		}
		item.setJirValListId(valueList.getJirValListId());
		item.setCode(code);
		item.setLabel(label);
		if (isNew) {
			item.setSequence(existingItems.stream().mapToLong(JiraValueListItem::getSequence).max().orElse(0L) + 1);
		} else {
			existingItems.stream()
					.filter(existing -> existing.getJirValItemId().equals(item.getJirValItemId()))
					.findFirst()
					.ifPresent(existing -> item.setSequence(existing.getSequence()));
		}
		return jiraValueListItemDAO.save(item);
	}

	/** Remplace les entrées d'une liste existante par celles d'un CSV et l'enregistre. */
	public void importItems(@SecuredOperation("botAdm") final Chatbot bot, final long jirValListId, final FileInfoURI importFileUri) {
		final JiraValueList valueList = findOptionalByIdAndBot(jirValListId, bot)
				.orElseThrow(() -> new SecurityException("Access denied: value list does not belong to this bot"));
		final String[] columns = new String[] {
				DtDefinitions.JiraValueListItemFields.code.name(),
				DtDefinitions.JiraValueListItemFields.label.name(),
		};
		save(bot, valueList, designerFileServices.readCsvFile(JiraValueListItem.class, designerFileServices.getFileTmp(importFileUri), columns));
	}

	/** Supprime une entrée d'une liste. */
	public void deleteItem(@SecuredOperation("botAdm") final Chatbot bot, final long jirValListId, final long jirValItemId) {
		final JiraValueList valueList = findOptionalByIdAndBot(jirValListId, bot)
				.orElseThrow(() -> new SecurityException("Access denied: value list does not belong to this bot"));
		final DtList<JiraValueListItem> existingItems = findItems(valueList);
		if (existingItems.stream().noneMatch(existing -> existing.getJirValItemId().equals(jirValItemId))) {
			throw new SecurityException("Access denied: value list item does not belong to this value list");
		}
		jiraValueListItemDAO.delete(jirValItemId);
	}

	/** Supprime la liste et ses entrées, sauf si un champ personnalisé la référence encore. */
	public void delete(@SecuredOperation("botAdm") final Chatbot bot, final long id) {
		findOptionalByIdAndBot(id, bot).ifPresent(valueList -> {
			final boolean used = !jiraCustomFieldSettingDAO.findAll(
					Criterions.isEqualTo(DtDefinitions.JiraCustomFieldSettingFields.jirValListId, valueList.getJirValListId()),
					DtListState.of(1)).isEmpty();
			if (used) {
				throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_IN_USE));
			}
			deleteItems(valueList);
			jiraValueListDAO.delete(id);
		});
	}

	/** Supprime les listes du chatbot. Les champs personnalisés, qui portent la clé étrangère, doivent déjà être supprimés. */
	public void deleteAllByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
		findAllByBotId(bot).forEach(valueList -> {
			deleteItems(valueList);
			jiraValueListDAO.delete(valueList.getJirValListId());
		});
	}

	private void deleteItems(final JiraValueList valueList) {
		findItems(valueList).forEach(item -> jiraValueListItemDAO.delete(item.getJirValItemId()));
	}

	/** Modèle embarqué à l'entraînement : code, libellé et entrées, sans les identifiants techniques. */
	public DtList<JiraValueListExport> exportValueLists(final Chatbot bot) {
		return findAllByBotId(bot).stream().map(valueList -> {
			final JiraValueListExport export = new JiraValueListExport();
			export.setCode(valueList.getCode());
			export.setLabel(valueList.getLabel());
			final DtList<JiraValueListItemExport> itemExports = findItems(valueList).stream().map(item -> {
				final JiraValueListItemExport itemExport = new JiraValueListItemExport();
				itemExport.setCode(item.getCode());
				itemExport.setLabel(item.getLabel());
				return itemExport;
			}).collect(VCollectors.toDtList(JiraValueListItemExport.class));
			export.setItems(itemExports);
			return export;
		}).collect(VCollectors.toDtList(JiraValueListExport.class));
	}

	/** Écarte les lignes vides, refuse une saisie incomplète et les codes en double. */
	private List<JiraValueListItem> sanitizeItems(final List<JiraValueListItem> items) {
		final List<JiraValueListItem> sanitized = new ArrayList<>();
		final Set<String> codes = new HashSet<>();
		if (items == null) {
			return sanitized;
		}
		for (final JiraValueListItem item : items) {
			final String code = item.getCode() == null ? "" : item.getCode().trim();
			final String label = item.getLabel() == null ? "" : item.getLabel().trim();
			if (code.isEmpty() && label.isEmpty()) {
				continue;
			}
			if (code.isEmpty() || label.isEmpty()) {
				throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
			}
			checkLength(code);
			checkLength(label);
			if (!codes.add(code)) {
				throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_ITEM_CODE_DUPLICATE));
			}
			item.setCode(code);
			item.setLabel(label);
			sanitized.add(item);
		}
		return sanitized;
	}

	private static void checkLength(final String value) {
		if (value.length() > MAX_FIELD_LENGTH) {
			throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_FIELD_TOO_LONG, MAX_FIELD_LENGTH));
		}
	}
}
