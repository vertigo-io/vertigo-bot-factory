package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.AttachmentInfo;
import io.vertigo.chatbot.commons.dao.AttachmentFileInfoDAO;
import io.vertigo.chatbot.commons.dao.SavedTrainingDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.SavedTraining;
import io.vertigo.chatbot.commons.domain.SavedTrainingCriteria;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicFileExport;
import io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.model.ModelMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.topic.*;
import io.vertigo.chatbot.designer.builder.services.topic.export.file.TopicFileExportServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.VFile;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.builder.services.TrainingServices.MAX_TRAINING_ELEMENTS;
import static java.lang.Long.parseLong;

@Transactional
public class SavedTrainingServices implements Component {

    @Inject
    private SavedTrainingDAO savedTrainingDAO;

    @Inject
    private AttachmentFileInfoDAO attachmentFileInfoDAO;

    @Inject
    private TopicCategoryServices topicCategoryServices;

    @Inject
    private TopicServices topicServices;

    @Inject
    private UtterTextServices utterTextServices;

    @Inject
    private TopicLabelServices topicLabelServices;

    @Inject
    private ScriptIntentionServices scriptIntentionServices;

    @Inject
    private SmallTalkServices smallTalkServices;

    @Inject
    private TopicFileExportServices topicFileExportServices;

    @Inject
    private DictionaryEntityServices dictionaryEntityServices;

    @Inject
    private FileServices fileServices;

    @Inject
    private FileStoreManager fileStoreManager;


    @Secured("BotUser")
    public SavedTraining save(@SecuredOperation("botAdm") final Chatbot bot, final SavedTraining savedTraining) {
        savedTrainingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.SavedTrainingFields.botId, savedTraining.getBotId())
                        .and(Criterions.isEqualTo(DtDefinitions.SavedTrainingFields.traId, savedTraining.getTraId())))
                .ifPresent(it -> {
                    throw new VUserException(ModelMultilingualResources.SAVED_TRAINING_ALREADY_EXISTS);
                });
        FileInfo newSavedTrainingInfo = saveConfig(bot);
        savedTraining.setAttFileInfoId(parseLong(newSavedTrainingInfo.getURI().getKey().toString()));
        return savedTrainingDAO.save(savedTraining);
    }

    public FileInfo saveConfig(@SecuredOperation("botAdm") final Chatbot bot) {
        final Map<String, VFile> fileMap = new HashMap<>();

        final DtList<TopicCategory> topicCategories = topicCategoryServices.getAllCategoriesByBot(bot);
        fileMap.put(MessageText.of(ExportMultilingualResources.FILE_TYPE_CATEGORIES).getDisplay(), topicCategoryServices.exportCategories(bot, topicCategories));

        final DtList<TopicFileExport> listTopics = topicFileExportServices.getTopicFileExport(bot.getBotId(),
                topicCategoryServices.getAllCategoriesByBot(bot).stream().map(TopicCategory::getTopCatId).collect(Collectors.toList()));
        fileMap.put(MessageText.of(ExportMultilingualResources.FILE_TYPE_TOPICS).getDisplay(), topicFileExportServices.exportTopicFile(bot, listTopics));

        final DtList<DictionaryEntityWrapper> listDictionaryEntitiesToExport = dictionaryEntityServices.getDictionaryExportByBotId(bot.getBotId(), "|");
        fileMap.put(MessageText.of(ExportMultilingualResources.FILE_TYPE_DICTIONARY).getDisplay(), dictionaryEntityServices.exportDictionary(bot, listDictionaryEntitiesToExport));

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        VFile zipFile = fileServices.zipMultipleFiles(fileMap,
                MessageText.of(BotMultilingualResources.EXPORT_ZIP_FILENAME, bot.getName(), dateFormat.format(new Date())).getDisplay());

        return fileStoreManager.create(new AttachmentInfo(zipFile));
    }


    public void rollbackConfig(@SecuredOperation("botAdm") final Chatbot bot, final long savedTrainingId) {

        SavedTraining savedTraining = getById(savedTrainingId);
        savedTraining.attachmentFileInfo().load();
        long fileInfoId = savedTraining.attachmentFileInfo().get().getAttFiId();
        VFile savedTrainingZip = fileServices.getAttachment(fileInfoId);
        Map<String, VFile> csvMap = fileServices.unzipMultipleFiles(savedTrainingZip);

        VFile categoriesCSV = csvMap.get(MessageText.of(ExportMultilingualResources.FILE_TYPE_CATEGORIES).getDisplay());
        VFile topicsCSV = csvMap.get(MessageText.of(ExportMultilingualResources.FILE_TYPE_TOPICS).getDisplay());
        VFile dictionaryCSV = csvMap.get(MessageText.of(ExportMultilingualResources.FILE_TYPE_DICTIONARY).getDisplay());

        utterTextServices.removeAllUtterTextByBotId(bot);
        dictionaryEntityServices.deleteAllByBot(bot);
        topicServices.removeAllNTSFromBot(bot);
        topicLabelServices.cleanLabelFromBot(bot);
        scriptIntentionServices.removeAllScriptIntentionFromBot(bot);
        smallTalkServices.removeAllSmallTalkFromBot(bot);
        topicServices.removeAllTopicsFromBot(bot);
        topicCategoryServices.removeAllCategoryByBot(bot);

        // import new Categories
        topicCategoryServices.transformFileToList(categoriesCSV).forEach(topicCategory -> topicCategoryServices.generateCategoryFromCategoryExport(topicCategory, bot));
        // import new Topics
        topicFileExportServices.importTopicFromList(bot, topicFileExportServices.transformFileToList(topicsCSV), true);
        // import new Dictionary
        dictionaryEntityServices.transformFileToList(dictionaryCSV).forEach(dex -> dictionaryEntityServices.generateDictionaryFromDictionaryExport(dex, bot));
    }


    @Secured("BotUser")
    public void delete(@SecuredOperation("botAdm") final Chatbot bot, final Long id) {
        SavedTraining savedTraining = getById(id);
        savedTrainingDAO.delete(id);
        fileServices.deleteAttachment(savedTraining.getAttFileInfoId());
    }

    public DtList<SavedTraining> getAllSavedTrainingByBotId(final Long botId) {
        return savedTrainingDAO.findAll(Criterions.isEqualTo(DtDefinitions.SavedTrainingFields.botId, botId),
                DtListState.of(MAX_TRAINING_ELEMENTS, 0, DtDefinitions.SavedTrainingFields.creationTime.name(), true));
    }

    public void deleteAllByBotId(final Chatbot bot) {
        getAllSavedTrainingByBotId(bot.getBotId()).forEach(savedTraining -> delete(bot, savedTraining.getSavedTraId()));
    }

    public SavedTraining getById(final Long savedTraId) {
        return savedTrainingDAO.get(savedTraId);
    }

    private DtList<SavedTraining> findAllByBotIdAndBetweenDates(final Long botId, final LocalDate fromDate, final LocalDate toDate) {
        return savedTrainingDAO.getSavedTrainingByBotIdAndWithDateBetween(botId, fromDate, toDate);
    }

    private DtList<SavedTraining> findAllByBotIdAndWithDateAfter(final Long botId, final LocalDate fromDate) {
        return savedTrainingDAO.getSavedTrainingByBotIdAndWithDateAfter(botId, fromDate);
    }

    private DtList<SavedTraining> findAllByBotIdAndWithDateBefore(final Long botId, final LocalDate toDate) {
        return savedTrainingDAO.getSavedTrainingByBotIdAndWithDateBefore(botId, toDate);
    }

    public DtList<SavedTraining> filter(final Chatbot bot, final SavedTrainingCriteria criteria) {
        DtList<SavedTraining> filteredSavedTraining;
        if (criteria.getFromDate() == null && criteria.getToDate() == null) {
            filteredSavedTraining = getAllSavedTrainingByBotId(bot.getBotId());
        } else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
            filteredSavedTraining = findAllByBotIdAndWithDateAfter(bot.getBotId(), criteria.getFromDate());
        } else if (criteria.getFromDate() == null && criteria.getToDate() != null) {
            filteredSavedTraining = findAllByBotIdAndWithDateBefore(bot.getBotId(), criteria.getToDate().plusDays(1));
        } else {
            filteredSavedTraining = findAllByBotIdAndBetweenDates(bot.getBotId(), criteria.getFromDate(), criteria.getToDate().plusDays(1));
        }

        if (criteria.getText() != null) {
            filteredSavedTraining = filteredSavedTraining.stream().filter(savedTraining -> {
                savedTraining.training().load();
                return savedTraining.getName().toLowerCase().contains(criteria.getText().toLowerCase()) ||
                        savedTraining.training().get().getVersionNumber().toString().toLowerCase().contains(criteria.getText().toLowerCase());
            }).collect(VCollectors.toDtList(SavedTraining.class));
        }
        return filteredSavedTraining;
    }
}
