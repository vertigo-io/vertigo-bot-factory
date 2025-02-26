package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.ChatbotUtils;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ContextValue;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.ResponseButtonUrl;
import io.vertigo.chatbot.commons.domain.topic.ResponseType;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.SmallTalkWrapper;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicLabel;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonUrlServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextValueServices;
import io.vertigo.chatbot.designer.builder.services.topic.NluTrainingSentenceServices;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicLabelServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.services.topic.TypeTopicServices;
import io.vertigo.chatbot.designer.utils.AbstractChatbotDtObjectValidator;
import io.vertigo.chatbot.domain.DtDefinitions.TopicFields;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.data.definitions.DataFieldName;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;
import static io.vertigo.chatbot.designer.utils.UserSessionUtils.getUserSession;

@Controller
@RequestMapping("/bot/{botId}/topics/detail")
@Secured("BotUser")
public class TopicDetailController extends AbstractBotCreationController<Topic> {

    protected static final ViewContextKey<Topic> topicKey = ViewContextKey.of("topic");
    protected static final ViewContextKey<Topic> topicListKey = ViewContextKey.of("topicList");

    protected static final ViewContextKey<TopicCategory> topicCategoryKey = ViewContextKey.of("topicCategory");
    protected static final ViewContextKey<TopicCategory> topicCategoryListKey = ViewContextKey.of("topicCategoryList");

    protected static final ViewContextKey<String> newNluTrainingSentenceKey = ViewContextKey.of("newNluTrainingSentence");
    protected static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesKey = ViewContextKey
            .of("nluTrainingSentences");
    protected static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesToDeleteKey = ViewContextKey
            .of("nluTrainingSentencesToDelete");

    // Label
    protected static final ViewContextKey<TopicLabel> topicLabelListKey = ViewContextKey
            .of("topicLabelList");
    protected static final ViewContextKey<TopicLabel> initialTopicLabelListKey = ViewContextKey
            .of("initialTopicLabelList");
    protected static final ViewContextKey<TopicLabel> allTopicLabelListKey = ViewContextKey
            .of("allTopicLabelList");

    @Inject
    protected TopicServices topicServices;

    @Inject
    protected TopicCategoryServices topicCategoryServices;

    @Inject
    private NluTrainingSentenceServices nluTrainingSentenceServices;

    @Inject
    protected TopicLabelServices topicLabelServices;

    @Inject
    private ScriptIntentionServices scriptIntentionServices;

    @Inject
    private SmallTalkServices smallTalkServices;

    private static final ViewContextKey<SmallTalk> smallTalkKey = ViewContextKey.of("smallTalk");

    private static final ViewContextKey<ResponseType> responseTypeKey = ViewContextKey.of("responseTypes");

    private static final ViewContextKey<UtterText> utterTextsKey = ViewContextKey.of("utterTexts");

    private static final ViewContextKey<ResponseButton> buttonsKey = ViewContextKey.of("buttons");

    private static final ViewContextKey<ResponseButtonUrl> buttonsUrlKey = ViewContextKey.of("buttonsUrl");

    private static final ViewContextKey<ScriptIntention> scriptIntentionKey = ViewContextKey.of("scriptIntention");

    private static final ViewContextKey<TypeTopic> typeTopicListKey = ViewContextKey.of("typeTopicList");

    private static final ViewContextKey<Boolean> unreachableKey = ViewContextKey.of("unreachable");

    private static final ViewContextKey<ContextValue> contextValuesKey = ViewContextKey.of("contextValues");

    private static final ViewContextKey<ChatbotCustomConfig> chatbotCustomConfigKey = ViewContextKey.of("chatbotCustomConfig");

    @Inject
    private UtterTextServices utterTextServices;

    @Inject
    private ResponsesButtonServices responsesButtonServices;

    @Inject
    private ResponsesButtonUrlServices responsesButtonUrlServices;

    @Inject
    private TypeTopicServices typeTopicServices;

    @Inject
    private ContextValueServices contextValueServices;

    @Inject
    private ChatbotCustomConfigServices chatbotCustomConfigServices;

    @GetMapping("/{topId}")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
                            @PathVariable("topId") final Long topId) {

        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final Topic topic = topicServices.findTopicById(topId);
        initTopicContext(viewContext, uiMessageStack, bot, topic);
        if (TypeTopicEnum.SCRIPTINTENTION.name().equals(topic.getTtoCd())) {
            final ScriptIntention scriptIntention = scriptIntentionServices.findByTopId(topId).orElseThrow();
            addMessageDeactivate(uiMessageStack, scriptIntention, viewContext.readDtListModifiable(nluTrainingSentencesKey, uiMessageStack), bot, topic);
            viewContext.publishDto(scriptIntentionKey, scriptIntention);
            viewContext.publishDto(smallTalkKey, smallTalkServices.getNewSmallTalk(bot));
            viewContext.publishDtListModifiable(buttonsKey, new DtList<>(ResponseButton.class));
            viewContext.publishDtListModifiable(buttonsUrlKey, new DtList<>(ResponseButtonUrl.class));
            final DtList<UtterText> utterTextList =  new DtList<>(UtterText.class);
            utterTextList.add(new UtterText());
            viewContext.publishDtListModifiable(utterTextsKey, utterTextList);
        } else {
            final SmallTalk smallTalk = smallTalkServices.findByTopId(topId).orElseThrow();
            addMessageDeactivate(uiMessageStack, smallTalk, viewContext.readDtListModifiable(nluTrainingSentencesKey, uiMessageStack), bot, topic);
            viewContext.publishDto(smallTalkKey, smallTalk);
            viewContext.publishDtListModifiable(buttonsKey, responsesButtonServices.getButtonsBySmalltalk(bot, smallTalk.getSmtId()));
             viewContext.publishDtListModifiable(buttonsUrlKey, responsesButtonUrlServices.getButtonsUrlBySmalltalk(bot, smallTalk.getSmtId()));
            final DtList<UtterText> utterTextList = utterTextServices.getUtterTextList(bot, smallTalk);
            utterTextList.add(new UtterText()); // add the next for random, or the 1st for rich text if 0 lines
            viewContext.publishDtListModifiable(utterTextsKey, utterTextList);
            viewContext.publishDto(scriptIntentionKey, scriptIntentionServices.getNewScriptIntention(bot));
        }

        viewContext.publishMdl(responseTypeKey, ResponseType.class, null);
        viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());

        super.initBreadCrums(viewContext, topic);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @GetMapping("/new")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {

        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);

        initContextNewTopic(viewContext, bot);

        viewContext.publishDto(smallTalkKey, smallTalkServices.getNewSmallTalk(bot));
        viewContext.publishDto(scriptIntentionKey, scriptIntentionServices.getNewScriptIntention(bot));
        viewContext.publishMdl(responseTypeKey, ResponseType.class, null);

        final DtList<UtterText> utterTextList = new DtList<>(UtterText.class);
        utterTextList.add(new UtterText()); // add the first one for initialization, list can't be empty
        viewContext.publishDtListModifiable(utterTextsKey, utterTextList);
        viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());

        viewContext.publishDtListModifiable(buttonsKey, new DtList<>(ResponseButton.class));
        viewContext.publishDtListModifiable(buttonsUrlKey, new DtList<>(ResponseButtonUrl.class));
        super.initEmptyBreadcrums(viewContext);
        listLimitReached(viewContext, uiMessageStack);
        toModeCreate();
    }

    private void initTopicContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, final Chatbot bot, final Topic topic) {
        Assertion.check().isTrue(topic.getBotId().equals(bot.getBotId()), "Incoherent parameters");

        viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBot(bot));
        viewContext.publishDto(topicKey, topic);
        viewContext.publishRef(newNluTrainingSentenceKey, "");
        final DtList<NluTrainingSentence> nluSentences = topicServices.getNluTrainingSentenceByTopic(bot, topic);
        viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluSentences);
        viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
                new DtList<NluTrainingSentence>(NluTrainingSentence.class));

        viewContext.publishDto(topicCategoryKey, topicCategoryServices.getTopicCategoryById(bot, topic.getTopCatId()));
        viewContext.publishDtList(topicCategoryListKey, topicCategoryServices.getAllActiveCategoriesByBot(bot));
        viewContext.publishDtList(contextValuesKey, contextValueServices.getAllContextValueByBotId(bot.getBotId()));
        viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfigServices.getChatbotCustomConfigByBotId(bot.getBotId()));

        //Label
        final DtList<TopicLabel> initialList = topicLabelServices.getTopicLabelByBotIdAndTopId(bot, topic.getTopId());
        viewContext.publishDtList(initialTopicLabelListKey, initialList);
        viewContext.publishDtListModifiable(topicLabelListKey, new DtList<>(TopicLabel.class));
        viewContext.publishDtList(allTopicLabelListKey, topicLabelServices.getTopicLabelByBotId(bot));
        viewContext.publishRef(unreachableKey, KindTopicEnum.UNREACHABLE.name().equals(topic.getKtoCd()));
    }

    public void initContextNewTopic(final ViewContext viewContext, final Chatbot bot) {
        viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBot(bot));
        viewContext.publishDto(topicKey, topicServices.getNewTopic(bot));

        viewContext.publishRef(newNluTrainingSentenceKey, "");
        viewContext.publishDtListModifiable(nluTrainingSentencesKey,
                new DtList<NluTrainingSentence>(NluTrainingSentence.class));
        viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
                new DtList<NluTrainingSentence>(NluTrainingSentence.class));

        viewContext.publishDto(topicCategoryKey, new TopicCategory());
        viewContext.publishDtList(topicCategoryListKey, topicCategoryServices.getAllActiveCategoriesByBot(bot));
        viewContext.publishDtList(contextValuesKey, contextValueServices.getAllContextValueByBotId(bot.getBotId()));
        viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfigServices.getChatbotCustomConfigByBotId(bot.getBotId()));

        //Labels
        viewContext.publishDtList(initialTopicLabelListKey, new DtList<>(TopicLabel.class));
        viewContext.publishDtListModifiable(topicLabelListKey, new DtList<>(TopicLabel.class));
        viewContext.publishDtList(allTopicLabelListKey, topicLabelServices.getTopicLabelByBotId(bot));
        viewContext.publishRef(unreachableKey, false);

    }

    private void addMessageDeactivate(final UiMessageStack uiMessageStack, final ScriptIntention scriptIntention,
                                      final DtList<NluTrainingSentence> sentences, final Chatbot chatbot, final Topic topic) {
        if (!topic.getIsTechnical() && scriptIntentionServices.hasToBeDeactivated(topic, sentences, scriptIntention, chatbot)) {
            uiMessageStack.info(scriptIntentionServices.getDeactivateMessage());
        }
    }

    private void addMessageDeactivate(final UiMessageStack uiMessageStack, final SmallTalk smallTalk,
                                      final DtList<NluTrainingSentence> sentences, final Chatbot chatbot, final Topic topic) {
        final SmallTalkWrapper smallTalkWrapper = new SmallTalkWrapper();
        smallTalkWrapper.setSmallTalk(smallTalk);
        if (!topic.getIsTechnical() && smallTalkServices.hasToBeDeactivated(topic, sentences, smallTalkWrapper, chatbot)) {
            uiMessageStack.info(smallTalkServices.getDeactivateMessage());
        }
    }

    @Override
    protected String getBreadCrums(final Topic object) {
        return object.getTitle();
    }

    @PostMapping("/_edit")
    public void doEdit(final ViewContext viewContext) {
        toModeEdit();
        // On locale change, a js reload is performed, and viewContext is reinitialized : locale value needs to be set again in viewContext.
        Locale locale = getUserSession().getLocale();
        viewContext.publishRef(localeKey, String.valueOf(locale));
    }

    @PostMapping("/_save")
    public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
                         @ViewAttribute("smallTalk") final SmallTalk smallTalk,
                         @ViewAttribute("scriptIntention") final ScriptIntention scriptIntention,
                         @ViewAttribute("topic") @Validate(TopicCategoryNotEmptyValidator.class) final Topic topic,
                         @ViewAttribute("bot") final Chatbot chatbot,
                         @ViewAttribute("unreachable") final String unreachable,
                         @ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
                         @ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
                         @ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
                         @ViewAttribute("topicLabelList") final DtList<TopicLabel> labels,
                         @ViewAttribute("initialTopicLabelList") final DtList<TopicLabel> initialLabels) {

        final Long botId = chatbot.getBotId();
        if (topic.getIsTechnical()) {
            if (TypeTopicEnum.SMALLTALK.name().equals(topic.getTtoCd())) {
                topicServices.saveBotTopic(chatbot, topic, ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(utterTextsKey), uiMessageStack).get(0).getText());
            } else {
                topicServices.saveBotTopic(chatbot, topic, scriptIntention.getScript());
            }
        } else {
            DataObject topicTypeObject = scriptIntention;
            if ("true".equals(unreachable)) {
                topic.setKtoCd(KindTopicEnum.UNREACHABLE.name());
            } else {
                topic.setKtoCd(KindTopicEnum.NORMAL.name());
                nluTrainingSentenceServices.addTrainingSentense(newNluTrainingSentence, nluTrainingSentences);
            }
            if (TypeTopicEnum.SMALLTALK.name().equals(topic.getTtoCd())) {
                final SmallTalkWrapper smallTalkWrapper = new SmallTalkWrapper();
                smallTalkWrapper.setSmallTalk(smallTalk);
                smallTalkWrapper.setUtterTexts(ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(utterTextsKey), uiMessageStack));
                smallTalkWrapper.setButtons(ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(buttonsKey), uiMessageStack));
                smallTalkWrapper.setButtonsUrl(ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(buttonsUrlKey), uiMessageStack));
                topicTypeObject = smallTalkWrapper;
            }

            topicServices.saveTopic(topic, chatbot, newNluTrainingSentence, nluTrainingSentences, nluTrainingSentencesToDelete, topicTypeObject, labels, initialLabels);
        }
        return "redirect:/bot/" + botId + "/topics/detail/" + topic.getTopId();
    }

    public static final class TopicCategoryNotEmptyValidator extends AbstractChatbotDtObjectValidator<Topic> {

        @Override
        protected List<DataFieldName<Topic>> getFieldsToNullCheck() {
            return List.of(TopicFields.topCatId);
        }
    }


    @PostMapping("/_addTrainingSentence")
    public ViewContext doAddTrainingSentence(final ViewContext viewContext,
                                             @ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentenceIn,
                                             @ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
                                             final UiMessageStack uiMessageStack) {

        nluTrainingSentenceServices.addTrainingSentense(newNluTrainingSentenceIn, nluTrainingSentences);

        viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);
        viewContext.publishRef(newNluTrainingSentenceKey, "");
        listLimitReached(viewContext, uiMessageStack);
        return viewContext;
    }

    @PostMapping("/_editTrainingSentence")
    public ViewContext doEditTrainingSentence(final ViewContext viewContext, @RequestParam("index") final int index,
                                              @ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
                                              @ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
                                              final UiMessageStack uiMessageStack) {

        if (StringUtil.isBlank(newNluTrainingSentence)) {
            // empty edit, rollback modification
            viewContext.markModifiedKeys(nluTrainingSentencesKey);
            return viewContext;
        }

        int curIdx = 0;
        for (final NluTrainingSentence nts : nluTrainingSentences) {
            if (curIdx == index) {
                nts.setText(newNluTrainingSentence);
            } else if (newNluTrainingSentence.equalsIgnoreCase(nts.getText())) {
                throw new VUserException(TopicsMultilingualResources.NLU_ALREADY_EXISTS);
            }
            curIdx++;
        }

        viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);
        listLimitReached(viewContext, uiMessageStack);

        return viewContext;
    }

    @PostMapping("/_removeTrainingSentence")
    public ViewContext doRemoveTrainingSentence(final ViewContext viewContext, @RequestParam("index") final int index,
                                                @ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
                                                @ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
                                                final UiMessageStack uiMessageStack) {

        // remove from list
        final NluTrainingSentence removed = nluTrainingSentences.remove(index);
        viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);

        // keep track of deleted persisted NluTrainingSentence
        if (removed.getNtsId() != null) {
            nluTrainingSentencesToDelete.add(removed);
        }
        viewContext.publishDtList(nluTrainingSentencesToDeleteKey, nluTrainingSentencesToDelete);
        listLimitReached(viewContext, uiMessageStack);

        return viewContext;
    }

    @PostMapping("/_delete")
    String doDelete(@ViewAttribute("bot") final Chatbot chatbot, @ViewAttribute("topic") final Topic topic) {
        if (topic.getIsTechnical()) {
            throw new VUserException(TopicsMultilingualResources.DELETE_TECHNICAL_ERROR);
        }
        final DtList<Topic> listTopicRef = topicServices.getTopicReferencingTopId(topic.getTopId());
        if (!listTopicRef.isEmpty()) {
            final String topicErrorList = listTopicRef.stream()
                    .map(Topic::getTitle)
                    .collect(Collectors.joining(", "));

            throw new VUserException(TopicsMultilingualResources.DELETION_REF_ERROR, topicErrorList);
        }
        topicServices.deleteTopic(topic, chatbot);

        return "redirect:/bot/" + topic.getBotId() + "/topics/";

    }
}
