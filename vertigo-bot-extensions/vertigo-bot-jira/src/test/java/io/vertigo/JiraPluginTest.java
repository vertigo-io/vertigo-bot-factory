package io.vertigo;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;

import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.core.plugins.resource.url.URLResourceResolverPlugin;

public class JiraPluginTest {

	private AutoCloseableNode node;

	@Inject
	private JiraServerService jiraServerService;

	@Test
	void testSpaceList() {
		BasicIssue issue = jiraServerService.createRequestJira();
		assertNotEquals(issue.getId(), null);
	}

	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
	}

	@AfterEach
	public final void tearDown() {
		if (node != null) {
			node.close();
		}
	}

	private NodeConfig buildNodeConfig() {
		return NodeConfig.builder().withBoot(
				BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.addPlugin(URLResourceResolverPlugin.class)
						.build())
				.addModule(
						ModuleConfig.builder("confluence")
								.addComponent(JiraServerService.class)
								.build())
				.build();
	}
}
