package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.ai.bt.BTNodes.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTNodes;
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

		sequence.add(BotNodeProvider.inputString(bb, "/user/local/reference", "Bien sûr, quelle est la référence du client impacté par cette anomalie ?"));

		sequence.add(poursuiteSelector(bb, "/user/local/poursuite"));
		sequence.add(issueFromReferenceSelector(bb, "/user/local/reference", "/user/local/continue"));

		sequence.add(BotNodeProvider.inputString(bb, "/user/local/scenario", "Quel est le scénario de test ?"));
		sequence.add(BotNodeProvider.inputString(bb, "/user/local/attendu", "Quel est le résultat attendu ?"));
		sequence.add(BotNodeProvider.inputString(bb, "/user/local/obtenu", "Quel est le résultat obtenu?"));
		sequence.add(getReproButton(bb, "/user/local/reproductibilite", "Quelle est la reproductibilité ?"));
		sequence.add(getCriticiteButton(bb, "/user/local/criticite", "Quelle est la criticité ?"));
		sequence.add(getComponentIssue(bb, "/user/local/components", "Quel est le produit ?"));
		sequence.add(jiraIssueCreation(bb, jiraFields, urlSentence));

		return sequence(sequence);
	}

	public BTNode poursuiteSelector(final BlackBoard bb, final String keyTemplate) {
		final List<BTNode> sequence = new ArrayList<>();
		sequence.add(BTNodes.selector(
				BotNodeProvider.fulfilled(bb, keyTemplate),
				buildQuestionPoursuite(bb, keyTemplate)));
		sequence.add(BotNodeProvider.postConfirmation(bb, keyTemplate, BotNodeProvider.switchTopicEnd(bb)));
		return sequence(sequence);
	}

	public BTNode buildQuestionPoursuite(final BlackBoard bb, final String keyTemplate) {
		return () -> {
			final boolean isErrorTarifReduit = webServices.getIsErrorTarifReduit(bb.getString(BBKey.of("/user/local/reference")));

			bb.putString(BBKey.of(keyTemplate + "/call"), "done");
			if (isErrorTarifReduit) {
				final String question = "Le contrat NL+ du client ne dispose d'aucun profil tarifaire, ce qui n'est pas possible fonctionnellement. Êtes-vous sûr.e de vouloir poursuivre ?";
				return BotNodeProvider.askConfirmation(bb, keyTemplate, question, "Oui", "Non").eval();
			}
			return BTStatus.Succeeded;
		};
	}

	private BTNode issueFromReferenceSelector(final BlackBoard bb, final String keyReference, final String keyTemplate) {
		final List<BTNode> sequence = new ArrayList<>();
		sequence.add(selector(
				BotNodeProvider.fulfilled(bb, keyTemplate),
				getIssue(bb, keyReference, keyTemplate)));
		sequence.add(BotNodeProvider.postConfirmation(bb, keyTemplate, BotNodeProvider.switchTopicEnd(bb)));
		return sequence(sequence);

	}

	private BTNode getIssue(final BlackBoard bb, final String keyReference, final String keyTemplate) {
		return () -> {
			final List<String> result = new ArrayList<>();
			bb.putString(BBKey.of(keyTemplate + "/call"), "done");
			result.add("J'ai trouvé une anomalie qui porte déjà sur ce client.");
			result.add("Pourriez-vous vérifier que votre problème n'est pas déjà référencé ?");
			final String jqlSearch = "description ~ " + bb.getString(BBKey.of(keyReference));
			final List<String> jiraIssues = jiraService.getIssues(jqlSearch);
			if (jiraIssues != null) {
				result.addAll(jiraIssues);
			}
			if (result.size() > 2) {
				result.stream().forEach(x -> bb.listPush(BotEngine.BOT_RESPONSE_KEY, x));
				return BotNodeProvider.askConfirmation(bb, keyTemplate, "Voulez-vous continuer ?", "Oui", "Non").eval();
			}
			return BTStatus.Succeeded;
		};
	}

	private BTNode getCriticiteButton(final BlackBoard bb, final String keyTemplate, final String question) {
		final List<BotButton> buttons = new ArrayList<>();
		buttons.add(new BotButton("Critique", "10408"));
		buttons.add(new BotButton("Bloquant", "10409"));
		buttons.add(new BotButton("Majeur", "10410"));
		buttons.add(new BotButton("Mineur", "10411"));
		buttons.add(new BotButton("Esthétique", "10412"));
		return BotNodeProvider.chooseButton(bb, keyTemplate, question, buttons);
	}

	private BTNode getComponentIssue(final BlackBoard bb, final String keyTemplate, final String question) {
		return () -> {
			final List<BasicComponent> listComponents = (List<BasicComponent>) jiraService.getProject().getComponents();
			final List<BotButton> listButtons = listComponents.stream().map(x -> mapComponentToButtonNode(x)).collect(Collectors.toList());
			return BotNodeProvider.chooseButton(bb, keyTemplate, question, listButtons).eval();
		};
	}

	private BTNode getReproButton(final BlackBoard bb, final String keyTemplate, final String question) {
		final List<BotButton> buttons = new ArrayList<>();
		buttons.add(new BotButton("Reproductible", "10413"));
		buttons.add(new BotButton("Aléatoire", "10414"));
		buttons.add(new BotButton("Non reproductible", "10415"));
		buttons.add(new BotButton("N'a pas essayé", "10416"));
		return BotNodeProvider.chooseButton(bb, keyTemplate, question, buttons);
	}

	private BotButton mapComponentToButtonNode(final BasicComponent component) {
		return new BotButton(component.getName(), component.getName());
	}

}
