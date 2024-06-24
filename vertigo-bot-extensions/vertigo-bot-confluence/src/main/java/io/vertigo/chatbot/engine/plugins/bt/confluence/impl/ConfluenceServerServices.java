package io.vertigo.chatbot.engine.plugins.bt.confluence.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.analytics.AnalyticsObjectSend;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.domain.ConfluenceSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceSearchHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.JsonHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchLinks;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResult;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpace;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpaceResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceView;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchObject;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;

import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.API_URL;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.CONTENT_URL;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.PAGE_BODY;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.SEARCH_URL;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.SPACE_URL;

@Transactional
public class ConfluenceServerServices implements IConfluenceService, Component {

	private String baseUrl;
	private String user;
	private String password;
	private String limit;
	private List<String> spaces;

	@Inject
	private PasswordEncryptionServices passwordEncryptionServices;

	public void refreshConfig(final ExecutorGlobalConfig config, StringBuilder logs) throws VSystemException {
		LogsUtils.addLogs(logs, "Refreshing Confluence settings ... ");
		final ConfluenceSettingExport confluenceSettingExport = config.getBot().getConfluenceSetting();
		if (confluenceSettingExport == null) {
			LogsUtils.logKO(logs);
			throw new VSystemException("Confluence setting must be set for confluence plugin to work...");
		} else {
			baseUrl = confluenceSettingExport.getUrl();
			user = confluenceSettingExport.getLogin();
			password = passwordEncryptionServices.decryptPassword(confluenceSettingExport.getPassword());
			limit = confluenceSettingExport.getNumberOfResults().toString();
			spaces = confluenceSettingExport.getSpaces();
			LogsUtils.logOK(logs);
		}
	}

	@Override
	public ConfluenceSearchResponse searchOnConfluence(final Map<String, String> params, final Map<String, String> headers, final ConfluenceSearchObject filter) {
		final String searchArgs = ConfluenceHttpRequestHelper.createSearchArgs(filter, true, spaces);
		params.put("cql", searchArgs);
		params.put("limit", limit);
		final HttpResponse<String> response = sendRequestToConfluence(params, headers, SEARCH_URL, 200, BodyHandlers.ofString());
		return JsonHelper.getObjectFromJson(response.body(), ConfluenceSearchResponse.class);
	}

	@Override
	public ConfluenceSpaceResponse searchAllSpaceOnConfluence(final Map<String, String> headers) {
		final HttpResponse<String> response = sendRequestToConfluence(null, headers, SPACE_URL, 200, BodyHandlers.ofString());
		return JsonHelper.getObjectFromJson(response.body(), ConfluenceSpaceResponse.class);
	}

	@Override
	public ConfluenceSpace searchSpaceOnClonfluence(final Map<String, String> headers, final String spaceKey) {
		final HttpResponse<String> response = sendRequestToConfluence(null, headers, SPACE_URL + spaceKey, 200, BodyHandlers.ofString());
		return JsonHelper.getObjectFromJson(response.body(), ConfluenceSpace.class);
	}

	private <T> HttpResponse<T> sendRequestToConfluence(final Map<String, String> params, final Map<String, String> headers, final String endUrl, final int successCode, final BodyHandler<T> handler) {
		final var apiUrl = getApiUrl();
		final var request = ConfluenceHttpRequestHelper.createGetRequest(apiUrl + endUrl, headers, params);
		return ConfluenceHttpRequestHelper.sendRequest(null, request, handler, successCode);
	}

	private String getBaseUrl() {
		return baseUrl;
	}

	private String getApiUrl() {
		return baseUrl + API_URL;
	}

	private Map<String, String> getHeadersWithAuthorization() {
		return ConfluenceHttpRequestHelper.getHeadersWithAuthorization(user, password);
	}

	private String removeTagWithClass(String html, String tagName, String className) {
		// Pattern pour rechercher et remplacer la balise avec la classe spécifiée
		String regex = "<" + tagName + "\\s+class=\"" + className + "\".*?>.*?</" + tagName + ">";
		return html.replaceAll(regex, "");
	}

	@Override
	public List<String> searchOnConfluenceCommand(final String search) {
		final Map<String, String> headers = getHeadersWithAuthorization();
		final Map<String, String> params = new HashMap<>();
		final var searchObject = ConfluenceSearchHelper.createConfluenceSearchObject(search);
		final ConfluenceSearchResponse searchResult = searchOnConfluence(params, headers, searchObject);
		final List<ConfluenceSearchResult> results = Arrays.asList(searchResult.getResults());

		// map of pageId (pageName -> pageId)
		Map<String, String> pageIds = results.stream().collect(Collectors.toMap(result -> result.getDetail().getTitle(), result -> result.getDetail().getId()));

		// map of pageBody (pageName -> pageBody)
		Map<String, HttpResponse<String>> responses = pageIds.entrySet().stream()
				.collect(Collectors.toMap(
                        Map.Entry::getKey,
						entry -> sendRequestToConfluence(null, headers, CONTENT_URL + entry.getValue() + PAGE_BODY, 200, BodyHandlers.ofString())
				));

		// map of html (pageName -> html)
		Map<String, String> responsesHtml = getHtmlFromPageHttpResponse(responses);

		return responsesHtml.entrySet().stream().map(entry -> addingTitleToHtml(entry.getKey(), entry.getValue())).collect(Collectors.toList());

		//Option to create clickable links instead of documents
		//return results.stream().map(x -> createLinkUrl(x.getUrl(), x.getDetail().getTitle())).collect(Collectors.toList());
	}

	Map<String, String> getHtmlFromPageHttpResponse (Map <String, HttpResponse<String>> responses) {
        return responses.entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> {
					HttpResponse<String> response = entry.getValue();
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						JsonNode jsonNode = objectMapper.readTree(response.body());
						JsonNode body = jsonNode.get("body");
						ConfluenceView view = objectMapper.readValue(body.get("view").toString(), ConfluenceView.class);
						return view.getValue();
					} catch (Exception e) {
						return "Erreur lors de la lecture du JSON pour la page " + entry.getKey() + " : " + e.getMessage();
					}
				}
		));
	}

	private String createLinkUrl(final String link, final String name) {
		final String url = getBaseUrl() + link;
		final var builder = new StringBuilder();
		builder.append("<a class='imgClass' href=\"");
		builder.append(url);
		builder.append("\" target=\"_blank\" rel=\"noopener noreferrer\">");
		builder.append(name);
		builder.append("</a>");
		return builder.toString();
	}

	private String addingTitleToHtml(final String name, final String html) {
		final var builder = new StringBuilder();
		String escapedHtml = html.replace("\"", "&quot;");
		builder.append("<div class='imgClass' data-html=\"");
		builder.append("<div>");
		builder.append("<h1 style='text-align: center; font-weight: bold;'>");
		builder.append(name);
		builder.append("</h1>");
		builder.append(escapedHtml);
		builder.append("</div>");
		builder.append("\">");
		builder.append(name);
		builder.append("</div>");
		return builder.toString();
	}
}
