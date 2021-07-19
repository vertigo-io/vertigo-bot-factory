package io.vertigo.chatbot.analytics;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.chatbot.engine.model.TopicDefinition;

public final class AnalyticsObjectSend {

	private TopicDefinition topic;

	private Double accuracy;

	private final List<TopicDefinition> topicsPast = new ArrayList<>();

	public TopicDefinition getTopic() {
		return topic;
	}

	public void setTopic(final TopicDefinition topic) {
		this.topic = topic;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(final Double accuracy) {
		this.accuracy = accuracy;
	}

	public List<TopicDefinition> getTopicsPast() {
		return topicsPast;
	}

	public AnalyticsObjectSend(final TopicDefinition topic, final Double accuracy) {
		super();
		this.topic = topic;
		this.accuracy = accuracy;
	}

}
