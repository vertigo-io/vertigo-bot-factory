package io.vertigo.chatbot.designer.commons.services;

import java.util.Locale;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.commons.ihmEnum.IHMEnum;
import io.vertigo.chatbot.designer.domain.commons.SelectionOption;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.locale.MessageKey;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

public class EnumIHMManager implements Component {

	@Inject
	LocaleManager localeManager;

	public DtList<SelectionOption> getSelectionOptions(final IHMEnum[] values) {
		final Locale locale = localeManager.getCurrentLocale();
		final DtList<SelectionOption> result = new DtList<>(SelectionOption.class);

		for (final IHMEnum value : values) {
			final SelectionOption option = new SelectionOption();
			option.setLabel(localeManager.getMessage(new MessageKeyEnumImpl(value.getLabel()), locale));
			option.setValue(value.getValue());
			result.add(option);
		}

		return result;
	}

	private static class MessageKeyEnumImpl implements MessageKey {

		private static final long serialVersionUID = -537192677101694430L;
		private final String name;

		MessageKeyEnumImpl(final String name) {
			this.name = name;
		}

		/** {@inheritDoc} */
		@Override
		public String name() {
			return name;
		}

	}
}
