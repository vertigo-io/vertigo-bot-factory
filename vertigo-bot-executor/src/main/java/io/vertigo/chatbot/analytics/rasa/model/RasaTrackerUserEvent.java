/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.analytics.rasa.model;

import com.google.gson.annotations.SerializedName;

import io.vertigo.chatbot.analytics.rasa.model.nested.RasaTrackerParseData;

public class RasaTrackerUserEvent extends AbstractRasaTrackerEvent {

	private String text;

	@SerializedName("input_channel")
	private String inputChannel;

	@SerializedName("message_id")
	private String messageId;

	private String metadata;

	@SerializedName("parse_data")
	private RasaTrackerParseData parseData;

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * @return the inputChannel
	 */
	public String getInputChannel() {
		return inputChannel;
	}

	/**
	 * @param inputChannel the inputChannel to set
	 */
	public void setInputChannel(final String inputChannel) {
		this.inputChannel = inputChannel;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(final String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the metadata
	 */
	public String getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(final String metadata) {
		this.metadata = metadata;
	}

	/**
	 * @return the parseData
	 */
	public RasaTrackerParseData getParseData() {
		return parseData;
	}

	/**
	 * @param parseData the parseData to set
	 */
	public void setParseData(final RasaTrackerParseData parseData) {
		this.parseData = parseData;
	}
}
