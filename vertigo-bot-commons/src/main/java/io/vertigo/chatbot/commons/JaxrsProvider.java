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
package io.vertigo.chatbot.commons;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.multipart.internal.MultiPartReaderServerSide;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import io.vertigo.core.node.component.Component;

public class JaxrsProvider implements Component {

	public WebTarget getWebTarget(final String targetUrl) {
		return ClientBuilder.newClient()
				.target(targetUrl)
				.register(GsonProvider.class)
				.register(MultiPartReaderServerSide.class)
				.register(MultiPartWriter.class);
	}
}
