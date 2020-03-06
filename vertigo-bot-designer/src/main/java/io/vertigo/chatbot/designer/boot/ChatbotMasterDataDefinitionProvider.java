/**
 *
 */
package io.vertigo.chatbot.designer.boot;

import io.vertigo.chatbot.commons.domain.ResponseType;
import io.vertigo.dynamo.impl.store.datastore.AbstractMasterDataDefinitionProvider;

/**
 * Init masterdata list.
 * 
 * @author skerdudou
 */
public class ChatbotMasterDataDefinitionProvider extends AbstractMasterDataDefinitionProvider {

	@Override
	public void declareMasterDataLists() {
		registerDtMasterDatas(ResponseType.class);
	}

}
