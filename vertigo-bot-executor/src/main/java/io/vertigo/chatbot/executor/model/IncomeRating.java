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
package io.vertigo.chatbot.executor.model;

import java.util.UUID;

public final class IncomeRating {
	private UUID convUUID;
	private Integer note;
	private String comment;
	private Boolean isSatisfied;

	/**
	 * @return the convUUID
	 */
	public UUID getConvUUID() {
		return convUUID;
	}

	/**
	 * @param convUUID the convUUID to set
	 */
	public void setConvUUID(final UUID convUUID) {
		this.convUUID = convUUID;
	}

	/**
	 * @return the note
	 */
	public Integer getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(final Integer note) {
		this.note = note;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * @return the isSatisfied
	 */
	public Boolean getIsSatisfied() {
		return isSatisfied;
	}

	/**
	 * @param isSatisfied the isSatisfied to set
	 */
	public void setIsSatisfied(final Boolean isSatisfied) {
		this.isSatisfied = isSatisfied;
	}

}
