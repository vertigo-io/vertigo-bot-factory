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

/**
 * Manages global variables and their types for a chatbot.
 *
 * @author Chatbot Team
 */
@Controller
@RequestMapping("/bot/{botId}/globalVariable")
@Secured("Chatbot$botAdm")
public class GlobalVariableListController extends AbstractBotListEntityController<GlobalVariable> {

    private static final ViewContextKey<GlobalVariable> globalVariablesKey = ViewContextKey.of("globalVariables");
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

    /**
     * Initializes the bounded global-variable view.
     *
     * @param viewContext current view context
     * @param uiMessageStack UI message stack
     * @param botId bot identifier
     */
    @GetMapping("/")
    @Secured("BotUser")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        initCommonContext(viewContext, uiMessageStack, botId);
        viewContext.publishDtList(globalVariablesKey, globalVariableService.findGlobalVariablesForUi(botId, null));
        viewContext.publishDtList(globalVariableTypesKey, globalVariablesTypeService.finAllGlobalVariableTypesByBot(botId));
        viewContext.publishDto(newGlobalVariableKey, new GlobalVariable());
        viewContext.publishDto(newGlobalVariableTypeKey, new GlobalVariableType());
        viewContext.publishFileInfoURI(importGlobalVariablesFileUriKey, null);
        super.initBreadCrums(viewContext, GlobalVariable.class);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    /**
     * Saves a global variable. The displayed list is refreshed separately with
     * the active server-side filter.
     *
     * @param viewContext current view context
     * @param bot secured bot
     * @param globalVariable variable to save
     * @return updated view context
     */
    @PostMapping("/_saveGlobalVariable")
    public ViewContext saveGlobalVariable(final ViewContext viewContext,
                                             @ViewAttribute("bot") final Chatbot bot,
                                             @ViewAttribute("newGlobalVariable") @Validate(GlobalVariableNotEmptyValidator.class) final GlobalVariable globalVariable) {

        globalVariableService.saveGlobalVariable(bot, globalVariable);
        viewContext.publishDto(newGlobalVariableKey, new GlobalVariable());
        return viewContext;
    }

    /**
     * Deletes a global variable.
     *
     * @param viewContext current view context
     * @param bot secured bot
     * @param glvId variable identifier
     * @return updated view context
     */
    @PostMapping("/_deleteGlobalVariable")
    public ViewContext deleteGlobalVariable(final ViewContext viewContext,
                                            @ViewAttribute("bot") final Chatbot bot,
                                            @RequestParam("glvId") final Long glvId) {

        globalVariableService.deleteGlobalVariableById(bot, glvId);
        return viewContext;
    }

    /**
     * Refreshes the bounded variable list with an optional type filter.
     *
     * @param viewContext current view context
     * @param bot secured bot
     * @param gvtId optional type identifier
     * @return updated view context
     */
    @PostMapping("/_filterGlobalVariables")
    public ViewContext filterGlobalVariables(final ViewContext viewContext,
                                             @ViewAttribute("bot") final Chatbot bot,
                                             @RequestParam(value = "gvtId", required = false) final Long gvtId) {
        viewContext.publishDtList(globalVariablesKey, globalVariableService.findGlobalVariablesForUi(bot.getBotId(), gvtId));
        return viewContext;
    }

    /**
     * Exports all global variables without applying the UI limit.
     *
     * @param viewContext current view context
     * @param bot secured bot
     * @return generated export file
     */
    @PostMapping("/_exportGlobalVariables")
    public VFile doExportGlobalVariables(final ViewContext viewContext,
                                   @ViewAttribute("bot") final Chatbot bot) {
        return globalVariableExportService.exportGlobalVariables(bot, globalVariableService.findAllByTypeBotId(bot));
    }

    /**
     * Imports global variables from a CSV file.
     *
     * @param viewContext current view context
     * @param bot secured bot
     * @param importGlobalVariablesFile uploaded file
     * @return redirect to the variables page
     */
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

    /**
     * Saves a global-variable type.
     *
     * @param viewContext current view context
     * @param bot secured bot
     * @param globalVariableType type to save
     * @return updated view context
     */
    @PostMapping("/_saveGlobalVariableType")
    public ViewContext saveGlobalVariableType(final ViewContext viewContext,
                                          @ViewAttribute("bot") final Chatbot bot,
                                          @ViewAttribute("newGlobalVariableType") @Validate(GlobalVariableTypeNotEmptyValidator.class) final GlobalVariableType globalVariableType) {

        globalVariablesTypeService.saveGlobalVariableType(bot, globalVariableType);
        viewContext.publishDto(newGlobalVariableTypeKey, new GlobalVariableType());
        viewContext.publishDtList(globalVariableTypesKey, globalVariablesTypeService.finAllGlobalVariableTypesByBot(bot.getBotId()));
        return viewContext;
    }

    /**
     * Deletes a global-variable type and its variables.
     *
     * @param viewContext current view context
     * @param bot secured bot
     * @param gvtId type identifier
     * @return updated view context
     */
    @PostMapping("/_deleteGlobalVariableType")
    public ViewContext deleteGlobalVariableType(final ViewContext viewContext,
                                            @ViewAttribute("bot") final Chatbot bot,
                                            @RequestParam("gvtId") final Long gvtId) {

        globalVariablesTypeService.deleteGlobalVariableTypeById(bot, gvtId);
        viewContext.publishDtList(globalVariableTypesKey, globalVariablesTypeService.finAllGlobalVariableTypesByBot(bot.getBotId()));
        return viewContext;
    }

    /**
     * Validates required global-variable fields.
     */
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

    /**
     * Validates required global-variable-type fields.
     */
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
