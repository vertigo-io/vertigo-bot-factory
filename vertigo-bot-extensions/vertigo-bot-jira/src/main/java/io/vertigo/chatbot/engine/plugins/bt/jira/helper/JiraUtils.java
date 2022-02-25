package io.vertigo.chatbot.engine.plugins.bt.jira.helper;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.core.locale.MessageText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.vertigo.ai.bt.BTNodes.selector;

public final class JiraUtils {

	public static final BBKey userLocalJira = BBKey.of(BotEngine.USER_LOCAL_PATH,  "/jira");
	public static final String yesPayload = "YES";
	public static final String noPayload = "NO";
	public static final BBKey continueBBPath = BBKey.of(userLocalJira, "/continue");
	public static final BBKey wsBBPath = BBKey.of(userLocalJira, "/ws");
	public static final BBKey reporterWsBBPath = BBKey.of(userLocalJira, "/reporterws");
	public static final BBKey assigneeWsBBPath = BBKey.of(userLocalJira, "/assigneews");
	public static final String wsValue = "done";

	private JiraUtils() {}

	public static Optional<JiraField> findIssueField(List<JiraField> fields, IssueFieldId issueFieldId) {
		return fields.stream().filter(jiraField -> jiraField.getFieldType().equals(issueFieldId.id))
				.findFirst();
	}

	public static JiraFieldSettingExport findJiraFieldSetting(List<JiraFieldSettingExport> jiraFieldSettingExports, IssueFieldId issueFieldId) {
		return jiraFieldSettingExports.stream()
				.filter(jiraFieldSettingExport -> jiraFieldSettingExport.getFieldKey().equals(issueFieldId.name())).findFirst().orElseThrow();
	}

	public static BTNode getVersionButtons(final BlackBoard bb, List<Version> versions, JiraField issueTypeField) {
		final List<BotButton> buttons = new ArrayList<>();
		versions.forEach(version -> buttons.add(new BotButton(version.getName(), version.getName())));
		return BotNodeProvider.chooseButton(bb, issueTypeField.getKey(), issueTypeField.getQuestion(), buttons);
	}

	public static BTNode getUserFromInput(final BlackBoard bb, JiraField jiraField, JiraServerService jiraServerService) {
		return selector(
				BotNodeProvider.fulfilled(bb, reporterWsBBPath.key()),
				getUser(bb, jiraField, jiraServerService));
	}

	public static BTNode getUser(final BlackBoard bb, JiraField jiraField, JiraServerService jiraServerService) {
		return () -> {
			bb.putString(reporterWsBBPath, wsValue);
			List<User> users = jiraServerService.findUserByUsername(bb.getString(BBKey.of(jiraField.getKey())));
			if (users.isEmpty()) {
				return BotNodeProvider.say(bb, MessageText.of(JiraMultilingualResources.NO_USER_FOUND).getDisplay()).eval();
			} else if (users.size() == 1) {
				bb.putString(BBKey.of(jiraField.getKey()), users.get(0).getName());
			} else {
				return getUserButtons(bb, users, jiraField).eval();
			}
			return BTStatus.Succeeded;
		};
	}

	public static BTNode getUserButtons(final BlackBoard bb, List<User> users, JiraField issueTypeField) {
		final List<BotButton> buttons = new ArrayList<>();
		users.forEach(user -> buttons.add(new BotButton(user.getDisplayName(), user.getName())));
		bb.delete(BBKeyPattern.of(issueTypeField.getKey()));
		return BotNodeProvider.chooseButton(bb, issueTypeField.getKey(), issueTypeField.getQuestion(), buttons);
	}
}
