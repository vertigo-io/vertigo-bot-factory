package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.topic.TopicDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.UnknownSentenceDetail;
import io.vertigo.chatbot.commons.domain.UnknownSentenceStatusEnum;
import io.vertigo.chatbot.commons.domain.UnknownSentenceToUpdateIhm;
import io.vertigo.chatbot.commons.domain.topic.*;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.UnknownSentencesServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.topic.TopicPAO;
import io.vertigo.chatbot.designer.utils.HashUtils;
import io.vertigo.chatbot.domain.DtDefinitions.NluTrainingSentenceFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopicFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.model.Entity;
import io.vertigo.datamodel.data.util.VCollectors;

import javax.inject.Inject;
import java.util.*;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;
import static java.lang.Long.parseLong;


@Transactional
public class TopicServices implements Component, Activeable {

    @Inject
    private TopicDAO topicDAO;

    @Inject
    private TopicPAO topicPAO;

    @Inject
    private NluTrainingSentenceDAO nluTrainingSentenceDAO;

    @Inject
    private KindTopicServices kindTopicServices;

    private final Set<ITopicService<? extends Entity>> topicInterfaceServices = new HashSet<>();

    @Inject
    private SmallTalkServices smallTalkServices;

    @Inject
    private ScriptIntentionServices scriptIntentionServices;

    @Inject
    private NodeServices nodeServices;

    @Inject
    protected NluTrainingSentenceServices nluTrainingSentenceServices;

    @Inject
    private TopicLabelServices topicLabelServices;

    @Inject
    private UnknownSentencesServices unknownSentencesServices;

    public Topic findTopicById(@SecuredOperation("botVisitor") final Long id) {
        return topicDAO.get(id);
    }

    public TopicIhm findTopicIhmById(@SecuredOperation("botVisitor") final Long id) {
        return topicPAO.getTopicIhmById(id);
    }

    private Topic doSave(final Topic topic, final Chatbot bot) {
        //create code for export
        hasUniqueCode(topic);
        if (topic.getTopId() != null) {
            final Topic oldTopic = findTopicById(topic.getTopId());
            if (!oldTopic.getCode().equals(topic.getCode())) {
                nodeServices.updateNodes(bot);
            }
        }
        return topicDAO.save(topic);
    }


    public void saveCategoryChange(@SecuredOperation("botContributor") final Long topicId, final Long topicCategoryId, Chatbot bot) {
        Topic topic = topicDAO.get(topicId);
        topic.setTopCatId(topicCategoryId);
        topicDAO.update(topic);
    }

    public void saveTopicsCategoryChangeFromTopIdsString(@SecuredOperation("botContributor") final String topicIdsString, final Long topicCategoryId, Chatbot bot) {
        List<String> topicIdList = Arrays.asList(topicIdsString.split(","));
        topicIdList.forEach(topicId -> saveCategoryChange(parseLong(topicId), topicCategoryId, bot));
    }

    public DtList<TopicIhm> filterTopicsRemovingACategory(final DtList<TopicIhm> topicIhmList, final Long topicCategoryId, final Chatbot bot) {
        return topicIhmList.stream()
                .filter(topicIhm -> !Objects.equals(topicIhm.getCatId(), topicCategoryId))
                .sorted(Comparator.comparing(TopicIhm::getCatId)
                        .thenComparing(TopicIhm::getTitle))
                .collect(VCollectors.toDtList(TopicIhm.class));
    }


    public Topic save(@SecuredOperation("botContributor") final Topic topic, final Chatbot bot, final Boolean isEnabled, final DtList<NluTrainingSentence> nluTrainingSentences,
                      DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {

        //check if code matches the pattern
        if (KindTopicEnum.NORMAL.name().equals(topic.getKtoCd())) {
            Assertion.check().isNotNull(nluTrainingSentences)
                    .isNotNull(nluTrainingSentencesToDelete);
        }
        //create code for export
        hasUniqueCode(topic);
        // save and remove NTS
        final DtList<NluTrainingSentence> oldNluSentences = topic.getTopId() != null ? getNluTrainingSentenceByTopic(bot, topic) : new DtList<>(NluTrainingSentence.class);
        if (KindTopicEnum.UNREACHABLE.name().equals(topic.getKtoCd())) {
            nluTrainingSentencesToDelete = oldNluSentences;
            topic.setIsEnabled(isEnabled);
        } else {
            final DtList<NluTrainingSentence> ntsToSave = saveAllNotBlankNTS(topic, nluTrainingSentences); //TODO do this check before this function
            topic.setIsEnabled(!ntsToSave.isEmpty() && isEnabled);
        }
        removeNTS(nluTrainingSentencesToDelete);

        final Topic oldTopic = topic.getTopId() != null ? findTopicById(topic.getTopId()) : null;
        if ((oldTopic != null && !oldTopic.getCode().equals(topic.getCode())) || !nluTrainingSentencesToDelete.isEmpty()
                || !HashUtils.generateHashCodeForNluTrainingSentences(oldNluSentences).equals(HashUtils.generateHashCodeForNluTrainingSentences(nluTrainingSentences))) {
            nodeServices.updateNodes(bot);
        }

        return topicDAO.save(topic);
    }

    private void hasUniqueCode(final Topic topic) {
        final Optional<Long> topIdOpt = topic.getTopId() != null ? Optional.of(topic.getTopId()) : Optional.empty();
        if (topicPAO.checkUnicityTopicCode(topic.getBotId(), topic.getCode(), topIdOpt)) {
            throw new VUserException(TopicsMultilingualResources.CODE_NON_UNIQUE_ERROR);
        }
        if (TopicsUtils.checkSpecialCharacters(topic.getCode())) {
            throw new VUserException("The code cannot contain the following characters : '[', ']', '|', '¤'. ");
        }
    }

    public Topic createTopic(@SecuredOperation("botContributor") final Topic topic) {
        return topicDAO.create(topic);
    }

    public Topic createTopic(@SecuredOperation("botContributor") final String title, final String ttoCd, final String description, final Boolean isEnabled) {
        final Topic toCreate = new Topic();
        toCreate.setTitle(title);
        toCreate.setTtoCd(ttoCd);
        toCreate.setDescription(description);
        toCreate.setIsEnabled(isEnabled);
        return topicDAO.create(toCreate);
    }

    public Topic getNewTopic(@SecuredOperation("botContributor") final Chatbot bot) {
        final Topic topic = new Topic();
        topic.setBotId(bot.getBotId());
        topic.setIsEnabled(true);
        topic.setKtoCd(KindTopicEnum.NORMAL.name());
        topic.setTtoCd(TypeTopicEnum.SMALLTALK.name());
        return topic;
    }

    public void deleteTopic(@SecuredOperation("botContributor") final Chatbot bot, final Topic topic) {
        // delete sub elements
        for (final NluTrainingSentence its : getNluTrainingSentenceByTopic(bot, topic)) {
            nluTrainingSentenceDAO.delete(its.getUID());
        }
        topicLabelServices.cleanLabelFromTopic(bot, topic.getTopId());
        topicDAO.delete(topic.getTopId());
        nodeServices.updateNodes(bot);
    }

    public void deleteCompleteTopic(@SecuredOperation("botContributor") final Chatbot bot, final Topic topic) {
        for (final ITopicService<? extends Entity> services : topicInterfaceServices) {
            services.deleteIfExists(bot, topic);
        }
        deleteTopic(bot, topic);
    }

    public DtList<Topic> getAllTopicByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<Topic> getAllTopicByBotId(final Long botId) {
        return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<Topic> getAllTopicByBotIdExceptACategory(final Chatbot bot, final Long categoryId) {
        DtList<Topic> allTopics = getAllTopicByBot(bot);
        return allTopics.stream().filter(topic -> !topic.getTopCatId().equals(categoryId)).collect(VCollectors.toDtList(Topic.class));
    }

    public DtList<Topic> getAllTopicByBotTtoCd(@SecuredOperation("botVisitor") final Chatbot bot, final String ttoCd) {
        return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()).and(Criterions.isEqualTo(TopicFields.ttoCd, ttoCd)), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<Topic> getAllNonTechnicalTopicAndActiveByBotTtoCd(@SecuredOperation("botVisitor") final Chatbot bot, final String ttoCd) {
        return topicDAO.findAll(
                Criterions.isEqualTo(TopicFields.botId, bot.getBotId())
                        .and(Criterions.isEqualTo(TopicFields.isEnabled, true))
                        .and(Criterions.isEqualTo(TopicFields.ttoCd, ttoCd)
                                .and(Criterions.isEqualTo(TopicFields.ktoCd, KindTopicEnum.NORMAL.name())
                                        .or(Criterions.isEqualTo(TopicFields.ktoCd, KindTopicEnum.UNREACHABLE.name())))),
                DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<Topic> getAllTopicEnableByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()).and(Criterions.isEqualTo(TopicFields.isEnabled, true)), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public void removeAllTopicsFromBot(@SecuredOperation("botAdm") final Chatbot bot) {
        topicPAO.removeAllTopicsFromBot(bot.getBotId());
    }

    public DtList<Topic> getTopicFromTopicCategory(final TopicCategory category) {
        return topicDAO.getAllTopicFromCategory(category.getTopCatId());
    }

    public DtList<Topic> getAllTopicRelativeSmallTalkByBot(final Chatbot bot) {
        return topicDAO.getAllTopicRelativeSmallTalkByBotId(bot.getBotId());
    }

    public DtList<Topic> getAllTopicRelativeScriptIntentionByBot(final Chatbot bot) {
        return topicDAO.getAllTopicRelativeScriptIntentByBotId(bot.getBotId());
    }

    public Optional<Topic> getBasicTopicByBotIdKtoCd(final Long botId, final String ktoCd) {
        return topicDAO.getBasicTopicByBotIdKtoCd(botId, ktoCd);
    }

    public DtList<TopicIhm> getAllNonTechnicalTopicIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot, final String locale) {
        final List<String> kindTopics = new ArrayList<>();
        kindTopics.add(KindTopicEnum.NORMAL.name());
        kindTopics.add(KindTopicEnum.UNREACHABLE.name());
        return topicPAO.getAllTopicsIhmFromBot(bot.getBotId(), kindTopics, locale);
    }

    public DtList<TopicIhm> getAllTechnicalTopicIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot, final String locale) {
        return topicPAO.getAllTopicsIhmFromBot(bot.getBotId(), getTechnicalKindTopics(), locale);
    }

    public DtList<Topic> getTopicReferencingTopId(final Long topId) {
        return topicDAO.getTopicReferencingTopId(topId);
    }

    public Topic initNewBasicTopic(final Chatbot chatbot, final String ktoCd, final long catId, final String locale) {
        final Topic topic = new Topic();
        final KindTopic kto = kindTopicServices.findKindTopicByCd(ktoCd);
        topic.setBotId(chatbot.getBotId());
        topic.setIsEnabled(true);
        topic.setTitle(locale.equals(Locale.FRANCE.toString()) ? kto.getLabelFr() : kto.getLabel());
        topic.setTopCatId(catId);
        if (ktoCd.equals(KindTopicEnum.IDLE.name()) || ktoCd.equals(KindTopicEnum.RATING.name())) {
            topic.setTtoCd(TypeTopicEnum.SCRIPTINTENTION.name());
        } else {
            topic.setTtoCd(TypeTopicEnum.SMALLTALK.name());
        }
        topic.setKtoCd(ktoCd);
        topic.setDescription(locale.equals(Locale.FRANCE.toString()) ? kto.getDescriptionFr() : kto.getDescription());
        topic.setCode(ktoCd);
        return topic;

    }

    public Topic saveBotTopic(final Chatbot chatbot, final Topic topic, final String text) {
        topicDAO.save(topic);
        for (final ITopicService<? extends Entity> service : topicInterfaceServices) {
            if (service.handleObject(topic)) {
                service.createOrUpdateFromTopic(chatbot, topic, text);
            } else {
                service.deleteIfExists(chatbot, topic);
            }
        }
        return topic;

    }

    public void initTechnicalTopics(final Chatbot chatbot, final long catId, final String locale) {
        getTechnicalKindTopics().stream()
                .map(ktoCd -> initNewBasicTopic(chatbot, ktoCd, catId, locale))
                .forEach(topic -> saveBotTopic(chatbot, topic, UtterTextServices.initializeDefaultText(topic.getKtoCd())));
    }

    public static List<String> getTechnicalKindTopics() {
        return List.of(KindTopicEnum.START.name(), KindTopicEnum.END.name(), KindTopicEnum.FAILURE.name(), KindTopicEnum.IDLE.name(), KindTopicEnum.RATING.name());
    }

    public static Boolean isTechnical(final Topic topic) {
        return getTechnicalKindTopics().contains(topic.getKtoCd());
    }

    public Topic saveTtoCd(final Topic topic, final String ttoCd, final Chatbot bot) {
        topic.setTtoCd(ttoCd);
        return doSave(topic, bot);
    }

    /**
     * Save a topic with its generic elements (nlu training sentences)
     * Then delegate to a specific service implementation
     * depending on its type
     *
     * @param topic                        Global topic to save
     * @param chatbot                      Topic's chatbot
     * @param newNluTrainingSentence       New nlu training sentences
     * @param nluTrainingSentences         Nlu training sentences (including updated sentences)
     * @param nluTrainingSentencesToDelete Nlu training sentences to be deleted
     * @param dtObject                     High level object holding different types of topic (smalltalk, scriptIntention ...)
     * @param labels                       Topic's tags
     * @param initialLabels                Initial topic's tags
     */
    public void saveTopic(final Topic topic, final Chatbot chatbot,
                          final String newNluTrainingSentence, final DtList<NluTrainingSentence> nluTrainingSentences, final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
                          final DataObject dtObject,
                          final DtList<TopicLabel> labels,
                          final DtList<TopicLabel> initialLabels) {

        saveTtoCd(topic, topic.getTtoCd(), chatbot);
        for (final ITopicService<? extends Entity> service : topicInterfaceServices) {
            if (service.handleObject(topic)) {
                service.saveTopic(topic, chatbot, dtObject);
                final boolean isEnabled = service.isEnabled(topic, nluTrainingSentences, dtObject, topic.getIsEnabled(), chatbot);
                save(topic, chatbot, isEnabled, nluTrainingSentences, nluTrainingSentencesToDelete);
            } else {
                service.deleteIfExists(chatbot, topic);
            }
        }
        topicLabelServices.manageLabels(chatbot, topic, labels, initialLabels);
    }

    public void deleteTopic(final Topic topic, final Chatbot chatbot) {
        for (final ITopicService<? extends Entity> service : topicInterfaceServices) {
            if (service.handleObject(topic)) {
                service.deleteIfExists(chatbot, topic);
            }
        }
        topicLabelServices.cleanLabelFromTopic(chatbot, topic.getTopId());
        deleteTopic(chatbot, topic);
    }

    //********* NTS part ********/

    public DtList<NluTrainingSentence> addTrainingSentence(final Chatbot bot, final List<UnknownSentenceToUpdateIhm> sentencesToUpdate, final Long topId) {
        final Topic topic = findTopicById(topId);
        final DtList<NluTrainingSentence> nluTrainingSentenceDtList = getNluTrainingSentenceByTopic(bot, topic);
        for (final UnknownSentenceToUpdateIhm sentence : sentencesToUpdate) {
            final UnknownSentenceDetail unknownSentenceDetail = unknownSentencesServices.findById(sentence.getUnkSeId());
            nluTrainingSentenceServices.addTrainingSentense(sentence.getText(), nluTrainingSentenceDtList);
            unknownSentencesServices.updateStatus(unknownSentenceDetail, UnknownSentenceStatusEnum.TREATED);
        }
        final DtList<NluTrainingSentence> savedNluTrainingSentences = saveAllNotBlankNTS(topic, nluTrainingSentenceDtList);
        nodeServices.updateNodes(bot);
        return savedNluTrainingSentences;
    }

    public DtList<NluTrainingSentence> getNluTrainingSentenceByTopic(@SecuredOperation("botVisitor") final Chatbot bot, final Topic topic) {
        Assertion.check()
                .isNotNull(topic.getTopId());
        // ---

        return nluTrainingSentenceDAO.findAll(
                Criterions.isEqualTo(NluTrainingSentenceFields.topId, topic.getTopId()),
                DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, NluTrainingSentenceFields.ntsId.name(), false));
    }

    public void removeNTS(final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {
        nluTrainingSentencesToDelete.stream()
                .filter(itt -> itt.getNtsId() != null)
                .forEach(itt -> nluTrainingSentenceDAO.delete(itt.getNtsId()));
    }

    protected DtList<NluTrainingSentence> saveAllNotBlankNTS(final Topic topic, final DtList<NluTrainingSentence> nluTrainingSentences) {
        // save nlu textes
        final DtList<NluTrainingSentence> ntsToSave = nluTrainingSentences.stream()
                .filter(nts -> !StringUtil.isBlank(nts.getText()))
                .collect(VCollectors.toDtList(NluTrainingSentence.class));

        for (final NluTrainingSentence nts : ntsToSave) {
            if (TopicsUtils.checkSpecialCharacters(nts.getText())) {
                throw new VUserException("The responses cannot contain the following characters : '[', ']', '|', '¤'. ");
            }
            nts.setTopId(topic.getTopId());
            nluTrainingSentenceDAO.save(nts);

        }

        return ntsToSave;
    }

    public void removeAllNTSFromBot(final Chatbot bot) {
        topicPAO.removeAllNluTrainingSentenceByBotId(bot.getBotId());
    }

    @Override
    public void start() {
        topicInterfaceServices.add(scriptIntentionServices);
        topicInterfaceServices.add(smallTalkServices);
    }

    @Override
    public void stop() {
        //nothing
    }

    public Optional<Topic> getTopicByCode(final String code, final Long botId) {
        final Criteria<Topic> criteria = Criterions.isEqualTo(TopicFields.code, code).and(Criterions.isEqualTo(TopicFields.botId, botId));
        return topicDAO.findOptional(criteria);
    }
}
