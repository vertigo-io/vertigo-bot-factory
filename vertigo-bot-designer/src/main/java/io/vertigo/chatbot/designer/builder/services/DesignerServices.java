/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.designer.builder.services;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.dao.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.ResponseButtonDAO;
import io.vertigo.chatbot.commons.dao.SmallTalkDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.commons.DesignerUserSession;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotNodeFields;
import io.vertigo.chatbot.domain.DtDefinitions.NluTrainingSentenceFields;
import io.vertigo.chatbot.domain.DtDefinitions.ResponseButtonFields;
import io.vertigo.chatbot.domain.DtDefinitions.SmallTalkFields;
import io.vertigo.chatbot.domain.DtDefinitions.UtterTextFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.core.locale.MessageText;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.FileInfoURI;
import io.vertigo.dynamo.domain.model.ListVAccessor;
import io.vertigo.dynamo.domain.util.VCollectors;
import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.Assertion;
import io.vertigo.util.StringUtil;

@Secured("AdmBot")
@Transactional
public class DesignerServices implements Component {

	@Inject
	private FileServices fileServices;

	@Inject
	private FileManager fileManager;

	@Inject
	private ChatbotDAO chatbotDAO;

	@Inject
	private SmallTalkDAO smallTalkDAO;

	@Inject
	private NluTrainingSentenceDAO nluTrainingSentenceDAO;

	@Inject
	private ResponseButtonDAO responseButtonDAO;

	@Inject
	private UtterTextDAO utterTextDAO;

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private AuthorizationManager authorizationManager;

	@Inject
	private VSecurityManager securityManager;

	public DtList<Chatbot> getMySupervisedChatbots() {
		if (authorizationManager.hasAuthorization(GlobalAuthorizations.AtzSuperAdmBot)) {
			return getAllChatbots();
		} else {
			final ListVAccessor<Chatbot> chatbotLoader = getUserSession().getLoggedPerson().chatbots();
			chatbotLoader.load();
			return chatbotLoader.get();
		}
	}

	@Secured("SuperAdmBot")
	public DtList<Chatbot> getAllChatbots() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	@Secured("SuperAdmBot")
	public Chatbot getNewChatbot() {
		final Chatbot newChatbot = new Chatbot();
		newChatbot.setCreationDate(LocalDate.now());

		return newChatbot;
	}

	public Chatbot getChatbotById(final Long botId) {
		Assertion.checkNotNull(botId);
		// ---
		final Chatbot chatbot = chatbotDAO.get(botId);
		checkRights(chatbot, ChatbotOperations.read);
		return chatbot;
	}

	public VFile getAvatar(final Chatbot bot) {
		if (bot.getFilIdAvatar() == null) {
			return getNoAvatar();
		}
		return fileServices.getFile(bot.getFilIdAvatar());
	}

	public VFile getNoAvatar() {
		return fileManager.createFile(
				"noAvatar.png",
				"image/png",
				DesignerServices.class.getResource("/noAvatar.png"));
	}

	public UtterText getDefaultTextByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdDefault());
	}

	public UtterText getWelcomeTextByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdWelcome());
	}

	public Chatbot saveChatbot(@SecuredOperation("write") final Chatbot chatbot, final Optional<FileInfoURI> personPictureFile,
			final UtterText defaultText, final DtList<ResponseButton> defaultButtons,
			final UtterText welcomeText, final DtList<ResponseButton> welcomeButtons) {

		Assertion.checkNotNull(chatbot);
		Assertion.checkNotNull(defaultText);
		Assertion.checkNotNull(defaultButtons);
		Assertion.checkNotNull(welcomeText);
		Assertion.checkNotNull(welcomeButtons);
		// ---

		// default text
		utterTextDAO.save(defaultText);
		chatbot.setUttIdDefault(defaultText.getUttId());

		// welcome
		utterTextDAO.save(welcomeText);
		chatbot.setUttIdWelcome(welcomeText.getUttId());

		// Avatar
		Long oldAvatar = null;
		if (personPictureFile.isPresent()) {
			oldAvatar = chatbot.getFilIdAvatar();
			final VFile fileTmp = fileServices.getFileTmp(personPictureFile.get());
			final FileInfoURI fileInfoUri = fileServices.saveFile(fileTmp);
			chatbot.setFilIdAvatar((Long) fileInfoUri.getKey());
		}

		// chatbot save
		chatbot.setStatus("OK");
		final Chatbot savedChatbot = chatbotDAO.save(chatbot);

		// clean old avatar
		if (oldAvatar != null) {
			fileServices.deleteFile(oldAvatar);
		}

		// clear old buttons
		builderPAO.removeAllButtonsByBotId(chatbot.getBotId());

		// save new buttons
		for (final ResponseButton btn : defaultButtons) {
			btn.setBtnId(null); // force creation
			btn.setBotIdDefault(chatbot.getBotId());
			responseButtonDAO.save(btn);
		}

		for (final ResponseButton btn : welcomeButtons) {
			btn.setBtnId(null); // force creation
			btn.setBotIdWelcome(chatbot.getBotId());
			responseButtonDAO.save(btn);
		}

		return savedChatbot;
	}

	public SmallTalk getSmallTalkById(final Long movId) {
		Assertion.checkNotNull(movId);
		// ---
		return smallTalkDAO.get(movId);
	}

	public DtList<SmallTalk> getAllSmallTalksByBotId(final Long botId) {
		return smallTalkDAO.findAll(Criterions.isEqualTo(SmallTalkFields.botId, botId), DtListState.of(1000));
	}

	public SmallTalk getNewSmallTalk(final Long botId) {
		final SmallTalk smallTalk = new SmallTalk();
		smallTalk.setBotId(botId);
		smallTalk.setIsEnabled(true);
		smallTalk.responseType().setEnumValue(ResponseTypeEnum.RICH_TEXT);
		return smallTalk;
	}

	public SmallTalk saveSmallTalk(final SmallTalk smallTalk,
			final DtList<NluTrainingSentence> nluTrainingSentences, final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			final DtList<UtterText> utterTexts, final DtList<ResponseButton> buttonList) {

		Assertion.checkNotNull(smallTalk);
		Assertion.checkNotNull(nluTrainingSentences);
		Assertion.checkNotNull(nluTrainingSentencesToDelete);
		Assertion.checkNotNull(utterTexts);
		Assertion.checkNotNull(buttonList);
		// ---

		SmallTalk savedST = smallTalkDAO.save(smallTalk);

		// save nlu textes
		final DtList<NluTrainingSentence> ntsToSave = nluTrainingSentences.stream()
				.filter(nts -> !StringUtil.isEmpty(nts.getText()))
				.collect(VCollectors.toDtList(NluTrainingSentence.class));

		for (final NluTrainingSentence nts : ntsToSave) {
			nts.setSmtId(savedST.getSmtId());
			nluTrainingSentenceDAO.save(nts);
		}

		nluTrainingSentencesToDelete.stream()
				.filter(itt -> itt.getNtsId() != null)
				.forEach(itt -> nluTrainingSentenceDAO.delete(itt.getNtsId()));

		// save utter textes, remove all + create all
		builderPAO.removeAllUtterTextBySmtId(savedST.getSmtId());

		Stream<UtterText> utterStream = utterTexts.stream();
		if (ResponseTypeEnum.RICH_TEXT.equals(smallTalk.responseType().getEnumValue())) {
			utterStream = utterStream.limit(1);
		}

		final DtList<UtterText> uttToSave = utterStream
				.filter(utt -> !StringUtil.isEmpty(utt.getText()))
				.collect(VCollectors.toDtList(UtterText.class));

		for (final UtterText utt : uttToSave) {
			utt.setUttId(null); // force creation
			utt.setSmtId(savedST.getSmtId());
			utt.setText(sanatizeHtml(utt.getText()));
			utterTextDAO.save(utt);
		}

		if (ntsToSave.isEmpty() || uttToSave.isEmpty()) {
			// no training or response, disable this small talk
			savedST.setIsEnabled(false);
			savedST = smallTalkDAO.save(savedST);
		}

		// clear old buttons
		builderPAO.removeAllButtonsBySmtId(savedST.getSmtId());

		// save new buttons
		for (final ResponseButton btn : buttonList) {
			btn.setBtnId(null); // force creation
			btn.setSmtId(savedST.getSmtId());
			responseButtonDAO.save(btn);
		}

		return savedST;
	}

	private String sanatizeHtml(final String in) {
		final PolicyFactory sanitizer = Sanitizers.FORMATTING
				.and(Sanitizers.BLOCKS)
				.and(Sanitizers.LINKS)
				.and(Sanitizers.STYLES)
				.and(new HtmlPolicyBuilder()
						.allowElements("font", "hr")
						.allowAttributes("size").onElements("font")
						.allowElements( // force target _blank https://github.com/OWASP/java-html-sanitizer/issues/147
								(elementName, attrs) -> {
									final int targetIndex = attrs.indexOf("target");
									if (targetIndex < 0) {
										attrs.add("target");
										attrs.add("_blank");
									} else {
										attrs.set(targetIndex + 1, "_blank");
									}
									return elementName;
								},
								"a")
						.toFactory());

		return sanitizer.sanitize(in);
	}

	public void deleteSmallTalk(final SmallTalk smallTalk) {
		// delete sub elements
		for (final NluTrainingSentence its : getNluTrainingSentenceList(smallTalk)) {
			nluTrainingSentenceDAO.delete(its.getUID());
		}

		for (final UtterText ut : getUtterTextList(smallTalk)) {
			utterTextDAO.delete(ut.getUID());
		}

		// delete smallTalk
		smallTalkDAO.delete(smallTalk.getUID());
	}

	public DtList<NluTrainingSentence> getNluTrainingSentenceList(final SmallTalk smallTalk) {
		Assertion.checkNotNull(smallTalk);
		Assertion.checkNotNull(smallTalk.getSmtId());
		// ---

		return nluTrainingSentenceDAO.findAll(
				Criterions.isEqualTo(NluTrainingSentenceFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, NluTrainingSentenceFields.ntsId.name(), false));
	}

	public DtList<UtterText> getUtterTextList(final SmallTalk smallTalk) {
		Assertion.checkNotNull(smallTalk);
		Assertion.checkNotNull(smallTalk.getSmtId());
		// ---
		return utterTextDAO.findAll(
				Criterions.isEqualTo(UtterTextFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, UtterTextFields.uttId.name(), false));
	}

	public DtList<ResponseButton> getWelcomeButtonsByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		Assertion.checkNotNull(bot.getBotId());
		// ---

		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.botIdWelcome, bot.getBotId()),
				DtListState.of(1000, 0, ResponseButtonFields.btnId.name(), false));
	}

	public DtList<ResponseButton> getDefaultButtonsByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		Assertion.checkNotNull(bot.getBotId());
		// ---

		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.botIdDefault, bot.getBotId()),
				DtListState.of(1000, 0, ResponseButtonFields.btnId.name(), false));
	}

	public DtList<ResponseButton> getButtonsBySmalltalk(final SmallTalk smallTalk) {
		Assertion.checkNotNull(smallTalk);
		Assertion.checkNotNull(smallTalk.getSmtId());
		// ---

		return responseButtonDAO.findAll(
				Criterions.isEqualTo(ResponseButtonFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, ResponseButtonFields.btnId.name(), false));
	}

	public DtList<ChatbotNode> getAllNodesByBotId(final Long botId) {
		return chatbotNodeDAO.findAll(Criterions.isEqualTo(ChatbotNodeFields.botId, botId), DtListState.of(100));
	}

	public Optional<ChatbotNode> getDevNodeByBotId(final Long botId) {
		return chatbotNodeDAO.findOptional(
				Criterions.isEqualTo(ChatbotNodeFields.botId, botId)
						.and(Criterions.isEqualTo(ChatbotNodeFields.isDev, true)));
	}

	public void saveNode(final ChatbotNode node) {
		if (node.getNodId() != null) {
			// enforce previous values
			final ChatbotNode previousValues = chatbotNodeDAO.get(node.getNodId());

			node.setBotId(previousValues.getBotId());
			node.setTraId(previousValues.getTraId());
		}

		if (Boolean.TRUE.equals(node.getIsDev())) {
			// enforce only one dev node
			builderPAO.resetDevNode(node.getBotId());
		}

		chatbotNodeDAO.save(node);
	}

	public void deleteNode(final Long nodId) {
		chatbotNodeDAO.delete(nodId);
	}

	private DesignerUserSession getUserSession() {
		return securityManager.<DesignerUserSession>getCurrentUserSession().get();
	}

	private void checkRights(final Chatbot chatbot, final ChatbotOperations chatbotOperation) {
		if (!authorizationManager.isAuthorized(chatbot, chatbotOperation)) {
			throw new VSecurityException(MessageText.of("Not enought authorizations"));//no too sharp info here : may use log
		}
	}
}
