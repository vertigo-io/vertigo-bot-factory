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
package io.vertigo.chatbot.designer.commons.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.security.VSecurityManager;
import io.vertigo.chatbot.designer.commons.DesignerUserSession;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.vega.webservice.stereotype.QueryParam;

@Controller
@RequestMapping("/commons/")
public class FileUploadController {

	@Inject
	private FileServices fileServices;

	@Inject
	private VSecurityManager securityManager;

	@GetMapping("/upload")
	public VFile getFile(@QueryParam("file") final FileInfoURI file) {
		if (!getUserSession().getTmpFiles().contains(file)) {
			return null;
		}
		return fileServices.getFileTmp(file);
	}

	@PostMapping("/upload")
	public FileInfoURI uploadFile(@QueryParam("file") final VFile file) {
		final FileInfoURI tmpUri = fileServices.saveFileTmp(file);
		getUserSession().getTmpFiles().add(tmpUri);
		return tmpUri;
	}

	@DeleteMapping("/upload")
	public FileInfoURI removeFile(@QueryParam("file") final FileInfoURI file) {
		if (!getUserSession().getTmpFiles().contains(file)) {
			return null;
		}
		fileServices.deleteFileTmp(file);
		return file; //if no return, you must get the response. Prefer to return old uri.
	}

	private DesignerUserSession getUserSession() {
		return securityManager.<DesignerUserSession> getCurrentUserSession().get();
	}
}
