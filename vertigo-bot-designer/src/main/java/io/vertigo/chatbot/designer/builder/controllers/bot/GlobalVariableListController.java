package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.GlobalVariableExportService;
import io.vertigo.chatbot.designer.builder.services.GlobalVariableService;
import io.vertigo.chatbot.designer.builder.services.GlobalVariablesTypeService;
import io.vertigo.chatbot.designer.domain.GlobalVariable;
import io.vertigo.chatbot.designer.domain.GlobalVariableType;
import io.vertigo.chatbot.designer.utils.AbstractChatbotDtObjectValidator;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datamodel.data.definitions.DataFieldName;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.List;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/globalVariable")
@Secured("Chatbot$botAdm")
public class GlobalVariableListController extends AbstractBotListEntityController<GlobalVariable> {

    private static final ViewContextKey<GlobalVariable> globalVariablesKey = ViewContextKey.of("globalVariables");
    private static final ViewContextKey<GlobalVariable> filteredGlobalVariablesKey = ViewContextKey.of("filteredGlobalVariables");
    private static final ViewContextKey<GlobalVariable> newGlobalVariableKey = ViewContextKey.of("newGlobalVariable");
    private static final ViewContextKey<GlobalVariableType> globalVariableTypesKey = ViewContextKey.of("globalVariableTypes");
    private static final ViewContextKey<GlobalVariableType> newGlobalVariableTypeKey = ViewContextKey.of("newGlobalVariableType");
    private static final ViewContextKey<FileInfoURI> importGlobalVariablesFileUriKey = ViewContextKey.of("importGlobalVariablesFileUri");

    @Inject
    private GlobalVariableService globalVariableService;

    @Inject
    private GlobalVariablesTypeService globalVariablesTypeService;

    @Inject
    private GlobalVariableExportService globalVariableExportService;

    @GetMapping("/")
    @Secured("BotUser")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        DtList<GlobalVariable> globalVariableDtList = globalVariableService.findAllGlobalVariablesByBotId(botId);
        viewContext.publishDtList(globalVariablesKey, globalVariableDtList);
        viewContext.publishDtList(filteredGlobalVariablesKey, globalVariableDtList);
        viewContext.publishDtList(globalVariableTypesKey, globalVariablesTypeService.finAllGlobalVariableTypesByBot(botId));
        viewContext.publishDto(newGlobalVariableKey, new GlobalVariable());
        viewContext.publishDto(newGlobalVariableTypeKey, new GlobalVariableType());
        viewContext.publishFileInfoURI(importGlobalVariablesFileUriKey, null);
        super.initBreadCrums(viewContext, GlobalVariable.class);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @PostMapping("/_saveGlobalVariable")
    public ViewContext saveGlobalVariable(final ViewContext viewContext,
                                             @ViewAttribute("bot") final Chatbot bot,
                                             @ViewAttribute("newGlobalVariable") @Validate(GlobalVariableNotEmptyValidator.class) final GlobalVariable globalVariable) {

        globalVariableService.saveGlobalVariable(bot, globalVariable);
        viewContext.publishDto(newGlobalVariableKey, new GlobalVariable());
        DtList<GlobalVariable> globalVariableDtList = globalVariableService.findAllGlobalVariablesByBotId(bot.getBotId());
        viewContext.publishDtList(globalVariablesKey, globalVariableDtList);
        viewContext.publishDtList(filteredGlobalVariablesKey, globalVariableDtList);
        return viewContext;
    }

    @PostMapping("/_deleteGlobalVariable")
    public ViewContext deleteGlobalVariable(final ViewContext viewContext,
                                            @ViewAttribute("bot") final Chatbot bot,
                                            @RequestParam("glvId") final Long glvId) {

        globalVariableService.deleteGlobalVariableById(bot, glvId);
        DtList<GlobalVariable> globalVariableDtList = globalVariableService.findAllGlobalVariablesByBotId(bot.getBotId());
        viewContext.publishDtList(globalVariablesKey, globalVariableDtList);
        viewContext.publishDtList(filteredGlobalVariablesKey, globalVariableDtList);
        return viewContext;
    }

    @PostMapping("/_exportGlobalVariables")
    public VFile doExportGlobalVariables(final ViewContext viewContext,
                                   @ViewAttribute("bot") final Chatbot bot) {
        return globalVariableExportService.exportGlobalVariables(bot, globalVariableService.findAllGlobalVariablesByBotId(bot.getBotId()));
    }

    @PostMapping("/_importGlobalVariables")
    public String doImportGlobalVariables(final ViewContext viewContext,
                                         @ViewAttribute("bot") final Chatbot bot,
                                         @ViewAttribute("importGlobalVariablesFileUri") final FileInfoURI importGlobalVariablesFile) {

        if (importGlobalVariablesFile == null) {
            throw new VUserException(UtilsMultilingualResources.IMPORT_FILE_MUST_NOT_BE_EMPTY);
        }
        globalVariableExportService.importGlobalVariablesFromCSV(bot, importGlobalVariablesFile);

        return "redirect:/bot/" + bot.getBotId() + "/globalVariable/";
    }

    @PostMapping("/_saveGlobalVariableType")
    public ViewContext saveGlobalVariableType(final ViewContext viewContext,
                                          @ViewAttribute("bot") final Chatbot bot,
                                          @ViewAttribute("newGlobalVariableType") @Validate(GlobalVariableTypeNotEmptyValidator.class) final GlobalVariableType globalVariableType) {

        globalVariablesTypeService.saveGlobalVariableType(bot, globalVariableType);
        viewContext.publishDto(newGlobalVariableTypeKey, new GlobalVariableType());
        viewContext.publishDtList(globalVariableTypesKey, globalVariablesTypeService.finAllGlobalVariableTypesByBot(bot.getBotId()));
        return viewContext;
    }

    @PostMapping("/_deleteGlobalVariableType")
    public ViewContext deleteGlobalVariableType(final ViewContext viewContext,
                                            @ViewAttribute("bot") final Chatbot bot,
                                            @RequestParam("gvtId") final Long gvtId) {

        globalVariablesTypeService.deleteGlobalVariableTypeById(bot, gvtId);
        DtList<GlobalVariable> globalVariableDtList = globalVariableService.findAllGlobalVariablesByBotId(bot.getBotId());
        viewContext.publishDtList(globalVariablesKey, globalVariableDtList);
        viewContext.publishDtList(filteredGlobalVariablesKey, globalVariableDtList);
        viewContext.publishDtList(globalVariableTypesKey, globalVariablesTypeService.finAllGlobalVariableTypesByBot(bot.getBotId()));
        return viewContext;
    }

    public static final class GlobalVariableNotEmptyValidator extends AbstractChatbotDtObjectValidator<GlobalVariable> {
        /**
         * {@inheritDoc}
         */
        @Override
        protected List<DataFieldName<GlobalVariable>> getFieldsToNullCheck() {
            return List.of(DtDefinitions.GlobalVariableFields.value,
                    DtDefinitions.GlobalVariableFields.param1,
                    DtDefinitions.GlobalVariableFields.botId,
                    DtDefinitions.GlobalVariableFields.gvtId);
        }
    }

    public static final class GlobalVariableTypeNotEmptyValidator extends AbstractChatbotDtObjectValidator<GlobalVariableType> {
        /**
         * {@inheritDoc}
         */
        @Override
        protected List<DataFieldName<GlobalVariableType>> getFieldsToNullCheck() {
            return List.of(DtDefinitions.GlobalVariableTypeFields.botId,
                    DtDefinitions.GlobalVariableTypeFields.label);
        }
    }
}
