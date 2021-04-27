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
/**
 *
 */
package io.vertigo.chatbot.designer.boot;

import io.vertigo.chatbot.commons.domain.topic.ResponseType;
import io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles;
import io.vertigo.chatbot.designer.domain.commons.PersonRole;
import io.vertigo.datastore.impl.entitystore.AbstractMasterDataDefinitionProvider;

/**
 * Init masterdata list.
 *
 * @author skerdudou
 */
public class ChatbotMasterDataDefinitionProvider extends AbstractMasterDataDefinitionProvider {

	@Override
	public void declareMasterDataLists() {
		registerDtMasterDatas(ResponseType.class);
		registerDtMasterDatas(PersonRole.class);
		registerDtMasterDatas(ChatbotProfiles.class);
	}

}
