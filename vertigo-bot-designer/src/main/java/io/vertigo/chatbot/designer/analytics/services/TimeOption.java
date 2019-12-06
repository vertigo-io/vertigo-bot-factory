package io.vertigo.chatbot.designer.analytics.services;

public enum TimeOption {
	DAY("1d", "1h"),
	WEEK("7d", "1d"),
	MONTH("30d", "1d"),
	YEAR("52w", "1w");
	private final String range;
	private final String grain;

	private TimeOption(final String range, final String grain) {
		this.range = range;
		this.grain = grain;
	}

	/**
	 * @return the range
	 */
	public String getRange() {
		return range;
	}
	/**
	 * @return the grain
	 */
	public String getGrain() {
		return grain;
	}
}