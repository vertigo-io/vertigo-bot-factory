package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import static io.vertigo.ai.bt.BTNodes.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.WebService;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;

public class BotJiraNodeProvider implements Component {

	@Inject
	private JiraServerService jiraService;

	@Inject
	private WebService webServices;

public BTNode jiraIssueCreation(final BlackBoard bb, final List<String> jfStrings, final String urlSentence) {
		return () -> {
			List<String> versions = webServices.getAllVersions();
			final String result = jiraService.createIssueJiraCommand(jfStrings, versions);
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, urlSentence + " " + result);
			return BTStatus.Succeeded;
		};
	}

	public BTNode buildJiraCreateIssue(final BlackBoard bb, final List<JiraField> jiraFields, final String urlSentence) {
		final Predicate<String> validator = t -> !StringUtil.isBlank(t);
		final List<BTNode> sequence = new ArrayList<>();
		final List<String> jfStrings = new ArrayList<>();
		for (final JiraField jiraField : jiraFields) {
			sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion(), validator));
			jfStrings.add(bb.getString(BBKey.of(jiraField.getKey())));
		}
		sequence.add(jiraIssueCreation(bb, jfStrings, urlSentence));

		return sequence(sequence);

	}

}
