package io.vertigo.chatbot.designer.commons.ihm.enums;

public enum TimeEnum implements IHMEnum {

	DAY("TD_DAY", "DAY"),
	WEEK("TD_WEEK", "WEEK"),
	MONTH("TD_MONTH", "MONTH"),
	YEAR("TD_YEAR", "YEAR");

	private final String label;

	private final String value;

	TimeEnum(final String label, final String value) {
		this.label = label;
		this.value = value;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getValue() {
		return value;
	}
}
