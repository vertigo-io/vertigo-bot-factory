package io.vertigo.chatbot.designer.builder.services.questionanswer;

import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.questionanswer.QuestionAnswerDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextValueExport;
import io.vertigo.chatbot.commons.domain.QuestionAnswerExport;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerIhm;
import io.vertigo.chatbot.designer.builder.questionAnswer.QuestionAnswerPAO;
import io.vertigo.chatbot.designer.builder.topic.export.ExportPAO;
import io.vertigo.chatbot.designer.utils.SequenceNormalizer;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;
import static java.lang.Long.parseLong;

/**
 * Services de gestion des questions/reponses FAQ, y compris leur ordre d'affichage dans une categorie.
 *
 * @author Chatbot Team
 */
@Transactional
public class QuestionAnswerServices implements Component {


    @Inject
    private QuestionAnswerDAO questionAnswerDAO;

    @Inject
    private QuestionAnswerPAO questionAnswerPAO;

    @Inject
    private QuestionAnswerContextServices questionAnswerContextServices;

    @Inject
    private ExportPAO exportPAO;

    @Inject
    private JsonEngine jsonEngine;



    public DtList<QuestionAnswer> getAllQueAnsByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return questionAnswerDAO.findAll(Criterions.isEqualTo(DtDefinitions.QuestionAnswerFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<QuestionAnswer> getAllQueAnsByCatId(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        return getAllQueAnsByBot(bot).stream().filter(queAns -> queAns.getQaCatId().equals(categoryId))
                .sorted(Comparator.comparing(QuestionAnswer::getSequence)).collect(VCollectors.toDtList(QuestionAnswer.class));
    }

    public Optional<QuestionAnswer> getQueAnsByCode(final String code, final Chatbot bot) {
        final Criteria<QuestionAnswer> criteria = Criterions.isEqualTo(DtDefinitions.QuestionAnswerFields.code, code).and(Criterions.isEqualTo(DtDefinitions.QuestionAnswerFields.botId, bot.getBotId()));
        return questionAnswerDAO.findOptional(criteria);
    }

    public DtList<QuestionAnswerIhm> getAllQueAnsIhmByCatIdList(@SecuredOperation("botVisitor") final Chatbot bot, final List<Long> categoryIds) {
        final DtList<QuestionAnswerIhm> questionAnswerIhmList = getAllQueAnsIhmByBot(bot);
        final DtList<QuestionAnswerIhm> queAnsFromCategoryList = new DtList<>(QuestionAnswerIhm.class);
        categoryIds.forEach(catId -> queAnsFromCategoryList.addAll(questionAnswerIhmList.stream().filter(queAns -> queAns.getCatId().equals(catId)).collect(VCollectors.toDtList(QuestionAnswerIhm.class))));
        return queAnsFromCategoryList;
    }

    public DtList<QuestionAnswerIhm> getAllQueAnsIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return questionAnswerPAO.getAllQuestionAnswerIhmFromBot(bot.getBotId());
    }


    public DtList<QuestionAnswerIhm> getAllParsedQueAnsIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        final DtList<QuestionAnswerIhm> questionAnswerIhmList = questionAnswerPAO.getAllQuestionAnswerIhmFromBot(bot.getBotId());
        return parseQuestionAnswer(bot, questionAnswerIhmList);
    }

    public DtList<QuestionAnswerIhm> getAllQueAnsIhmByBotIdExceptACategory(final Chatbot bot, final Long categoryId) {
        final DtList<QuestionAnswerIhm> allQueAnsIhm = getAllQueAnsIhmByBot(bot);
        final DtList<QuestionAnswerIhm> filteredQueAnsIhmList = allQueAnsIhm.stream().filter(queAnsIhm -> !queAnsIhm.getCatId().equals(categoryId)).collect(VCollectors.toDtList(QuestionAnswerIhm.class));
        return parseQuestionAnswer(bot, filteredQueAnsIhmList);
    }

    public QuestionAnswer getQueAnsById(@SecuredOperation("botVisitor") final Chatbot bot , final Long questionAnswerId) {
        return questionAnswerDAO.get(questionAnswerId);
    }

    public QuestionAnswerIhm getQueAnsIhmById(@SecuredOperation("botVisitor") final Chatbot bot, final Long questionAnswerId) {
        return questionAnswerPAO.getQuestionAnswerIhmById(questionAnswerId);
    }

    public DtList<QuestionAnswerIhm> getQueAnsIhmByCatId(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        DtList<QuestionAnswerIhm> questionAnswerIhms = questionAnswerPAO.getAllQuestionAnswerIhmFromBot(bot.getBotId());
        questionAnswerIhms = questionAnswerIhms.stream().filter(queAns -> Objects.equals(queAns.getCatId(), categoryId)).collect(VCollectors.toDtList(QuestionAnswerIhm.class));
        return parseQuestionAnswer(bot, questionAnswerIhms);
    }

    public DtList<QuestionAnswerIhm> parseQuestionAnswer(@SecuredOperation("botVisitor") final Chatbot bot, final DtList<QuestionAnswerIhm> questionAnswerIhmList) {
        questionAnswerIhmList.forEach(questionAnswerIhm -> {
            questionAnswerIhm.setQuestion(Jsoup.parse(questionAnswerIhm.getQuestion()).text());
            questionAnswerIhm.setAnswer(Jsoup.parse(questionAnswerIhm.getAnswer()).text());
        });
        return questionAnswerIhmList;
    }

    /**
     * Supprime une question/reponse puis normalise les sequences restantes de sa categorie.
     *
     * @param bot le chatbot proprietaire (controle d'autorisation)
     * @param questionAnswerId l'identifiant de la question/reponse a supprimer
     */
    public void deleteQueAnsById(@SecuredOperation("botContributor") final Chatbot bot, final Long questionAnswerId) {
        final QuestionAnswer questionAnswer = questionAnswerDAO.get(questionAnswerId);
        final Long qaCatId = questionAnswer.getQaCatId();
        questionAnswerContextServices.deleteAllQuestionAnswerContextByQaId(bot, questionAnswerId);
        questionAnswerDAO.delete(questionAnswerId);
        normalizeQuestionAnswerSequences(bot, qaCatId);
    }

    /**
     * Deplace une question/reponse vers le haut ou le bas dans sa categorie en echangeant sa sequence avec celle de son voisin.
     *
     * @param bot le chatbot proprietaire de la question/reponse (controle d'autorisation)
     * @param qaId l'identifiant de la question/reponse a deplacer
     * @param moveUp true pour remonter l'element, false pour le descendre
     * @throws VUserException si la question/reponse n'a pas de voisin dans la direction demandee
     */
    public void moveQuestionAnswer(@SecuredOperation("botContributor") final Chatbot bot, final Long qaId, final boolean moveUp) {
        final QuestionAnswer questionAnswer = questionAnswerDAO.get(qaId);
        final QuestionAnswer neighbor;
        if (moveUp) {
            neighbor = questionAnswerDAO.findQueAnsPreviousNeighbor(questionAnswer.getQaCatId(), questionAnswer.getSequence());
        } else {
            neighbor = questionAnswerDAO.findQueAnsNextNeighbor(questionAnswer.getQaCatId(), questionAnswer.getSequence());
        }

        if (neighbor != null) {
            final Long tempSequence = questionAnswer.getSequence();
            questionAnswer.setSequence(neighbor.getSequence());
            neighbor.setSequence(tempSequence);
            questionAnswerDAO.save(questionAnswer);
            questionAnswerDAO.save(neighbor);
            normalizeQuestionAnswerSequences(bot, questionAnswer.getQaCatId());
        } else {
            throw new VUserException("Can't move question/answer " + qaId + " because it is out of sequence");
        }
    }

    /**
     * Reecrit les sequences des Q/R d'une categorie en 1..N uniques, en conservant l'ordre relatif
     * (sequence croissante, puis identifiant).
     *
     * @param bot le chatbot proprietaire (controle d'autorisation)
     * @param qaCatId l'identifiant de la categorie a normaliser
     */
    public void normalizeQuestionAnswerSequences(@SecuredOperation("botContributor") final Chatbot bot, final Long qaCatId) {
        if (qaCatId == null) {
            return;
        }
        final DtList<QuestionAnswer> questionAnswers = getAllQueAnsByCatId(bot, qaCatId);
        final boolean changed = SequenceNormalizer.applyDenseSequences(
                questionAnswers,
                QuestionAnswer::getSequence,
                QuestionAnswer::getQaId,
                QuestionAnswer::setSequence);
        if (changed) {
            questionAnswers.forEach(questionAnswerDAO::save);
        }
    }

    public void deleteAllQueAnsByBot(@SecuredOperation("botContributor") final Chatbot bot) {
        getAllQueAnsByBot(bot).forEach(questionAnswer -> deleteQueAnsById(bot, questionAnswer.getQaId()));
    }


    /**
     * Enregistre une question/reponse. Une creation recoit la prochaine sequence puis la categorie est normalisee.
     * Une mise a jour sans sequence conserve la valeur existante.
     *
     * @param bot le chatbot proprietaire (controle d'autorisation)
     * @param questionAnswer la question/reponse a persister
     */
    public void saveQuestionAnswer(@SecuredOperation("botContributor") final Chatbot bot, final QuestionAnswer questionAnswer) {
        final boolean isNew = questionAnswer.getQaId() == null;
        if (isNew) {
            questionAnswer.setSequence(questionAnswerPAO.getNextQueAnsSequence(questionAnswer.getQaCatId()));
        } else if (questionAnswer.getSequence() == null) {
            questionAnswer.setSequence(questionAnswerDAO.get(questionAnswer.getQaId()).getSequence());
        }
        questionAnswerDAO.save(questionAnswer);
        if (isNew) {
            normalizeQuestionAnswerSequences(bot, questionAnswer.getQaCatId());
        }
    }

    /**
     * Deplace une question/reponse vers une autre categorie et normalise les deux perimetres.
     *
     * @param bot le chatbot proprietaire (controle d'autorisation)
     * @param queAnsId l'identifiant de la question/reponse
     * @param queAnsCategoryId l'identifiant de la categorie cible
     */
    public void saveCategoryChange(@SecuredOperation("botContributor") final Chatbot bot, final Long queAnsId, final Long queAnsCategoryId) {
        final Long previousCatId = applyCategoryChange(queAnsId, queAnsCategoryId);
        normalizeQuestionAnswerSequences(bot, previousCatId);
        normalizeQuestionAnswerSequences(bot, queAnsCategoryId);
    }

    /**
     * Deplace plusieurs questions/reponses vers une categorie puis normalise chaque categorie impactee une seule fois.
     *
     * @param bot le chatbot proprietaire (controle d'autorisation)
     * @param queAnsIdsString identifiants separes par des virgules
     * @param queAnsCategoryId l'identifiant de la categorie cible
     */
    public void saveQueAnsCategoryChangesFromTopIdsString(@SecuredOperation("botContributor") final Chatbot bot, final String queAnsIdsString, final Long queAnsCategoryId) {
        final List<String> queAnsIdList = Arrays.asList(queAnsIdsString.split(","));
        final Set<Long> categoryIds = new LinkedHashSet<>();
        categoryIds.add(queAnsCategoryId);
        queAnsIdList.forEach(queAnsId -> categoryIds.add(applyCategoryChange(parseLong(queAnsId), queAnsCategoryId)));
        categoryIds.forEach(categoryId -> normalizeQuestionAnswerSequences(bot, categoryId));
    }

    /**
     * Change la categorie d'une question/reponse et lui attribue la prochaine sequence de la cible, sans normaliser.
     *
     * @param queAnsId l'identifiant de la question/reponse
     * @param queAnsCategoryId l'identifiant de la categorie cible
     * @return l'identifiant de l'ancienne categorie
     */
    private Long applyCategoryChange(final Long queAnsId, final Long queAnsCategoryId) {
        final QuestionAnswer questionAnswer = questionAnswerDAO.get(queAnsId);
        final Long previousCatId = questionAnswer.getQaCatId();
        questionAnswer.setQaCatId(queAnsCategoryId);
        questionAnswer.setSequence(questionAnswerPAO.getNextQueAnsSequence(queAnsCategoryId));
        questionAnswerDAO.update(questionAnswer);
        return previousCatId;
    }

    public QuestionAnswerIhm getNewQueAns(@SecuredOperation("botContributor") final Chatbot bot) {
        final QuestionAnswerIhm questionAnswerIhm = new QuestionAnswerIhm();
        questionAnswerIhm.setIsEnabled(true);
        questionAnswerIhm.setCode(UUID.randomUUID().toString());
        questionAnswerIhm.setQuestion("");
        questionAnswerIhm.setAnswer("");
        return questionAnswerIhm;
    }

    /**
     * Persiste une question/reponse depuis l'IHM. Une creation normalise ensuite les sequences de la categorie.
     *
     * @param bot le chatbot proprietaire (controle d'autorisation)
     * @param questionAnswerIhm donnee IHM a convertir
     * @return l'entite persistee
     */
    public QuestionAnswer saveQueAnsFromIhm(@SecuredOperation("botContributor") final Chatbot bot, final QuestionAnswerIhm questionAnswerIhm) {
        final QuestionAnswer questionAnswer = new  QuestionAnswer();
        questionAnswer.setQaId(questionAnswerIhm.getQaId());
        questionAnswer.setQuestion(questionAnswerIhm.getQuestion());
        questionAnswer.setAnswer(questionAnswerIhm.getAnswer());
        questionAnswer.setIsEnabled(questionAnswerIhm.getIsEnabled());
        questionAnswer.setQaCatId(questionAnswerIhm.getCatId());
        questionAnswer.setBotId(bot.getBotId());
        questionAnswer.setCode(questionAnswerIhm.getCode());
        final boolean isNew = questionAnswerIhm.getQaId() == null;
        if (isNew) {
            questionAnswer.setSequence(questionAnswerPAO.getNextQueAnsSequence(questionAnswerIhm.getCatId()));
        } else {
            questionAnswer.setSequence(questionAnswerDAO.get(questionAnswerIhm.getQaId()).getSequence());
        }
        final QuestionAnswer saved = questionAnswerDAO.save(questionAnswer);
        if (isNew) {
            normalizeQuestionAnswerSequences(bot, saved.getQaCatId());
        }
        return saved;
    }

    public String exportActiveQuestionsAnswers( final Chatbot bot, final StringBuilder logs) {
        LogsUtils.addLogs(logs, "Questions/Answers export...");
        final DtList<QuestionAnswerExport> questionAnswerExports = exportPAO.getAllActiveQuestionAnswerExportByBotId(bot.getBotId());
        questionAnswerExports.forEach(questionAnswer -> questionAnswer.setContextValues(
        questionAnswerContextServices.getAllQuestionAnswerContextByQaId(bot, questionAnswer.getQaId()).stream().map(questionAnswerContext -> {
            questionAnswerContext.contextValue().load();
            questionAnswerContext.contextPossibleValue().load();
            final ContextValueExport contextValueExport = new ContextValueExport();
            contextValueExport.setLabel(questionAnswerContext.contextValue().get().getLabel());
            contextValueExport.setValue(questionAnswerContext.contextPossibleValue().get().getValue());
            contextValueExport.setTyopCd(questionAnswerContext.contextPossibleValue().get().getTyopCd());
            return contextValueExport;
        }).collect(VCollectors.toDtList(ContextValueExport.class))));
        LogsUtils.logOK(logs);
        return jsonEngine.toJson(questionAnswerExports);
    }
}
