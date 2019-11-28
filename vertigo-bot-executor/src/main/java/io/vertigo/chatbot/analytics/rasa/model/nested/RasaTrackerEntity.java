package io.vertigo.chatbot.analytics.rasa.model.nested;

public class RasaTrackerEntity {

	private String entity;
	private Integer start;
	private Integer end;
	private String value;

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(final String entity) {
		this.entity = entity;
	}
	/**
	 * @return the start
	 */
	public Integer getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(final Integer start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public Integer getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(final Integer end) {
		this.end = end;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

}
