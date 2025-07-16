package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.GlobalVariableExport;
import io.vertigo.chatbot.commons.multilingual.globalVariables.GlobalVariablesMultilingualResources;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.designer.domain.GlobalVariable;
import io.vertigo.chatbot.designer.domain.GlobalVariableType;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class GlobalVariableExportService implements Component {

    @Inject
    private GlobalVariableService globalVariableService;

    @Inject
    private GlobalVariablesTypeService globalVariablesTypeService;

    @Inject
    ExporterManager exportManager;

    @Inject
    DesignerFileServices designerFileServices;

    @Inject
    private JsonEngine jsonEngine;

    public VFile exportGlobalVariables(@SecuredOperation("botVisitor") final Chatbot bot, DtList<GlobalVariable> globalVariableDtList) {

        final DtList<GlobalVariableExport> globalVariableExportDtList = mapToGlobalVariableExport(globalVariableDtList);
        final String exportName = LocaleMessageText.of(GlobalVariablesMultilingualResources.GLOBAL_VARIABLES_EXPORT_FILENAME, bot.getName()).getDisplay();
        final Export export = new ExportBuilder(ExportFormat.CSV, exportName)
                .beginSheet(globalVariableExportDtList, null)
                .addField(DtDefinitions.GlobalVariableExportFields.type)
                .addField(DtDefinitions.GlobalVariableExportFields.param1)
                .addField(DtDefinitions.GlobalVariableExportFields.param2)
                .addField(DtDefinitions.GlobalVariableExportFields.param3)
                .addField(DtDefinitions.GlobalVariableExportFields.param4)
                .addField(DtDefinitions.GlobalVariableExportFields.value)
                .endSheet()
                .build();
        return exportManager.createExportFile(export);
    }

    public String exportGlobalVariables(@SecuredOperation("botAdm") final Chatbot bot, final StringBuilder logs) {
        LogsUtils.addLogs(logs, "Export documentary resources : ");
        String export = jsonEngine.toJson(mapToGlobalVariableExport(globalVariableService.findAllByTypeBotId(bot)));
        LogsUtils.logKO(logs);
        return export;
    }

    public DtList<GlobalVariableExport> mapToGlobalVariableExport(DtList<GlobalVariable> globalVariableDtList) {
        return globalVariableDtList.stream().map(globalVariable -> {
            globalVariable.globalVariableType().load();
            final GlobalVariableExport globalVariableExport = new GlobalVariableExport();
            globalVariableExport.setId(globalVariable.getGlvId());
            globalVariableExport.setType(globalVariable.globalVariableType().get().getLabel());
            globalVariableExport.setParam1(globalVariable.getParam1());
            globalVariableExport.setParam2(globalVariable.getParam2());
            globalVariableExport.setParam3(globalVariable.getParam3());
            globalVariableExport.setParam4(globalVariable.getParam4());
            globalVariableExport.setValue(globalVariable.getValue());
            return globalVariableExport;
        }).collect(VCollectors.toDtList(GlobalVariableExport.class));
    }

    private Map<String, Long> globalVariableTypes(final Chatbot chatbot) {
        DtList<GlobalVariableType> globalVariableTypes = globalVariablesTypeService.finAllGlobalVariableTypesByBot(chatbot.getBotId());
        final Map<String, Long> globalVariableTypesMap = new HashMap<>();
        for (final GlobalVariableType globalVariableType : globalVariableTypes) {
            globalVariableTypesMap.put(globalVariableType.getLabel(), globalVariableType.getGvtId());
        }
        return globalVariableTypesMap;
    }

    public void importGlobalVariablesFromCSV(final Chatbot bot, final FileInfoURI importGlobalVariablesFile) {
        globalVariableService.deleteAllByBotId(bot);
        Map<String, Long> globalVariableTypesMap = globalVariableTypes(bot);
        transformFileToList(designerFileServices.getFileTmp(importGlobalVariablesFile))
                .forEach(globalVariableExport -> generateGlobalVariableFromExport(bot, globalVariableExport, globalVariableTypesMap));
    }

    private void generateGlobalVariableFromExport(final Chatbot bot, GlobalVariableExport globalVariableExport, Map<String, Long> globalVariableTypesMap) {
        if (globalVariableExport.getType() == null || globalVariableExport.getType().isBlank()) {
            throw new VUserException(GlobalVariablesMultilingualResources.GLOBAL_VARIABLES_EXPORT_TYPE_ERROR);
        }
        if (globalVariableExport.getParam1() == null || globalVariableExport.getParam1().isBlank()) {
            throw new VUserException(GlobalVariablesMultilingualResources.GLOBAL_VARIABLES_EXPORT_PARAM1_ERROR);
        }
        if (globalVariableExport.getValue() == null || globalVariableExport.getValue().isBlank()) {
            throw new VUserException(GlobalVariablesMultilingualResources.GLOBAL_VARIABLES_EXPORT_VALUE_ERROR);
        }
        GlobalVariable globalVariable = new GlobalVariable();
        globalVariable.setBotId(bot.getBotId());
        globalVariable.setParam1(globalVariableExport.getParam1());
        globalVariable.setParam2(globalVariableExport.getParam2());
        globalVariable.setParam3(globalVariableExport.getParam3());
        globalVariable.setParam4(globalVariableExport.getParam4());
        globalVariable.setValue(globalVariableExport.getValue());
        if (globalVariableTypesMap.containsKey(globalVariableExport.getType())) {
            globalVariable.setGvtId(globalVariableTypesMap.get(globalVariableExport.getType()));
        } else {
            GlobalVariableType globalVariableType = new GlobalVariableType();
            globalVariableType.setLabel(globalVariableExport.getType());
            globalVariableType.setBotId(bot.getBotId());
            globalVariableType = globalVariablesTypeService.saveGlobalVariableType(bot, globalVariableType);
            globalVariableTypesMap.put(globalVariableExport.getType(), globalVariableType.getGvtId());
            globalVariable.setGvtId(globalVariableType.getGvtId());
        }
        globalVariableService.saveGlobalVariable(bot, globalVariable);
    }

    public List<GlobalVariableExport> transformFileToList(final VFile file) {
        final String[] columns = new String[] {
                DtDefinitions.GlobalVariableExportFields.type.name(),
                DtDefinitions.GlobalVariableExportFields.param1.name(),
                DtDefinitions.GlobalVariableExportFields.param2.name(),
                DtDefinitions.GlobalVariableExportFields.param3.name(),
                DtDefinitions.GlobalVariableExportFields.param4.name(),
                DtDefinitions.GlobalVariableExportFields.value.name()
        };
        return designerFileServices.readCsvFile(GlobalVariableExport.class, file, columns);
    }

}
