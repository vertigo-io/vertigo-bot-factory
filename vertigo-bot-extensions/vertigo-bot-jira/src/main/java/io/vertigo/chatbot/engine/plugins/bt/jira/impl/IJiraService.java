package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import java.util.List;

public interface IJiraService {

	String createIssueJiraCommand(final List<String> jfStrings);

}
