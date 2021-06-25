package io.vertigo.chatbot.designer.commons.services;

import io.vertigo.chatbot.designer.commons.ihmEnum.IHMEnum;
import io.vertigo.chatbot.designer.domain.commons.SelectionOption;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

public class EnumIHMManager implements Component {

	public DtList<SelectionOption> getSelectionOptions(final IHMEnum[] values) {
		final DtList<SelectionOption> result = new DtList<>(SelectionOption.class);

		for (final IHMEnum value : values) {
			final SelectionOption option = new SelectionOption();
			option.setLabel(MessageText.of(value.getLabel()).getDisplay());
			option.setValue(value.getValue());
			result.add(option);
		}

		return result;
	}

}
