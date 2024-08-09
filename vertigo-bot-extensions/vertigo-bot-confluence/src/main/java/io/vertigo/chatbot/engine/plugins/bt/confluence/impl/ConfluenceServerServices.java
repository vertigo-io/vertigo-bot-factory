package io.vertigo.chatbot.engine.plugins.bt.confluence.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.HtmlInputUtils;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.domain.ConfluenceSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceSearchHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.JsonHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResult;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpace;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpaceResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceView;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchObject;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;

import static io.vertigo.chatbot.commons.FileUtils.formatImageToBase64String;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.API_URL;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.CONTENT_URL;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.PAGE_BODY;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.SEARCH_URL;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper.SPACE_URL;
import static io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources.LINK_TO_PAGE;

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

	public HttpResponse<InputStream> searchImageOnConfluence(final String url, final Map<String, String> headers) {
		final var request = ConfluenceHttpRequestHelper.createGetRequest(url, headers, null);
		return ConfluenceHttpRequestHelper.sendRequest(null, request, BodyHandlers.ofInputStream(), 200);
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

	@Override
	public List<String> searchOnConfluenceCommand(final String search) {
		final Map<String, String> headers = getHeadersWithAuthorization();
		final Map<String, String> params = new HashMap<>();
		final var searchObject = ConfluenceSearchHelper.createConfluenceSearchObject(search);
		final ConfluenceSearchResponse searchResult = searchOnConfluence(params, headers, searchObject);
		final List<ConfluenceSearchResult> results = Arrays.asList(searchResult.getResults());

        return results.stream().map(result -> formatHtml(result.getDetail().getTitle(),
                getBaseUrl() + result.getUrl(),
                getHtmlFromPageHttpResponse(result.getDetail().getTitle(),
                        sendRequestToConfluence(null, headers, CONTENT_URL + result.getDetail().getId() + PAGE_BODY, 200, BodyHandlers.ofString()))))
				.collect(Collectors.toList());

	}

	public String getHtmlFromPageHttpResponse (String pageTitle, HttpResponse<String> response) {
       try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response.body());
			JsonNode body = jsonNode.get("body");
			ConfluenceView view = objectMapper.readValue(body.get("view").toString(), ConfluenceView.class);
			return view.getValue();
	   } catch (Exception e) {
			return "Erreur lors de la lecture du JSON pour la page " + pageTitle + " : " + e.getMessage();
	   }
	}

	private String getHtmlWithImages(String html) {
		Map<String, String> headers = getHeadersWithAuthorization();

		Document document = Jsoup.parse(html);
		Elements imgElements = document.select("img");
		for (Element imgElement : imgElements) {
			String src = imgElement.attr("src");
			String imageBase64 = formatImageToBase64String(searchImageOnConfluence(baseUrl + src, headers));
			imgElement.attr("src", imageBase64);
		}
		return document.html();
	}

	private String formatHtml(final String name, final String url, final String html){
		final var builder = new StringBuilder();
		String cleanHtml = HtmlInputUtils.sanitizeHtml(html, true);
		String htmlWithImages = getHtmlWithImages(cleanHtml);
		String escapedHtml = htmlWithImages.replace("\"", "&quot;");

		builder.append("<div class='htmlClass' style='color:blue; text-decoration:underline; cursor:pointer;'");
		builder.append("data-html=\"");
		builder.append("<div>");
		builder.append("<h1 style='text-align: center; font-weight: bold;'>");
		builder.append(name);
		builder.append("</h1>");
		builder.append("<div style='text-align: center; padding-bottom:3%;'><a target='_blank' href='");
		builder.append(url);
		builder.append("'>");
		builder.append(MessageText.of(LINK_TO_PAGE).getDisplay());
		builder.append("</a></div>");
		builder.append(escapedHtml);
		builder.append("</div>");
		builder.append("\">");
		builder.append(name);
		builder.append("</div>");
		return builder.toString();
	}
}
