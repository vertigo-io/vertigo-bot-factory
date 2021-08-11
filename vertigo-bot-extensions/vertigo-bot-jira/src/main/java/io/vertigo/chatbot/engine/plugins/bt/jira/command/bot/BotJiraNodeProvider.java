package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import static io.vertigo.ai.bt.BTNodes.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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
import io.vertigo.core.util.StringUtil;

public class BotJiraNodeProvider implements Component {

	@Inject
	private JiraServerService jiraService;

	@Inject
	private WebService webServices;

	public BTNode jiraIssueCreation(final BlackBoard bb, final List<String> jfStrings, final String urlSentence) {
		return () -> {
			final List<String> versions = webServices.getAllVersions();
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
			if (jiraField.getKey().equals("/user/local/reference")) {
				BTNode issues = getIssueFromReference(bb, "/user/local/reference");
				if (issues != null) {
					sequence.add(issues);
				}

			} else {
				sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion(), validator));
				jfStrings.add(bb.getString(BBKey.of(jiraField.getKey())));
			}

		}
		sequence.add(getComponentIssue(bb, "/user/local/components", "Quel est le composant ?"));
		jfStrings.add(bb.getString(BBKey.of("/user/local/components")));
		sequence.add(jiraIssueCreation(bb, jfStrings, urlSentence));

		return sequence(sequence);

	}

	private BTNode getComponentIssue(final BlackBoard bb, final String keyTemplate, final String question) {
		final List<BasicComponent> listComponents = (List<BasicComponent>) jiraService.getProject().getComponents();
		final List<BotButton> listButtons = listComponents.stream().map(x -> mapComponentToButtonNode(x)).collect(Collectors.toList());
		return BotNodeProvider.chooseButton(bb, keyTemplate, question, listButtons);
	}

	private BotButton mapComponentToButtonNode(final BasicComponent component) {
		return new BotButton(component.getName(), component.getName());
	}

	private BTNode getIssueFromReference(final BlackBoard bb, final String string) {
		final List<String> result = new ArrayList<>();
		result.add("J'ai trouvé une anomalie qui porte déjà sur ce client.");
		result.add("Pourriez-vous vérifier que votre problème n'est pas déjà référencé?");
		final String refClient = bb.getString(BBKey.of(string));
		if (refClient != null) {
			final String jqlSearch = "description ~ " + bb.getString(BBKey.of(string));
			result.addAll(jiraService.getIssues(jqlSearch));
			if (result.size() > 2) {
				final List<BTNode> sequence = new ArrayList<>();
				result.stream().forEach(x -> bb.listPush(BotEngine.BOT_RESPONSE_KEY, x));
				return sequence(sequence);
			}
		}
		return null;
	}

}
