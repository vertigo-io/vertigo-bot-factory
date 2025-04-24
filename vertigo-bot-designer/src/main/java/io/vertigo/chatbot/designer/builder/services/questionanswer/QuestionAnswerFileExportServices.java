package io.vertigo.chatbot.designer.builder.services.questionanswer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerCategory;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerFileExport;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerIhm;
import io.vertigo.chatbot.commons.multilingual.questionAnswer.QuestionAnswerMultilingualResources;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
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

@Transactional
public class QuestionAnswerFileExportServices implements Component {

    @Inject
    QuestionAnswerServices questionAnswerServices;

    @Inject
    QuestionAnswerCategoryServices questionAnswerCategoryServices;

    @Inject
    ExporterManager exportManager;

    @Inject
    DesignerFileServices designerFileServices;


    public VFile exportQuestionAnswers(@SecuredOperation("botVisitor") final Chatbot bot, final DtList<QuestionAnswerIhm> questionAnswerIhms) {
        final DtList<QuestionAnswerFileExport> QuestionAnswerFileExports = questionAnswerIhms.stream().map(questionAnswer -> {
            final QuestionAnswerFileExport QuestionAnswerFileExport = new QuestionAnswerFileExport();
            QuestionAnswerFileExport.setQuestion(questionAnswer.getQuestion());
            QuestionAnswerFileExport.setAnswer(questionAnswer.getAnswer());
            QuestionAnswerFileExport.setIsEnabled(questionAnswer.getIsEnabled() ? "TRUE" : "FALSE");
            QuestionAnswerFileExport.setCategory(questionAnswer.getCatLabel());
            QuestionAnswerFileExport.setCode(questionAnswer.getCode());
            return QuestionAnswerFileExport;
        }).collect(VCollectors.toDtList(QuestionAnswerFileExport.class));
        final String exportName = LocaleMessageText.of(QuestionAnswerMultilingualResources.EXPORT_QUESTIONS_ANSWERS_FILENAME, bot.getName()).getDisplay();
        final Export export = new ExportBuilder(ExportFormat.CSV, exportName)
                .beginSheet(QuestionAnswerFileExports, null)
                .addField(DtDefinitions.QuestionAnswerFileExportFields.question)
                .addField(DtDefinitions.QuestionAnswerFileExportFields.answer)
                .addField(DtDefinitions.QuestionAnswerFileExportFields.isEnabled)
                .addField(DtDefinitions.QuestionAnswerFileExportFields.category)
                .addField(DtDefinitions.QuestionAnswerFileExportFields.code)
                .endSheet()
                .build();
        return exportManager.createExportFile(export);
    }

    public void importQueAnsFromCSVFile(final Chatbot bot, final FileInfoURI importQuestionAnswerFile) {
        final Map<String, Long> queAnsLabelIdMap = mapCategoryInitialization(bot);
        transformFileToList(designerFileServices.getFileTmp(importQuestionAnswerFile)).forEach(questionAnswerCategory -> generateQueAnsFromQueAnsExport(bot, questionAnswerCategory, queAnsLabelIdMap));
    }

    public List<QuestionAnswerFileExport> transformFileToList(@SecuredOperation("SuperAdm") final VFile file) {
        final String[] columns = new String[] {
                DtDefinitions.QuestionAnswerFileExportFields.question.name(),
                DtDefinitions.QuestionAnswerFileExportFields.answer.name(),
                DtDefinitions.QuestionAnswerFileExportFields.isEnabled.name(),
                DtDefinitions.QuestionAnswerFileExportFields.category.name(),
                DtDefinitions.QuestionAnswerFileExportFields.code.name(),
        };
        return designerFileServices.readCsvFile(QuestionAnswerFileExport.class, file, columns);
    }

    private Map<String, Long> mapCategoryInitialization(final Chatbot bot) {
        final DtList<QuestionAnswerCategory> listCategory = questionAnswerCategoryServices.getAllQueAnsCatByBot(bot);
        final Map<String, Long> mapCategory = new HashMap<>();
        for (final QuestionAnswerCategory category : listCategory) {
            mapCategory.put(category.getLabel(), category.getQaCatId());
        }
        return mapCategory;
    }

    public void generateQueAnsFromQueAnsExport(final Chatbot bot, final QuestionAnswerFileExport QuestionAnswerFileExport, Map<String, Long> queAnsLabelIdMap) {
        final QuestionAnswer questionAnswer = new QuestionAnswer();
        final Optional<QuestionAnswer> questionAnswerBase = questionAnswerServices.getQueAnsByCode(QuestionAnswerFileExport.getCode(), bot);

        if (queAnsLabelIdMap.containsKey(QuestionAnswerFileExport.getCategory())) {
            //if questionAnswer already exists, we use its id to update it
            questionAnswerBase.ifPresent(queAnsBase -> questionAnswer.setQaId(queAnsBase.getQaId()));

            questionAnswer.setBotId(bot.getBotId());
            questionAnswer.setQuestion(QuestionAnswerFileExport.getQuestion());
            questionAnswer.setAnswer(QuestionAnswerFileExport.getAnswer());
            questionAnswer.setIsEnabled("TRUE".equals(QuestionAnswerFileExport.getIsEnabled()));
            questionAnswer.setQaCatId(queAnsLabelIdMap.get(QuestionAnswerFileExport.getCategory()));
            questionAnswer.setCode(QuestionAnswerFileExport.getCode());
            questionAnswerServices.saveQuestionAnswer(bot, questionAnswer);
        }
    }
}
