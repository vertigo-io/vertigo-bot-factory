package io.vertigo.chatbot.engine.plugins.bt.jira.model;

import java.util.ArrayList;
import java.util.List;

public class JsmServiceDeskSearchResult {

	private List<JsmServiceDesk> values;

	public List<JsmServiceDesk> getValues() {
		if (values == null) {
			values = new ArrayList<>();
		}
		return values;
	}

	public void setValues(final List<JsmServiceDesk> values) {
		this.values = values;
	}
}
