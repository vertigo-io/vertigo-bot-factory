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
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerExport;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.multilingual.questionAnswer.QuestionAnswerMultilingualResources;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;
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
        final DtList<QuestionAnswerExport> questionAnswerExports = questionAnswerIhms.stream().map(questionAnswer -> {
            final QuestionAnswerExport questionAnswerExport = new QuestionAnswerExport();
            questionAnswerExport.setQuestion(questionAnswer.getQuestion());
            questionAnswerExport.setAnswer(questionAnswer.getAnswer());
            questionAnswerExport.setIsEnabled(questionAnswer.getIsEnabled() ? "TRUE" : "FALSE");
            questionAnswerExport.setCategory(questionAnswer.getCatLabel());
            questionAnswerExport.setCode(questionAnswer.getCode());
            return questionAnswerExport;
        }).collect(VCollectors.toDtList(QuestionAnswerExport.class));
        final String exportName = MessageText.of(QuestionAnswerMultilingualResources.EXPORT_QUESTIONS_ANSWERS_FILENAME, bot.getName()).getDisplay();
        final Export export = new ExportBuilder(ExportFormat.CSV, exportName)
                .beginSheet(questionAnswerExports, null)
                .addField(DtDefinitions.QuestionAnswerExportFields.question)
                .addField(DtDefinitions.QuestionAnswerExportFields.answer)
                .addField(DtDefinitions.QuestionAnswerExportFields.isEnabled)
                .addField(DtDefinitions.QuestionAnswerExportFields.category)
                .addField(DtDefinitions.QuestionAnswerExportFields.code)
                .endSheet()
                .build();
        return exportManager.createExportFile(export);
    }

    public void importQueAnsFromCSVFile(final Chatbot bot, final FileInfoURI importQuestionAnswerFile) {
        final Map<String, Long> queAnsLabelIdMap = mapCategoryInitialization(bot);
        transformFileToList(designerFileServices.getFileTmp(importQuestionAnswerFile)).forEach(questionAnswerCategory -> generateQueAnsFromQueAnsExport(bot, questionAnswerCategory, queAnsLabelIdMap));
    }

    public List<QuestionAnswerExport> transformFileToList(@SecuredOperation("SuperAdm") final VFile file) {
        final String[] columns = new String[] {
                DtDefinitions.QuestionAnswerExportFields.question.name(),
                DtDefinitions.QuestionAnswerExportFields.answer.name(),
                DtDefinitions.QuestionAnswerExportFields.isEnabled.name(),
                DtDefinitions.QuestionAnswerExportFields.category.name(),
                DtDefinitions.QuestionAnswerExportFields.code.name(),
        };
        return designerFileServices.readCsvFile(QuestionAnswerExport.class, file, columns);
    }

    private Map<String, Long> mapCategoryInitialization(final Chatbot bot) {
        final DtList<QuestionAnswerCategory> listCategory = questionAnswerCategoryServices.getAllQueAnsCatByBot(bot);
        final Map<String, Long> mapCategory = new HashMap<>();
        for (final QuestionAnswerCategory category : listCategory) {
            mapCategory.put(category.getLabel(), category.getQaCatId());
        }
        return mapCategory;
    }

    public void generateQueAnsFromQueAnsExport(final Chatbot bot, final QuestionAnswerExport questionAnswerExport, Map<String, Long> queAnsLabelIdMap) {
        final QuestionAnswer questionAnswer = new QuestionAnswer();
        final Optional<QuestionAnswer> questionAnswerBase = questionAnswerServices.getQueAnsByCode(questionAnswerExport.getCode(), bot);

        if (queAnsLabelIdMap.containsKey(questionAnswerExport.getCategory())) {
            //if questionAnswer already exists, we use its id to update it
            questionAnswerBase.ifPresent(queAnsBase -> questionAnswer.setQaId(queAnsBase.getQaId()));

            questionAnswer.setBotId(bot.getBotId());
            questionAnswer.setQuestion(questionAnswerExport.getQuestion());
            questionAnswer.setAnswer(questionAnswerExport.getAnswer());
            questionAnswer.setIsEnabled("TRUE".equals(questionAnswerExport.getIsEnabled()));
            questionAnswer.setQaCatId(queAnsLabelIdMap.get(questionAnswerExport.getCategory()));
            questionAnswer.setCode(questionAnswerExport.getCode());
            questionAnswerServices.saveQuestionAnswer(bot, questionAnswer);
        }
    }
}
