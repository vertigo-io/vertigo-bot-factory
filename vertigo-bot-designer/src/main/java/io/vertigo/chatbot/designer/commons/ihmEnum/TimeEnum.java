package io.vertigo.chatbot.designer.commons.ihmEnum;

public enum TimeEnum implements IHMEnum {

	DAY("TD_DAY", "DAY"),
	WEEK("TD_WEEK", "WEEK"),
	MONTH("TD_MONTH", "MONTH"),
	YEAR("TD_YEAR", "YEAR");

	private String label = "";

	private String value = "";

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
