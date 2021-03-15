package io.vertigo.chatbot.designer.admin.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.UserAuthorizations;
import io.vertigo.account.authorization.definitions.Authorization;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotAuthorizations;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotProfilServices;
import io.vertigo.chatbot.designer.domain.admin.ChatbotProfilesEnum;
import io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.domain.commons.PersonRoleEnum;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;

@Transactional
public class AuthorizationServices implements Component {

	@Inject
	private AuthorizationManager authorizationManager;
	@Inject
	private ChatbotProfilServices chatbotProfilServices;

	private List<Authorization> obtainAuthorizationPerRole(final String role) {
		if (PersonRoleEnum.RAdmin.name().equals(role)) {
			return resolveAuthorizations(GlobalAuthorizations.AtzAdmPer, GlobalAuthorizations.AtzSuperAdmBot, GlobalAuthorizations.AtzAdmBot, ChatbotAuthorizations.AtzChatbot$admin);
		} else if (PersonRoleEnum.RUser.name().equals(role)) {
			return resolveAuthorizations(GlobalAuthorizations.AtzAdmBot, ChatbotAuthorizations.AtzChatbot$read, ChatbotAuthorizations.AtzChatbot$write, ChatbotAuthorizations.AtzChatbot$visiteur,
					ChatbotAuthorizations.AtzChatbot$contributeur, ChatbotAuthorizations.AtzChatbot$admFct);
		}
		throw new IllegalArgumentException("Unsupported role " + role);
	}

	private List<Authorization> resolveAuthorizations(final AuthorizationName... authNames) {
		final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
		final List<Authorization> authorizations = Arrays.stream(authNames)
				.map(name -> definitionSpace.resolve(name.name(), Authorization.class))
				.collect(Collectors.toList());
		return authorizations;
	}

	private void addAuthorizationByChatbotProfil(final UserAuthorizations userAuthorizations, final ProfilPerChatbot profil) {
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

	public void addUserAuthorization(final Person person) {
		final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations();
		obtainAuthorizationPerRole(person.getRolCd()).stream()
				.forEach(auth -> userAuthorizations.addAuthorization(auth));

		chatbotProfilServices.getProfilByPerId(person.getPerId()).stream().forEach(profil -> addAuthorizationByChatbotProfil(userAuthorizations, profil));
	}

	public void reloadUserAuthorization(final Person person) {
		final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations();
		userAuthorizations.clearRoles();
		addUserAuthorization(person);
	}

}
