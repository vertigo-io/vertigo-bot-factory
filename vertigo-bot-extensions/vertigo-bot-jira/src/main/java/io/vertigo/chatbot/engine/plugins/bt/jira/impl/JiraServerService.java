package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import static com.atlassian.jira.rest.client.api.domain.IssueFieldId.*;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfigExport;
import io.vertigo.chatbot.commons.domain.JiraCustomFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.jira.helper.CustomAsynchronousJiraRestClient;
import io.vertigo.chatbot.engine.plugins.bt.jira.helper.CustomServerInfo;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JsmRequestType;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JsmRequestTypeSearchResult;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JsmServiceDesk;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JsmServiceDeskSearchResult;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

import javax.inject.Inject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Base64;
import java.util.ArrayList;
import java.util.HashMap;

import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.formatLink;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.noPayload;

public class JiraServerService implements Component, IJiraService {

	// Standard fields to exclude from JSM requestFieldValues (not supported by JSM API or handled separately)
	private static final List<String> JSM_EXCLUDED_STANDARD_FIELDS = List.of(
			REPORTER_FIELD.id,      // reporter - excluded per requirement
			ATTACHMENT_FIELD.id,    // attachment - excluded per requirement  
			ISSUE_TYPE_FIELD.id     // issuetype - handled separately as requestTypeId (not in requestFieldValues)
	);

    private String baseJira;
    private String user;
    private String password;
    private String project;
    private Long numberOfResults;
    private Boolean isCloud;
	private boolean jsmMode;
	private Long serviceDeskId;
	private HttpClient httpClient;
	private String jsmAuthHeader;
	private static final String SERVICE_DESK_API_PREFIX = "/rest/servicedeskapi";
    private JiraRestClient jiraRestClient;
    private DtList<JiraFieldSettingExport> jiraFieldSettingExports;
    private DtList<JiraCustomFieldSettingExport> jiraCustomFieldSettingExports;
    private CustomAsynchronousJiraRestClient customAsynchronousUserRestClient;
    @Inject
    private PasswordEncryptionServices passwordEncryptionServices;
    @Inject
    private AttachmentFieldService attachmentFieldService;
    @Inject
    private JsonEngine jsonEngine;


    public void refreshConfig(final ExecutorGlobalConfig config, final StringBuilder logs) {
        LogsUtils.addLogs(logs, "Refreshing Jira settings ... ");
        final JiraSettingExport jiraSettingExport = config.getBot().getJiraSetting();
        final DtList<JiraFieldSettingExport> jiraFieldSettingExport = config.getBot().getJiraFieldSetting();
        isCloud = true;
        if (jiraSettingExport == null || jiraFieldSettingExport == null) {
            LogsUtils.logKO(logs);
            throw new VSystemException("Jira setting and Jira fields settings must be set for jira plugin to work...");
        } else {
            baseJira = jiraSettingExport.getUrl();
            user = jiraSettingExport.getLogin();
            password = passwordEncryptionServices.decryptPassword(jiraSettingExport.getPassword());
            project = jiraSettingExport.getProject();
			jsmMode = Boolean.TRUE.equals(jiraSettingExport.getJsmMode());
			httpClient = HttpClient.newHttpClient();
			jsmAuthHeader = buildAuthHeader();
            jiraRestClient = createJiraRestClient();
            customAsynchronousUserRestClient = createCustomUserRestClient();
            isCloud = checkIfIsCloud();
			if (jsmMode) {
				serviceDeskId = resolveServiceDeskId(logs);
			} else {
				serviceDeskId = null;
			}
            jiraFieldSettingExports = jiraFieldSettingExport;
            jiraCustomFieldSettingExports = config.getBot().getJiraCustomFieldSetting();
            numberOfResults = jiraSettingExport.getNumberOfResults();
            LogsUtils.logOK(logs);
        }
    }

    public Boolean isCloud() {
        return isCloud;
    }

    public DtList<JiraFieldSettingExport> getJiraFieldSettingExports() {
        return jiraFieldSettingExports;
    }

    public DtList<JiraCustomFieldSettingExport> getJiraCustomFieldSettingExports() {
        return jiraCustomFieldSettingExports;
    }

    private JiraRestClient createJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(baseJira), user, password);
    }

    private CustomAsynchronousJiraRestClient createCustomUserRestClient() {
        final DisposableHttpClient httpClient = new AsynchronousHttpClientFactory()
                .createClient(URI.create(baseJira), new BasicHttpAuthenticationHandler(user, password));
        return new CustomAsynchronousJiraRestClient(URI.create(baseJira), httpClient);
    }

    public BasicIssue createIssue(final BlackBoard bb, final List<JiraField> jfFields, final List<IJiraFieldService> fieldServices) {
        final IssueRestClient issueClient = jiraRestClient.getIssueClient();
        final IssueInputBuilder iib = new IssueInputBuilder();
        iib.setProjectKey(project);
        jfFields.forEach(jiraField -> {
            if (jiraField.getValue() != null) {
                fieldServices.forEach(fieldService -> {
                    if (fieldService.supports(jiraField.getFieldType())) {
                        fieldService.processTicket(bb, iib, jiraField);
                    }
                });
            }
        });
        final IssueInput issue = iib.build();
        final BasicIssue newBasicIssue = issueClient.createIssue(issue).claim();

        final JiraField attachmentField = jfFields.stream()
                .filter(field -> attachmentFieldService.supports(field.getFieldType()))
                .findFirst()
                .orElse(null);

        if (attachmentField != null && !noPayload.equals(attachmentField.getValue())) {
            final URI attachmentsUri = URI.create(newBasicIssue.getSelf().toString() + "/attachments");
            attachmentFieldService.addingAttachmentToIssue(bb, attachmentField, attachmentsUri, issueClient);
        }
        return newBasicIssue;
    }

    private boolean checkIfIsCloud() {
        try {
            final CustomServerInfo customServerInfo = customAsynchronousUserRestClient.getServerInfo().claim();
            if (customServerInfo != null && customServerInfo.getDeploymentType() != null) {
                return customServerInfo.getDeploymentType().equalsIgnoreCase("cloud");
            } else {
                return false;
            }
        } catch (final Exception e) {
            return false;
        }
    }

    public List<String> getIssues(final String jqlSearch) {
        final SearchRestClient searchClient = jiraRestClient.getSearchClient();
        final SearchResult searchResult = searchClient.searchJql(jqlSearch).claim();
        return StreamSupport.stream(searchResult.getIssues().spliterator(), false)
                .map(x -> createLinkUrl(x.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public String createIssueJiraCommand(final BlackBoard bb, final List<JiraField> jiraFields, final List<IJiraFieldService> fieldServices) {
		if (jsmMode) {
			return createJsmRequest(jiraFields);
		}
		final var createdIssue = createIssue(bb, jiraFields, fieldServices);
		return createLinkUrl(createdIssue.getKey());

    }

	private String createJsmRequest(final List<JiraField> jiraFields) {
		final Long currentServiceDeskId = ensureServiceDeskId();
		
		// Validate mandatory fields
		final String requestTypeId = getFieldValue(jiraFields, ISSUE_TYPE_FIELD.id);
		final String summary = getFieldValue(jiraFields, SUMMARY_FIELD.id);

		if (requestTypeId == null || requestTypeId.isBlank()) {
			throw new VSystemException("Request type id is mandatory to create JSM request.");
		}
		if (summary == null || summary.isBlank()) {
			throw new VSystemException("Summary is mandatory to create JSM request.");
		}

		final Map<String, Object> payload = new java.util.HashMap<>();
		final Map<String, Object> requestFieldValues = new java.util.HashMap<>();

		// Add enabled standard fields (except excluded ones: reporter, attachment, and issueType, handled separately)
		if (jiraFieldSettingExports != null) {
			jiraFieldSettingExports.stream()
					.filter(JiraFieldSettingExport::getEnabled)
					.filter(fieldSetting -> !JSM_EXCLUDED_STANDARD_FIELDS.contains(fieldSetting.getFieldKey()))
					.forEach(fieldSetting -> {
						final String value = getFieldValue(jiraFields, fieldSetting.getFieldKey());
						if (value != null && !value.isBlank()) {
							final Object formattedValue = formatStandardFieldValue(fieldSetting.getFieldKey(), value);
							if (formattedValue != null) {
								requestFieldValues.put(fieldSetting.getFieldKey(), formattedValue);
							}
						}
					});
		}

		// Add custom fields if any
		if (jiraCustomFieldSettingExports != null) {
			jiraCustomFieldSettingExports.stream()
					.filter(JiraCustomFieldSettingExport::getEnabled)
					.forEach(customField -> {
						final String value = getFieldValue(jiraFields, customField.getFieldKey());
						if (value != null && !value.isBlank()) {
							final Object formattedValue = formatCustomFieldValue(value, customField.getFieldType());
							if (formattedValue != null) {
								requestFieldValues.put(customField.getFieldKey(), formattedValue);
							}
						}
					});
		}
		payload.put("requestFieldValues", requestFieldValues);
		payload.put("requestTypeId", requestTypeId);
		payload.put("serviceDeskId", currentServiceDeskId.toString());

		final HttpRequest request = HttpRequest.newBuilder()
				.uri(buildJsmUri(SERVICE_DESK_API_PREFIX + "/request"))
				.header("Authorization", jsmAuthHeader)
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(jsonEngine.toJson(payload)))
				.build();
		try {
			final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				final Map<?, ?> responseMap = jsonEngine.fromJson(response.body(), Map.class);
				final String issueKey = responseMap != null ? (String) responseMap.get("issueKey") : null;
				final String issueUrl = issueKey != null ? createLinkUrl(issueKey) : extractWebLink(responseMap);
				if (issueUrl != null) {
					return issueUrl;
				}
				throw new VSystemException("JSM request created but response had no issue link.");
			}
			throw new VSystemException("Failed to create JSM request. Status code " + response.statusCode() + " : " + response.body());
		} catch (final Exception e) {
			throw new VSystemException("Failed to create JSM request : " + e.getMessage(), e);
		}
	}

	private String extractWebLink(final Map<?, ?> responseMap) {
		if (responseMap != null && responseMap.get("_links") instanceof final Map<?, ?> links) {
			final Object web = links.get("web");
			if (web instanceof String) {
				return formatLink((String) web, true);
			}
		}
		return null;
	}

	/**
	 * Formats a standard Jira field value based on its field key for the JSM Cloud API.
	 * Based on JSM Cloud API format:
	 * - summary/description: simple string value
	 * - issuetype: simple string value (used for requestTypeId)
	 * - priority: { "id": "10002" }
	 * - fixVersions/versions: [{ "id": "24848" }]
	 * - assignee: { "accountId": "..." }
	 * - components: [{ "id": "..." }]
	 * - labels: ["label1", "label2"]
	 * - duedate: "YYYY-MM-DD"
	 *
	 * @param fieldKey The Jira field key (e.g., "priority", "fixVersions")
	 * @param value    The raw string value from user input (comma-separated for lists)
	 * @return The formatted value suitable for the JSM Cloud API, or null if not applicable
	 */
	private Object formatStandardFieldValue(final String fieldKey, final String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return switch (fieldKey) {
			// Simple string values (no transformation needed)
			case "summary", "description", "issuetype" -> value;

			// Single object with "id" key
			case "priority" -> Map.of("id", value.trim());

			// Array of objects with "id" key (comma-separated values)
			case "fixVersions", "versions" -> Arrays.stream(value.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.map(id -> Map.of("id", (Object) id))
					.toList();

			// Single object with "accountId" key (for Cloud)
			case "assignee" -> Map.of("accountId", value.trim());

			// Array of objects with "id" key (comma-separated values)
			case "components" -> Arrays.stream(value.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.map(id -> Map.of("id", (Object) id))
					.toList();

			// Array of strings (comma-separated values)
			case "labels" -> Arrays.stream(value.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.toList();

			// Date string (format: YYYY-MM-DD)
			case "duedate" -> value.trim();

			// Unknown standard field - return raw value
			default -> value;
		};
	}

	/**
	 * Formats a custom field value based on its declared type for the Jira/JSM API.
	 *
	 * @param value The raw string value from user input
	 * @param type  The field type code (STRING, NUMBER, ARRAY_LABELS, DATE, DATETIME, SINGLE_OPTION, MULTIPLE_OPTION, TREE_OPTION, SINGLE_USER, MULTIPLE_USER)
	 * @return The formatted value suitable for the JSM API, or null if formatting fails
	 */
	private Object formatCustomFieldValue(final String value, final String type) {
		if (value == null || value.isBlank() || type == null) {
			return null;
		}
		return switch (type) {
			case "STRING" -> value;
			case "NUMBER" -> {
				try {
					yield Long.parseLong(value.trim());
				} catch (final NumberFormatException e) {
					yield value; // Fallback to string if parsing fails
				}
			}
			case "ARRAY_LABELS" -> Arrays.stream(value.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.toList();
			case "DATE" -> value.trim(); // Format expected: "YYYY-MM-DD"
			case "DATETIME" -> value.trim(); // Format expected: ISO 8601 "YYYY-MM-DDTHH:mm:ss.SSS+ZZZZ"
			case "SINGLE_OPTION" -> Map.of("id", value.trim());
			case "MULTIPLE_OPTION" -> Arrays.stream(value.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.map(id -> Map.of("id", (Object) id))
					.toList();
			case "TREE_OPTION" -> parseTreeOption(value);
			case "SINGLE_USER" -> Map.of("name", value.trim());
			case "MULTIPLE_USER" -> Arrays.stream(value.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.map(name -> Map.of("name", name))
					.toList();
			default -> value; // Fallback to raw string for unknown types
		};
	}

	/**
	 * Parses a tree option value. Expected format: "parentId,childId" or just "parentId".
	 * Returns a nested structure: { "id": "parentId", "child": { "id": "childId" } }
	 */
	private Object parseTreeOption(final String value) {
		final String[] parts = value.split(",");
		if (parts.length == 0) {
			return null;
		}
		final Map<String, Object> result = new HashMap<>();
		result.put("id", parts[0].trim());
		if (parts.length > 1) {
			final Map<String, Object> child = new HashMap<>();
			child.put("id", parts[1].trim());
			result.put("child", child);
		}
		return result;
	}

	private String getFieldValue(final List<JiraField> jiraFields, final String fieldType) {
		return jiraFields.stream()
				.filter(field -> fieldType.equals(field.getFieldType()))
				.map(JiraField::getValue)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}

    private String createLinkUrl(final String key) {
        final String url = baseJira + "/browse/" + key;
        return formatLink(url, true);
    }

	public List<JsmRequestType> getRequestTypes() {
		final Long currentServiceDeskId = ensureServiceDeskId();
		final HttpRequest request = HttpRequest.newBuilder()
				.uri(buildJsmUri(SERVICE_DESK_API_PREFIX + "/servicedesk/" + currentServiceDeskId + "/requesttype"))
				.header("Authorization", jsmAuthHeader)
				.header("Accept", "application/json")
				.GET()
				.build();
		try {
			final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				final JsmRequestTypeSearchResult requestTypeSearchResult = jsonEngine.fromJson(response.body(), JsmRequestTypeSearchResult.class);
				return new ArrayList<>(requestTypeSearchResult.getValues());
			}
			throw new VSystemException("Failed to retrieve JSM request types. Status code " + response.statusCode() + " : " + response.body());
		} catch (final Exception e) {
			throw new VSystemException("Failed to retrieve JSM request types : " + e.getMessage(), e);
		}
	}

    public Project getProject() {
        return jiraRestClient.getProjectClient().getProject(project).claim();
    }

    public String getProjectName() {
        return project;
    }

    public OptionalIterable<IssueType> getIssueTypes() {
        return getProject().getIssueTypes();
    }

    public List<Version> getVersions() {
        return (List<Version>) getProject().getVersions();
    }

    public List<User> findUserByUsername(final String username) {
        if (isCloud) {
            return (List<User>) customAsynchronousUserRestClient.findUsers(username, project).claim();
        } else {
            return (List<User>) jiraRestClient.getUserClient().findUsers(username).claim();
        }
    }

    public List<Priority> getPriorities() {
        return (List<Priority>) jiraRestClient.getMetadataClient().getPriorities().claim();
    }

    public Issue getIssueByKey(final String key) {
        return jiraRestClient.getIssueClient().getIssue(key).claim();
    }

    public List<Transition> getIssueTransitions(final Issue issue) {
        return (List<Transition>) jiraRestClient.getIssueClient().getTransitions(issue).claim();
    }

    public List<Field> getCustomFields() {
        final List<Field> allFields = (List<Field>) jiraRestClient.getMetadataClient().getFields().claim();
        return allFields.stream().filter(field -> field.getFieldType() == FieldType.CUSTOM).collect(Collectors.toList());
    }

    public Iterable<BasicComponent> getComponents() {
        return getProject().getComponents();
    }

    public long getNumberOfResults() {
        return numberOfResults;
    }

    public boolean getJiraCheckFields(final ExecutorGlobalConfig config) {
        final ChatbotCustomConfigExport chatbotCustomConfig =
                jsonEngine.fromJson(config.getExecutorConfiguration().getCustomConfig(),
                        ChatbotCustomConfigExport.class);
        return chatbotCustomConfig.getJiraCheckBeforeCreate();
    }

	public boolean isJsmMode() {
		return jsmMode;
	}

	private String buildAuthHeader() {
		final String credentials = user + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}

	private URI buildJsmUri(final String path) {
		if (path.startsWith("/")) {
			return URI.create(baseJira + path);
		}
		return URI.create(baseJira + "/" + path);
	}

	private Long ensureServiceDeskId() {
		if (!jsmMode) {
			throw new VSystemException("JSM mode is disabled.");
		}
		if (serviceDeskId == null) {
			serviceDeskId = resolveServiceDeskId(new StringBuilder());
		}
		return serviceDeskId;
	}

	private Long resolveServiceDeskId(final StringBuilder logs) {
		final HttpRequest request = HttpRequest.newBuilder()
				.uri(buildJsmUri(SERVICE_DESK_API_PREFIX + "/servicedesk"))
				.header("Authorization", jsmAuthHeader)
				.header("Accept", "application/json")
				.GET()
				.build();
		try {
			final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				final JsmServiceDeskSearchResult result = jsonEngine.fromJson(response.body(), JsmServiceDeskSearchResult.class);
				return result.getValues().stream()
						.filter(serviceDesk -> project.equalsIgnoreCase(serviceDesk.getProjectKey()))
						.findFirst()
						.map(JsmServiceDesk::getId)
						.map(Long::valueOf)
						.orElseThrow(() -> new VSystemException("No service desk found for projectKey " + project));
			}
			if (logs != null) {
				LogsUtils.logKO(logs);
			}
			throw new VSystemException("Failed to retrieve service desks. Status code " + response.statusCode() + " : " + response.body());
		} catch (final Exception e) {
			if (logs != null) {
				LogsUtils.logKO(logs);
			}
			throw new VSystemException("Failed to retrieve service desks : " + e.getMessage(), e);
		}
	}
}
