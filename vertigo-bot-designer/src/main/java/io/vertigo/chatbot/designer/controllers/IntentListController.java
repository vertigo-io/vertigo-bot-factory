package io.vertigo.chatbot.designer.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.designer.services.ChatbotServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/intents")
public class IntentListController extends AbstractVSpringMvcController {

    private static final ViewContextKey<Intent> intentsKey = ViewContextKey.of("intents");

    @Inject
    private ChatbotServices chatbotServices;

    @GetMapping("/")
    public void initContext(final ViewContext viewContext) {
        viewContext.publishDtList(intentsKey, chatbotServices.getAllIntents());
        toModeReadOnly();
    }


}