package io.vertigo.chatbot.engine.plugins.bt.jira.model;

import java.util.List;

public record JsmRequestTypeFieldsResult(List<Field> requestTypeFields) {

	public record Field(String fieldId, List<ValidValue> validValues) {}

	public record ValidValue(String value, String label) {}
}
