package io.vertigo.chatbot.designer.commons.ihmEnum;

public enum TimeEnum implements IHMEnum {

	DAY("TD_DAY", "DAY"),
	WEEK("TD_WEEK", "WEEK"),
	MONTH("TD_MONTH", "MONTH"),
	YEAR("TD_YEAR", "YEAR");

	public String label = "";

	public String value = "";

	private TimeEnum(final String label, final String value) {
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
