package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;

import java.util.List;

import static io.vertigo.chatbot.engine.BotEngine.BOT_CONTEXT_KEY;

public class DescriptionFieldService implements IJiraFieldService, Component, Activeable {

	private ExecutorConfigManager executorConfigManager;

	@Override
	public boolean supports(String fieldKey) {
		return IssueFieldId.DESCRIPTION_FIELD.id.equals(fieldKey);
	}

	@Override
	public void processConversation(BlackBoard bb, JiraField jiraField, List<BTNode> sequence) {
		sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()));
	}

	@Override
	public void processTicket(BlackBoard bb, IssueInputBuilder iib, JiraField jiraField) {
		iib.setDescription(jiraField.getValue() + buildContextDescription(bb));
	}

	private String buildContextDescription(final BlackBoard bb) {
		StringBuilder stringBuilder = new StringBuilder("\n");
		executorConfigManager.getContextMap().forEach((key, value) -> {
			String bbValue = bb.getString(BBKey.of(BOT_CONTEXT_KEY,  "/" + key));
			if (bbValue != null) {
				stringBuilder.append(key).append(" : ").append(bbValue).append("\n");
			}
		});
		return stringBuilder.toString();
	}

	@Override
	public void start() {
		executorConfigManager = Node.getNode().getComponentSpace().resolve(ExecutorConfigManager.class);
	}

	@Override
	public void stop() {

	}
}
