package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/category")
@Secured("BotUser")
public class TopicCategoryDetailController extends AbstractBotCreationController<TopicCategory> {

    private static final ViewContextKey<TopicCategory> topicCategoryKey = ViewContextKey.of("topicCategory");
    private static final ViewContextKey<TopicCategory> allTopicCategoriesKey = ViewContextKey.of("allTopicCategories");
    private static final ViewContextKey<Topic> topicsKey = ViewContextKey.of("topics");
    private static final ViewContextKey<Long> topicIdKey = ViewContextKey.of("topicId");
    private static final ViewContextKey<Long> newTopCatIdKey = ViewContextKey.of("newTopCatId");
    private static final ViewContextKey<Topic> otherCategoriesTopicsKey = ViewContextKey.of("otherCategoriesTopics");


    @GetMapping("/{topCatId}")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
                            @PathVariable("topCatId") final Long topCatId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final TopicCategory topicCategory = topicCategoryServices.getTopicCategoryById(bot, topCatId);
        final DtList<Topic> topics = topicCategoryServices.getAllTopicFromCategory(bot, topicCategory);
        final DtList<TopicCategory> topicCategories = topicCategoryServices.getAllCategoriesByBot(bot);
        final DtList<Topic> otherCategoriesTopics = topicServices.getAllTopicByBotIdExceptACategory(bot, topCatId);

        viewContext.publishDto(topicCategoryKey, topicCategory);
        viewContext.publishDtList(topicsKey, topics);
        viewContext.publishDtList(otherCategoriesTopicsKey, otherCategoriesTopics);
        viewContext.publishDtList(allTopicCategoriesKey, topicCategories);
        viewContext.publishRef(topicIdKey, 0L);
        viewContext.publishRef(newTopCatIdKey, 0L);

        super.initBreadCrums(viewContext, topicCategory);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @GetMapping("/new")
    public void getNewCategory(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        viewContext.publishDto(topicCategoryKey, topicCategoryServices.getNewTopicCategory(bot));
        viewContext.publishDtList(topicsKey, new DtList<Topic>(Topic.class));
        super.initEmptyBreadcrums(viewContext);
        listLimitReached(viewContext, uiMessageStack);
        toModeCreate();
    }

    @PostMapping("/_saveTopicCategoryChange")
    public ViewContext saveTopicCategoryChange(final ViewContext viewContext,
                                               final UiMessageStack uiMessageStack,
                                               @ViewAttribute("bot") final Chatbot bot,
                                               @ViewAttribute("topicCategory") final TopicCategory topicCategory,
                                               @ViewAttribute("topicId") final Long topicId,
                                               @ViewAttribute("newTopCatId") final Long newTopicCategoryId) {

        topicServices.saveCategoryChange(topicId, newTopicCategoryId, bot);

        final DtList<Topic> topics = topicCategoryServices.getAllTopicFromCategory(bot, topicCategory);

        viewContext.publishDtList(topicsKey, topics);
        viewContext.publishRef(topicIdKey, 0L);
        viewContext.publishRef(newTopCatIdKey, 0L);

        return viewContext;
    }

    @PostMapping("/_saveAddingTopics")
    public ViewContext saveAddingTopics(final ViewContext viewContext,
                                        final UiMessageStack uiMessageStack,
                                        @ViewAttribute("bot") final Chatbot bot,
                                        @ViewAttribute("topicCategory") final TopicCategory topicCategory,
                                        @ViewAttribute("topicId") final Long topicId) {


        final DtList<Topic> topics = topicCategoryServices.getAllTopicFromCategory(bot, topicCategory);

        viewContext.publishDtList(topicsKey, topics);

        return viewContext;
    }

    @PostMapping("/_save")
    public String saveTopicCategory(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("topicCategory") final TopicCategory category) {
        topicCategoryServices.saveCategory(bot, category);
        return "redirect:/bot/" + category.getBotId() + "/category/" + category.getTopCatId();
    }

    @PostMapping("/_delete")
    public String deleteTopicCategory(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("topicCategory") final TopicCategory category) {
        topicCategoryServices.deleteCategory(bot, category);
        return "redirect:/bot/" + bot.getBotId() + "/categories/";
    }

    @PostMapping("/_edit")
    public void doEdit() {
        toModeEdit();
    }

    @Override
    protected String getBreadCrums(final TopicCategory object) {
        return object.getLabel();
    }
}
