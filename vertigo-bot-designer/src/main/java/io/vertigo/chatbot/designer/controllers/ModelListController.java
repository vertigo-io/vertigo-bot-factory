package io.vertigo.chatbot.designer.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.designer.services.ExecutorBridgeServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/models")
public class ModelListController extends AbstractVSpringMvcController {

    @Inject
    private ExecutorBridgeServices exportServices;
    
    @GetMapping("/")
    public void initContext(final ViewContext viewContext) {
        toModeReadOnly();
    }

    @PostMapping("/_train")
	public ViewContext doSave(final ViewContext viewContext) {
    	exportServices.trainAgent();
    	
		return viewContext;
	}

}