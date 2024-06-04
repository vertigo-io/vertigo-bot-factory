package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.continueBBPath;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.noPayload;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.wsBBPath;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.wsValue;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.yesPayload;

public class SummaryFieldService implements IJiraFieldService, Component {

	@Inject
	private JiraServerService jiraServerService;


	@Override
	public boolean supports(final String fieldKey) {
		return IssueFieldId.SUMMARY_FIELD.id.equals(fieldKey);
	}

	@Override
	public void processConversation(final BlackBoard bb, final JiraField jiraField, final List<BTNode> sequence) {

		sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()));
		if (jiraServerService.getNumberOfResults()>0) {
			sequence.add(getIssueFromReference(bb, jiraField.getKey()));
			if (noPayload.equals(bb.getString(continueBBPath))) {
				sequence.add(BotNodeProvider.switchTopicEnd(bb));
			}
		}
	}

	@Override
	public void processTicket(final BlackBoard bb, final IssueInputBuilder iib, final JiraField jiraField) {
		iib.setSummary(jiraField.getValue());
	}

	private BTNode getIssueFromReference(final BlackBoard bb, final String string) {
		return selector(
				BotNodeProvider.fulfilled(bb, wsBBPath.key()),
				getIssue(bb, string));
	}

	private BTNode getIssue(final BlackBoard bb, final String string) {
		return () -> {
			final List<String> result = new ArrayList<>();
			bb.putString(wsBBPath, wsValue);
			result.add(LocaleMessageText.of(JiraMultilingualResources.TICKET_FOUND).getDisplay());
			result.add(LocaleMessageText.of(JiraMultilingualResources.TICKET_CHECK_ALREADY_EXISTS).getDisplay());
			final String jqlSearch = "project = \"" + jiraServerService.getProjectName() + "\" AND (summary ~ \"" + bb.getString(BBKey.of(string)) + "\" OR description ~ \"" + bb.getString(BBKey.of(string)) + "\")";
			final List<String> jiraIssues = jiraServerService.getIssues(jqlSearch);
			final long numberOfResults = jiraServerService.getNumberOfResults();

			jiraIssues.stream().limit(numberOfResults).forEach(result::add);

			if (result.size() > 2) {
				result.forEach(x -> bb.listPush(BotEngine.BOT_RESPONSE_KEY, x));
				return getIssueButton(bb, continueBBPath.key(), LocaleMessageText.of(JiraMultilingualResources.ASK_CONTINUE).getDisplay()).eval();
			}
			return BTStatus.Succeeded;
		};
	}

	private BTNode getIssueButton(final BlackBoard bb, final String keyTemplate, final String question) {
		final List<BotButton> buttons = new ArrayList<>();
		buttons.add(new BotButton(LocaleMessageText.of(JiraMultilingualResources.YES).getDisplay(), yesPayload));
		buttons.add(new BotButton(LocaleMessageText.of(JiraMultilingualResources.NO).getDisplay(), noPayload));
		return BotNodeProvider.chooseButton(bb, keyTemplate, question, buttons);
	}
}
