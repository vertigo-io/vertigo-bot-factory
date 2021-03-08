package io.vertigo.chatbot.designer.admin.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.account.authorization.UserAuthorizations;
import io.vertigo.account.authorization.definitions.Authorization;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotAuthorizations;
import io.vertigo.chatbot.designer.domain.admin.ChatbotProfilesEnum;
import io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot;
import io.vertigo.chatbot.designer.domain.commons.PersonRoleEnum;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.definition.DefinitionSpace;

public final class AuthorizationUtils {

	public static List<Authorization> obtainAuthorizationPerRole(final String role) {
		if (PersonRoleEnum.RAdmin.name().equals(role)) {
			return resolveAuthorizations(GlobalAuthorizations.AtzAdmPer, GlobalAuthorizations.AtzSuperAdmBot, GlobalAuthorizations.AtzAdmBot, ChatbotAuthorizations.AtzChatbot$admin);
		} else if (PersonRoleEnum.RUser.name().equals(role)) {
			return resolveAuthorizations(GlobalAuthorizations.AtzAdmBot, ChatbotAuthorizations.AtzChatbot$read, ChatbotAuthorizations.AtzChatbot$write, ChatbotAuthorizations.AtzChatbot$visiteur,
					ChatbotAuthorizations.AtzChatbot$contributeur, ChatbotAuthorizations.AtzChatbot$admFct);
		}
		throw new IllegalArgumentException("Unsupported role " + role);
	}

	private static List<Authorization> resolveAuthorizations(final AuthorizationName... authNames) {
		final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
		final List<Authorization> authorizations = Arrays.stream(authNames)
				.map(name -> definitionSpace.resolve(name.name(), Authorization.class))
				.collect(Collectors.toList());
		return authorizations;
	}

	public static void addAuthorizationByChatbotProfil(UserAuthorizations userAuthorizations, ProfilPerChatbot profil) {
		userAuthorizations.withSecurityKeys("botId", profil.getBotId());
		if (profil.getChpCd().equals(ChatbotProfilesEnum.VISITEUR.name())) {
			userAuthorizations.withSecurityKeys("botVisiteur", profil.getBotId());
		}
		if (profil.getChpCd().equals(ChatbotProfilesEnum.CONTRIBUTEUR.name())) {
			userAuthorizations.withSecurityKeys("botContributeur", profil.getBotId());
		}
		if (profil.getChpCd().equals(ChatbotProfilesEnum.ADMINISTRATEUR.name())) {
			userAuthorizations.withSecurityKeys("botAdmFct", profil.getBotId());
		}
	}
}
