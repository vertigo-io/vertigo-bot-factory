package io.vertigo.chatbot.designer.builder.services.questionanswer;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.questionanswer.QuestionAnswerCategoryDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerCategory;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerCategoryExport;
import io.vertigo.chatbot.commons.multilingual.queAnsCategory.QueAnsCategoryMultilingualResources;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class QuestionAnswerCategoryServices implements Component {

    @Inject
    private QuestionAnswerCategoryDAO questionAnswerCategoryDAO;

    @Inject
    private QuestionAnswerServices questionAnswerServices;

    @Inject
    private ExporterManager exportManager;

    @Inject
    private DesignerFileServices designerFileServices;

    public DtList<QuestionAnswerCategory> getAllQueAnsCatByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return questionAnswerCategoryDAO.findAll(Criterions.isEqualTo(DtDefinitions.QuestionAnswerCategoryFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public QuestionAnswerCategory getQueAnsCategoryById(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        return questionAnswerCategoryDAO.get(categoryId);
    }

    public Optional<QuestionAnswerCategory> getQueAnsCategoryByBotIdAndLabel(@SecuredOperation("botVisitor") final Chatbot bot, final String label) {
        return questionAnswerCategoryDAO.findOptional(Criterions.isEqualTo(DtDefinitions.QuestionAnswerCategoryFields.botId, bot.getBotId())
                .and(Criterions.isEqualTo(DtDefinitions.QuestionAnswerCategoryFields.label, label)));
    }

    public void saveCategory(@SecuredOperation("botVisitor") final Chatbot bot, final QuestionAnswerCategory questionAnswerCategory) {
        questionAnswerCategoryDAO.save(questionAnswerCategory);
    }


    public void deleteCategory(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        for (final QuestionAnswer questionAnswer : questionAnswerServices.getAllQueAnsByCatId(bot, categoryId)) {
            questionAnswerServices.deleteQueAnsById(bot,questionAnswer.getQaId());
        }
        questionAnswerCategoryDAO.delete(categoryId);
    }

    public QuestionAnswerCategory getNewQueAnsCategory(@SecuredOperation("botAdm") final Chatbot bot) {
        final QuestionAnswerCategory category = new QuestionAnswerCategory();
        category.setBotId(bot.getBotId());
        category.setIsEnabled(true);
        return category;
    }

    public VFile exportQueAnsCategories(@SecuredOperation("botVisitor") final Chatbot bot, final DtList<QuestionAnswerCategory> questionAnswerCategories) {
        final DtList<QuestionAnswerCategoryExport> questionAnswerCategoryExports = questionAnswerCategories.stream().map(category -> {
            final QuestionAnswerCategoryExport questionAnswerCategoryExport = new QuestionAnswerCategoryExport();
            questionAnswerCategoryExport.setLabel(category.getLabel());
            questionAnswerCategoryExport.setIsEnabled(category.getIsEnabled() ? "TRUE" : "FALSE");
            return questionAnswerCategoryExport;
        }).collect(VCollectors.toDtList(QuestionAnswerCategoryExport.class));
        final String exportName = LocaleMessageText.of(QueAnsCategoryMultilingualResources.EXPORT_QUEANS_CATEGORIES_FILENAME, bot.getName()).getDisplay();
        final Export export = new ExportBuilder(ExportFormat.CSV, exportName)
                .beginSheet(questionAnswerCategoryExports, null)
                .addField(DtDefinitions.QuestionAnswerCategoryExportFields.label)
                .addField(DtDefinitions.QuestionAnswerCategoryExportFields.isEnabled)
                .endSheet()
                .build();
        return exportManager.createExportFile(export);
    }
    public void importQueAnsCategoriesFromCSVFile(@SecuredOperation("botAdm") final Chatbot chatbot, final FileInfoURI importCategoriesFileUri) {
        transformFileToList(designerFileServices.getFileTmp(importCategoriesFileUri)).forEach(questionAnswerCategory -> generateQueAnsCategoryFromCategoryExport(questionAnswerCategory, chatbot));
    }

    public List<QuestionAnswerCategoryExport> transformFileToList(final VFile file) {
        final String[] columns = new String[]{
                DtDefinitions.QuestionAnswerCategoryExportFields.label.name(),
                DtDefinitions.QuestionAnswerCategoryExportFields.isEnabled.name(),
        };
        return designerFileServices.readCsvFile(QuestionAnswerCategoryExport.class, file, columns);
    }

    public void generateQueAnsCategoryFromCategoryExport(final QuestionAnswerCategoryExport categoryExport, final Chatbot chatbot) {
        final Optional<QuestionAnswerCategory> queAnsCategoryEntityBase = getQueAnsCategoryByBotIdAndLabel(chatbot, categoryExport.getLabel());
        if (queAnsCategoryEntityBase.isEmpty()) {
            final QuestionAnswerCategory questionAnswerCategory = new QuestionAnswerCategory();
            questionAnswerCategory.setBotId(chatbot.getBotId());
            questionAnswerCategory.setLabel(categoryExport.getLabel());
            questionAnswerCategory.setIsEnabled("TRUE".equals(categoryExport.getIsEnabled()));
            saveCategory(chatbot, questionAnswerCategory);
        }
    }
}
