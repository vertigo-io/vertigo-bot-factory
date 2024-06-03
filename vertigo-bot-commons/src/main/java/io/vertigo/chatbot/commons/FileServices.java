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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.multilingual.AttachmentMultilingualResources;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import static io.vertigo.chatbot.commons.ChatbotUtils.MAX_UPLOAD_SIZE;

@Transactional
public class FileServices implements Component, Activeable {

	@Inject
	private ParamManager paramManager;

	@Inject
	private AntivirusServices antivirusServices;

	private String[] extensionsWhiteList;

	@Override
	public void start() {
		extensionsWhiteList = paramManager.getOptionalParam("EXTENSIONS_WHITELIST")
				.map(Param::getValueAsString).orElse("png,jpg,jpeg,pdf,csv,js").split(",");
	}

	@Override
	public void stop() {

	}

	public void checkFile (final String fileName, final Long fileLength, final InputStream inputStream) {
		final String[] extensionsTab = fileName.split(Pattern.quote("."));
		final Stream<String> extensionsWhiteListStream = Arrays.stream(extensionsWhiteList);
		if (extensionsTab.length != 2 || extensionsWhiteListStream.noneMatch(extensionsTab[1].toLowerCase()::contains)) {
			throw new VUserException(AttachmentMultilingualResources.EXTENSION_NOT_ALLOWED, extensionsTab[1],
					String.join(",", extensionsWhiteList));
		}
		if (fileLength > MAX_UPLOAD_SIZE) {
			throw new VUserException(AttachmentMultilingualResources.FILE_TOO_LARGE, MAX_UPLOAD_SIZE);
		}
        final ScanResult result = antivirusServices.checkForViruses(inputStream);
        if (result instanceof ScanResult.VirusFound) {
            final Map<String, Collection<String>> virusesMap = ((ScanResult.VirusFound) result).getFoundViruses();
            final String viruses = virusesMap.values().stream()
                    .flatMap(Collection::stream).collect(Collectors.joining(","));
            throw new VUserException(AttachmentMultilingualResources.VIRUSES_FOUND, viruses, fileName);
        }
    }
}
