package io.vertigo.chatbot.designer.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.ExecutorState;
import io.vertigo.chatbot.designer.services.ExecutorBridgeServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/models")
public class ModelListController extends AbstractVSpringMvcController {

	private static final ViewContextKey<ExecutorState> stateKey = ViewContextKey.of("state");
	
    @Inject
    private ExecutorBridgeServices executorBridgeServices;
    
    
    @GetMapping("/")
    public void initContext(final ViewContext viewContext) {
    	refreshState(viewContext);
    	
        toModeReadOnly();
    }
    

    @PostMapping("/_refresh")
	public ViewContext refreshState(final ViewContext viewContext) {
    	final ExecutorState state = executorBridgeServices.getState();
		viewContext.publishDto(stateKey, state);
    	
		return viewContext;
	}


    @PostMapping("/_train")
	public ViewContext doSave(final ViewContext viewContext) {
    	executorBridgeServices.trainAgent();
    	
		return viewContext;
	}

}