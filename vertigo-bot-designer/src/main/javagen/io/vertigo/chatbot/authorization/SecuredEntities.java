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
package io.vertigo.chatbot.authorization;

import io.vertigo.account.authorization.metamodel.Authorization;
import io.vertigo.account.authorization.metamodel.AuthorizationName;
import io.vertigo.account.authorization.metamodel.OperationName;
import io.vertigo.app.Home;
import io.vertigo.chatbot.commons.domain.Chatbot;

/**
 * Warning. This class is generated automatically !
 *
 * Enum of the security authorizations and operations associated with secured entities known by the application.
 */
public final class SecuredEntities {

	private SecuredEntities() {
		//private constructor
	}

	/**
	 * Authorizations of Chatbot.
	 */
	public enum ChatbotAuthorizations implements AuthorizationName {
		/** Visibilité si affecté à l'utilisateur'.. */
		AtzChatbot$read,
		/** Visibilité globale pour le super admin. Sans régles = toujours ok.. */
		AtzChatbot$admin,
		/** Modification si affecté à l'utilisateur.. */
		AtzChatbot$write;

		/**
		 * Get the associated authorization.
		 *
		 * @param code authorization code
		 * @return authorization
		 */
		public static Authorization of(final String code) {
			return Home.getApp().getDefinitionSpace().resolve(code, Authorization.class);
		}

		/**
		 * Get the associated authorization.
		 *
		 * @return role
		 */
		public Authorization getAuthorization() {
			return of(name());
		}
	}

	/**
	 * Operations of Chatbot.
	 */
	public enum ChatbotOperations implements OperationName<Chatbot> {
		/** Visibilité si affecté à l'utilisateur'.. */
		read,
		/** Visibilité globale pour le super admin. Sans régles = toujours ok.. */
		admin,
		/** Modification si affecté à l'utilisateur.. */
		write;
	}
}
