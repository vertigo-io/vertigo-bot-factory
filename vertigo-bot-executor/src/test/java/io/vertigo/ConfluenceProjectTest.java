package io.vertigo;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.chatbot.executor.atlassian.impl.ConfluenceServerImpl;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSpace;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSpaceResponse;
import io.vertigo.chatbot.executor.atlassian.model.confluence.search.ConfluenceSearchOperator;
import io.vertigo.chatbot.executor.atlassian.model.confluence.search.MultipleConfluenceSearch;
import io.vertigo.chatbot.executor.atlassian.model.confluence.search.SingleConfluenceSearch;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.core.plugins.resource.url.URLResourceResolverPlugin;

public class ConfluenceProjectTest {

	private AutoCloseableNode node;

	@Inject
	private ParamManager paramManager;

	@Inject
	private ConfluenceServerImpl confluenceServerImpl;

	@Test
	public void testSpaceList() {
		final Map<String, String> headers = getHeadersWithAuthorization();
		final ConfluenceSpaceResponse responses = confluenceServerImpl.searchAllSpaceOnConfluence(headers);
		assertNotEquals(responses.getResults().length, 0);
		final String spaceKey = responses.getResults()[0].getKey();
		final ConfluenceSpace space = confluenceServerImpl.searchSpaceOnClonfluence(headers, spaceKey);
		assertNotEquals(space, null);
	}

	@Test
	public void testSearchInTitle() {
		final Map<String, String> headers = getHeadersWithAuthorization();

		//create cql search
		final SingleConfluenceSearch title1 = new SingleConfluenceSearch("title", ConfluenceSearchOperator.CONTAINS, "dossier");
		final SingleConfluenceSearch title2 = new SingleConfluenceSearch("title", ConfluenceSearchOperator.CONTAINS, "Etiquette");
		final MultipleConfluenceSearch title = new MultipleConfluenceSearch(title1, title2, ConfluenceSearchOperator.OR);
		final SingleConfluenceSearch type = new SingleConfluenceSearch("type", ConfluenceSearchOperator.EQUALS, "page");
		final MultipleConfluenceSearch search = new MultipleConfluenceSearch(type, title, ConfluenceSearchOperator.AND);

		//create other params
		final Map<String, String> params = new HashMap<>();
		params.put("limit", "10");

		final ConfluenceSearchResponse response = confluenceServerImpl.searchOnConfluence(params, headers, search);
		assertNotEquals(response.getSize(), 0);
	}

	@Test
	public void testSearchContentBody() {
		final Map<String, String> headers = getHeadersWithAuthorization();

		//create cql search
		final SingleConfluenceSearch type = new SingleConfluenceSearch("type", ConfluenceSearchOperator.EQUALS, "page");
		final SingleConfluenceSearch text = new SingleConfluenceSearch("text", ConfluenceSearchOperator.CONTAINS, "évolutions apportées");
		//create other params
		final Map<String, String> params = new HashMap<>();
		params.put("limit", "10");

		final ConfluenceSearchResponse response = confluenceServerImpl.searchOnConfluence(params, headers, new MultipleConfluenceSearch(type, text, ConfluenceSearchOperator.AND));
		assertNotEquals(response.getSize(), 0);
	}

	@Test
	public void testSearchLabelContent() {
		final Map<String, String> headers = getHeadersWithAuthorization();

		//create cql search
		final SingleConfluenceSearch type = new SingleConfluenceSearch("type", ConfluenceSearchOperator.EQUALS, "page");
		final SingleConfluenceSearch label = new SingleConfluenceSearch("label", ConfluenceSearchOperator.EQUALS, "csf");
		//create other params
		final Map<String, String> params = new HashMap<>();
		params.put("limit", "10");

		final ConfluenceSearchResponse response = confluenceServerImpl.searchOnConfluence(params, headers, new MultipleConfluenceSearch(type, label, ConfluenceSearchOperator.AND));
		assertNotEquals(response.getSize(), 0);
	}

	@Test
	public void testSearchTitleAndBodyContent() {
		final Map<String, String> headers = getHeadersWithAuthorization();

		//create cql search
		final SingleConfluenceSearch type = new SingleConfluenceSearch("type", ConfluenceSearchOperator.EQUALS, "page");

		final SingleConfluenceSearch title = new SingleConfluenceSearch("title", ConfluenceSearchOperator.CONTAINS, "csf");
		final SingleConfluenceSearch text = new SingleConfluenceSearch("text", ConfluenceSearchOperator.CONTAINS, "évolutions apportées");
		final MultipleConfluenceSearch textSearch = new MultipleConfluenceSearch(title, text, ConfluenceSearchOperator.AND);

		//create other params
		final Map<String, String> params = new HashMap<>();
		params.put("limit", "10");

		final ConfluenceSearchResponse response = confluenceServerImpl.searchOnConfluence(params, headers, new MultipleConfluenceSearch(type, textSearch, ConfluenceSearchOperator.AND));
		assertNotEquals(response.getSize(), 0);
	}

	@Test
	public void testSearchSentenceInTitle() {
		final Map<String, String> headers = getHeadersWithAuthorization();

		//create cql search
		final SingleConfluenceSearch type = new SingleConfluenceSearch("type", ConfluenceSearchOperator.EQUALS, "page");
		final SingleConfluenceSearch title = new SingleConfluenceSearch("title", ConfluenceSearchOperator.CONTAINS, "controle de service fait");

		//create other params
		final Map<String, String> params = new HashMap<>();
		params.put("limit", "10");

		final ConfluenceSearchResponse response = confluenceServerImpl.searchOnConfluence(params, headers, new MultipleConfluenceSearch(type, title, ConfluenceSearchOperator.AND));
		assertNotEquals(response.getSize(), 0);
	}

	private Map<String, String> getHeadersWithAuthorization() {
		final String password = paramManager.getOptionalParam("password").map(Param::getValue).orElseThrow(() -> new VSystemException("not params user found : use  -D"));
		final String user = paramManager.getOptionalParam("user").map(Param::getValue).orElseThrow(() -> new VSystemException("not params user found : use  -D"));
		final Map<String, String> result = new HashMap<>();
		result.put("Authorization", basicAuth(user, password));
		return result;
	}

	private String basicAuth(final String username, final String password) {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
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
						.addPlugin(CommandLineParamPlugin.class)
						.build())
				.addModule(
						ModuleConfig.builder("confluence")
								.addComponent(ConfluenceServerImpl.class)
								.build())
				.build();
	}
}
