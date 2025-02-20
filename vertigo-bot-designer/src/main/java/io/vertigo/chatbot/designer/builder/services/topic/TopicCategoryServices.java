package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.TopicCategoryDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicCategoryExport;
import io.vertigo.chatbot.commons.multilingual.categories.CategoriesMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.topicCategory.TopicCategoryPAO;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static io.vertigo.chatbot.designer.builder.services.topic.TopicsUtils.DEFAULT_TOPIC_CAT_CODE;

@Transactional
@Secured("BotUser")
public class TopicCategoryServices implements Component {

    @Inject
    private TopicCategoryDAO topicCategoryDAO;

    @Inject
    private TopicCategoryPAO topicCategoryPAO;

    @Inject
    private TopicServices topicServices;

    @Inject
    private ExporterManager exportManager;

    @Inject
    private DesignerFileServices designerFileServices;

    public TopicCategory saveCategory(@SecuredOperation("botAdm") final Chatbot bot, final TopicCategory category) {
        return topicCategoryDAO.save(category);
    }

    public void deleteCategory(@SecuredOperation("botAdm") final Chatbot bot, final TopicCategory category) {

        for (final Topic topic : getAllTopicFromCategory(bot, category)) {
            topicServices.deleteCompleteTopic(bot, topic);
        }
        topicCategoryDAO.delete(category.getTopCatId());
    }

    public TopicCategory getTopicCategoryById(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        return topicCategoryDAO.get(categoryId);
    }

    public DtList<Topic> getAllTopicFromCategory(@SecuredOperation("botVisitor") final Chatbot bot, final TopicCategory category) {
        return topicServices.getTopicFromTopicCategory(category);
    }

    public DtList<TopicCategory> getAllCategoriesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return topicCategoryDAO.getAllCategoriesByBotId(bot.getBotId(), Optional.empty(), Optional.empty());
    }

    public Optional<TopicCategory> getTopicCategoryByBotIdAndCode(@SecuredOperation("botVisitor") final Chatbot bot, final String code) {
        return topicCategoryDAO.findOptional(Criterions.isEqualTo(DtDefinitions.TopicCategoryFields.botId, bot.getBotId())
                .and(Criterions.isEqualTo(DtDefinitions.TopicCategoryFields.code, code)));
    }

    public DtList<TopicCategory> getAllNonTechnicalCategoriesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return topicCategoryDAO.getAllCategoriesByBotId(bot.getBotId(), Optional.empty(), Optional.of(false));
    }

    public DtList<TopicCategory> getAllActiveCategoriesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return topicCategoryDAO.getAllCategoriesByBotId(bot.getBotId(), Optional.of(true), Optional.of(false));
    }

    public TopicCategory getNewTopicCategory(@SecuredOperation("botAdm") final Chatbot bot) {
        final TopicCategory category = new TopicCategory();
        category.setBotId(bot.getBotId());
        // Modify in the futur if sublevel needs
        category.setLevel(1L);
        category.setIsEnabled(true);
        category.setIsTechnical(false);
        return category;
    }

    public void removeAllCategoryByBot(@SecuredOperation("botAdm") final Chatbot bot) {
        topicCategoryPAO.removeAllCategoryByBotId(bot.getBotId());
    }

    public TopicCategory initializeBasicCategory(final Chatbot chatbot) {
        final TopicCategory topicCategory = new TopicCategory();
        topicCategory.setIsEnabled(true);
        topicCategory.setLabel(LocaleMessageText.of(TopicsMultilingualResources.DEFAULT_TOPICS).getDisplay());
        topicCategory.setCode(DEFAULT_TOPIC_CAT_CODE);
        topicCategory.setIsTechnical(true);
        topicCategory.setLevel(1L);
        topicCategory.setBotId(chatbot.getBotId());
        return topicCategoryDAO.save(topicCategory);
    }

    public VFile exportCategories(@SecuredOperation("botVisitor") final Chatbot bot, final DtList<TopicCategory> topicCategories) {
        final DtList<TopicCategoryExport> topicCategoryExports = topicCategories.stream().map(topicCategory -> {
            final TopicCategoryExport topicCategoryExport = new TopicCategoryExport();
            topicCategoryExport.setCode(topicCategory.getCode());
            topicCategoryExport.setLabel(topicCategory.getLabel());
            topicCategoryExport.setIsEnabled(topicCategory.getIsEnabled() ? "TRUE" : "FALSE");
            topicCategoryExport.setIsTechnical(topicCategory.getIsTechnical() ? "TRUE" : "FALSE");
            return topicCategoryExport;
        }).collect(VCollectors.toDtList(TopicCategoryExport.class));
        final String exportName = LocaleMessageText.of(CategoriesMultilingualResources.EXPORT_CATEGORIES_FILENAME, bot.getName()).getDisplay();
        final Export export = new ExportBuilder(ExportFormat.CSV, exportName)
                .beginSheet(topicCategoryExports, null)
                .addField(DtDefinitions.TopicCategoryExportFields.code)
                .addField(DtDefinitions.TopicCategoryExportFields.label)
                .addField(DtDefinitions.TopicCategoryExportFields.isEnabled)
                .addField(DtDefinitions.TopicCategoryExportFields.isTechnical)
                .endSheet()
                .build();

        return exportManager.createExportFile(export);

    }

    public void importCategoriesFromCSVFile(@SecuredOperation("botAdm") final Chatbot chatbot, final FileInfoURI importCategoriesFileUri) {
        transformFileToList(designerFileServices.getFileTmp(importCategoriesFileUri)).forEach(topicCategory -> generateCategoryFromCategoryExport(topicCategory, chatbot));
    }

    public List<TopicCategoryExport> transformFileToList(final VFile file) {
        final String[] columns = new String[]{
                DtDefinitions.TopicCategoryExportFields.code.name(),
                DtDefinitions.TopicCategoryExportFields.label.name(),
                DtDefinitions.TopicCategoryExportFields.isEnabled.name(),
                DtDefinitions.TopicCategoryExportFields.isTechnical.name(),
        };
        return designerFileServices.readCsvFile(TopicCategoryExport.class, file, columns);
    }

    public void generateCategoryFromCategoryExport(final TopicCategoryExport topicCategoryExport, final Chatbot chatbot) {
        final Optional<TopicCategory> topicCategoryEntityBase = getTopicCategoryByBotIdAndCode(chatbot, topicCategoryExport.getCode());
        if (topicCategoryEntityBase.isEmpty()) {
            final TopicCategory topicCategory = new TopicCategory();
            topicCategory.setBotId(chatbot.getBotId());
            topicCategory.setCode(topicCategoryExport.getCode());
            topicCategory.setLabel(topicCategoryExport.getLabel());
            topicCategory.setIsEnabled("TRUE".equals(topicCategoryExport.getIsEnabled()));
            topicCategory.setIsTechnical("TRUE".equals(topicCategoryExport.getIsTechnical()));
            saveCategory(chatbot, topicCategory);
        }

    }

}
