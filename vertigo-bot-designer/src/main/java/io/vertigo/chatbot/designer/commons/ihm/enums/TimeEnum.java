package io.vertigo.chatbot.designer.commons.ihm.enums;

public enum TimeEnum implements IHMEnum {

	TD_DAY("TD_DAY", "DAY"),
	TD_WEEK("TD_WEEK", "WEEK"),
	TD_MONTH("TD_MONTH", "MONTH"),
	TD_YEAR("TD_YEAR", "YEAR");

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
