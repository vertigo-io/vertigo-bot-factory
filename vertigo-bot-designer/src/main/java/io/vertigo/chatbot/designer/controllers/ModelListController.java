package io.vertigo.chatbot.designer.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

import io.vertigo.chatbot.designer.domain.IntentExport;
import io.vertigo.chatbot.designer.services.ExportServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Controller
@RequestMapping("/models")
public class ModelListController extends AbstractVSpringMvcController {

    @Inject
    private ExportServices exportServices;
    
    @Inject
    private JsonEngine jsonEngine;

    @GetMapping("/")
    public void initContext(final ViewContext viewContext) {
        toModeReadOnly();
    }

    @PostMapping("/_train")
	public ViewContext doSave(final ViewContext viewContext) {
    	DtList<IntentExport> test = exportServices.exportSmallTalk();
    	System.out.println(jsonEngine.toJson(test));
		return viewContext;
	}

}