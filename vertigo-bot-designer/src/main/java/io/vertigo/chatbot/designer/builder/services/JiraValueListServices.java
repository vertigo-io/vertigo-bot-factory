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
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS;
import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

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

	/**
	 * Recherche une liste par ID et vérifie qu'elle appartient au bot spécifié.
	 * @param id l'ID de la liste
	 * @param bot le bot propriétaire attendu
	 * @return la liste si elle appartient au bot
	 */
	public Optional<JiraValueList> findOptionalByIdAndBot(final long id, final Chatbot bot) {
		return jiraValueListDAO.findOptional(
				Criterions.isEqualTo(DtDefinitions.JiraValueListFields.jirValListId, id)
						.and(Criterions.isEqualTo(DtDefinitions.JiraValueListFields.botId, bot.getBotId())));
	}

	public DtList<JiraValueList> findAllByBotId(final Chatbot bot) {
		return jiraValueListDAO.findAll(
				Criterions.isEqualTo(DtDefinitions.JiraValueListFields.botId, bot.getBotId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public DtList<JiraValueListItem> findItems(final JiraValueList valueList) {
		return jiraValueListItemDAO.findAll(
				Criterions.isEqualTo(DtDefinitions.JiraValueListItemFields.jirValListId, valueList.getJirValListId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, DtDefinitions.JiraValueListItemFields.sequence.name(), false));
	}

	public DtList<JiraValueListItem> findAllItemsByBot(final Chatbot bot) {
		final DtList<JiraValueListItem> items = new DtList<>(JiraValueListItem.class);
		findAllByBotId(bot).forEach(valueList -> items.addAll(findItems(valueList)));
		return items;
	}

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
		if (sanitizedItems.isEmpty()) {
			throw new VUserException(LocaleMessageText.of(ExtensionsMultilingualResources.VALUE_LIST_ITEMS_REQUIRED));
		}
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

	public void deleteAllByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
		findAllByBotId(bot).forEach(valueList -> {
			deleteItems(valueList);
			jiraValueListDAO.delete(valueList.getJirValListId());
		});
	}

	private void deleteItems(final JiraValueList valueList) {
		findItems(valueList).forEach(item -> jiraValueListItemDAO.delete(item.getJirValItemId()));
	}

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
