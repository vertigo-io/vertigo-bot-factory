package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import static io.vertigo.ai.bt.BTNodes.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.WebService;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Component;

public class BotJiraNodeProvider implements Component {

	@Inject
	private JiraServerService jiraService;

	@Inject
	private WebService webServices;

	public BTNode jiraIssueCreation(final BlackBoard bb, final List<JiraField> jiraFields, final String urlSentence) {
		return () -> {
			final List<String> jfStrings = jiraFields.stream().map(x -> bb.getString(BBKey.of(x.getKey()))).collect(Collectors.toList());

			jfStrings.add(bb.getString(BBKey.of("/user/local/components")));
			final List<String> versions = webServices.getAllVersions();
			final String result = jiraService.createIssueJiraCommand(jfStrings, versions);
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, urlSentence + " " + result);
			return BTStatus.Succeeded;
		};
	}

	public BTNode buildJiraCreateIssue(final BlackBoard bb, final List<JiraField> jiraFields, final String urlSentence) {
		final List<BTNode> sequence = new ArrayList<>();
		final List<String> jfStrings = new ArrayList<>();
		for (final JiraField jiraField : jiraFields) {
			sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()));
		}
		sequence.add(getComponentIssue(bb, "/user/local/components", "Quel est le composant ?"));

		sequence.add(jiraIssueCreation(bb, jiraFields, urlSentence));

		return sequence(sequence);

	}

	private BTNode getComponentIssue(final BlackBoard bb, final String keyTemplate, final String question) {
		return () -> {
			final List<BasicComponent> listComponents = (List<BasicComponent>) jiraService.getProject().getComponents();
			final List<BotButton> listButtons = listComponents.stream().map(x -> mapComponentToButtonNode(x)).collect(Collectors.toList());
			return BotNodeProvider.chooseButton(bb, keyTemplate, question, listButtons).eval();
		};
	}

	private BotButton mapComponentToButtonNode(final BasicComponent component) {
		return new BotButton(component.getName(), component.getName());
	}

	private BTNode getIssueFromReference(final BlackBoard bb, final String string) {
		return () -> {
			final List<String> result = new ArrayList<>();
			final List<BTNode> sequence = new ArrayList<>();
			result.add("J'ai trouvé une anomalie qui porte déjà sur ce client.");
			result.add("Pourriez-vous vérifier que votre problème n'est pas déjà référencé?");
			final String refClient = bb.getString(BBKey.of(string));
			final String jqlSearch = "description ~ " + bb.getString(BBKey.of(string));
			final List<String> jiraIssues = jiraService.getIssues(jqlSearch);
			if (jiraIssues != null) {
				result.addAll(jiraIssues);
			}
			if (result.size() > 2) {
				result.stream().forEach(x -> bb.listPush(BotEngine.BOT_RESPONSE_KEY, x));
				bb.getString(BBKey.of(jqlSearch));
			}
			return sequence(sequence).eval();
		};
	}

}
