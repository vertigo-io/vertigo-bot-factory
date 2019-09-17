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
import io.vertigo.dynamo.domain.model.FileInfoURI;
import io.vertigo.dynamo.file.model.VFile;
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
