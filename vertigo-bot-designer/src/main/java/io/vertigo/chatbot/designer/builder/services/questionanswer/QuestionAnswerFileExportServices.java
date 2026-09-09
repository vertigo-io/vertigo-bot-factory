package io.vertigo.chatbot.designer.builder.services.questionanswer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

/**
 * Import et export CSV des questions/reponses FAQ.
 * L'export n'inclut pas la sequence : l'ordre est reconstruit a l'import par normalisation 1..N par categorie.
 *
 * @author Chatbot Team
 */
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

    /**
     * Importe des questions/reponses depuis un CSV puis normalise les sequences de chaque categorie impactee.
     *
     * @param bot le chatbot cible
     * @param importQuestionAnswerFile URI du fichier temporaire a importer
     */
    public void importQueAnsFromCSVFile(final Chatbot bot, final FileInfoURI importQuestionAnswerFile) {
        final Map<String, Long> queAnsLabelIdMap = mapCategoryInitialization(bot);
        final Set<Long> impactedCategoryIds = new HashSet<>();
        transformFileToList(designerFileServices.getFileTmp(importQuestionAnswerFile)).forEach(questionAnswerExport -> {
            final Long categoryId = generateQueAnsFromQueAnsExport(bot, questionAnswerExport, queAnsLabelIdMap);
            if (categoryId != null) {
                impactedCategoryIds.add(categoryId);
            }
        });
        impactedCategoryIds.forEach(categoryId -> questionAnswerServices.normalizeQuestionAnswerSequences(bot, categoryId));
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

    /**
     * Cree ou met a jour une question/reponse a partir d'une ligne d'export.
     * La sequence n'est jamais lue depuis le CSV : une creation recoit la prochaine valeur, une mise a jour conserve la sienne.
     *
     * @param bot le chatbot cible
     * @param questionAnswerFileExport ligne importee
     * @param queAnsLabelIdMap correspondance label de categorie vers identifiant
     * @return l'identifiant de la categorie impactee, ou {@code null} si la categorie est inconnue
     */
    public Long generateQueAnsFromQueAnsExport(final Chatbot bot, final QuestionAnswerFileExport questionAnswerFileExport, Map<String, Long> queAnsLabelIdMap) {
        final QuestionAnswer questionAnswer = new QuestionAnswer();
        final Optional<QuestionAnswer> questionAnswerBase = questionAnswerServices.getQueAnsByCode(questionAnswerFileExport.getCode(), bot);

        if (queAnsLabelIdMap.containsKey(questionAnswerFileExport.getCategory())) {
            //if questionAnswer already exists, we use its id to update it
            questionAnswerBase.ifPresent(queAnsBase -> questionAnswer.setQaId(queAnsBase.getQaId()));

            questionAnswer.setBotId(bot.getBotId());
            questionAnswer.setQuestion(questionAnswerFileExport.getQuestion());
            questionAnswer.setAnswer(questionAnswerFileExport.getAnswer());
            questionAnswer.setIsEnabled("TRUE".equals(questionAnswerFileExport.getIsEnabled()));
            questionAnswer.setQaCatId(queAnsLabelIdMap.get(questionAnswerFileExport.getCategory()));
            questionAnswer.setCode(questionAnswerFileExport.getCode());
            questionAnswerServices.saveQuestionAnswer(bot, questionAnswer);
            return questionAnswer.getQaCatId();
        }
        return null;
    }
}
