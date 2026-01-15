package io.vertigo.chatbot.engine.plugins.bt.jira.model;

import java.util.ArrayList;
import java.util.List;

public class JsmRequestTypeSearchResult {

	private List<JsmRequestType> values;

	public List<JsmRequestType> getValues() {
		if (values == null) {
			values = new ArrayList<>();
		}
		return values;
	}

	public void setValues(final List<JsmRequestType> values) {
		this.values = values;
	}
}
