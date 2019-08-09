package io.vertigo.chatbot.factory.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.factory.domain.Intent;
import io.vertigo.chatbot.factory.services.ChatbotServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/intent")
public class IntentDetailController extends AbstractVSpringMvcController {

    private static final ViewContextKey<Intent> intentKey = ViewContextKey.of("intent");

    @Inject
    private ChatbotServices movieServices;

    @GetMapping("/{intId}")
    public void initContext(final ViewContext viewContext, @PathVariable("intId") final Long intId) {
        viewContext.publishDto(intentKey, movieServices.getIntentById(intId));
        toModeReadOnly();
    }

    @GetMapping("/new")
    public void initContext(final ViewContext viewContext) {
        viewContext.publishDto(intentKey, new Intent());
        toModeCreate();
    }

    @PostMapping("/_edit")
    public void doEdit() {
        toModeEdit();
    }

    @PostMapping("/_save")
    public String doSave(@ViewAttribute("intent") final Intent intent) {
        movieServices.save(intent);
        return "redirect:/intent/" + intent.getIntId();
    }

}