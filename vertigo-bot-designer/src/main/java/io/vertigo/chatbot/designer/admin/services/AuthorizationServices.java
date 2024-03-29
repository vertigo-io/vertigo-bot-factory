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
			return resolveAuthorizations(GlobalAuthorizations.AtzSuperAdm, GlobalAuthorizations.AtzBotUser, ChatbotAuthorizations.AtzChatbot$botSuperAdmin);
		} else if (PersonRoleEnum.RUser.name().equals(role)) {
			return resolveAuthorizations(GlobalAuthorizations.AtzBotUser, ChatbotAuthorizations.AtzChatbot$botVisitor,
					ChatbotAuthorizations.AtzChatbot$botContributor, ChatbotAuthorizations.AtzChatbot$botAdm);
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
		final String chatbotProfile = profil.getChpCd();
		final ChatbotProfilesEnum chatbotEnum = ChatbotProfilesEnum.valueOf(chatbotProfile);

		//Not break to add the lower security
		switch (chatbotEnum) {
			case ADMINISTRATEUR:
				userAuthorizations.withSecurityKeys("botAdm", profil.getBotId());
			case CONTRIBUTEUR:
				userAuthorizations.withSecurityKeys("botContributor", profil.getBotId());
			case VISITEUR:
				userAuthorizations.withSecurityKeys("botVisitor", profil.getBotId());
			default:
				break;
		}
	}

	public void addUserAuthorization(final Person person) {
		final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations();
		obtainAuthorizationPerRole(person.getRolCd()).stream()
				.forEach(auth -> userAuthorizations.addAuthorization(auth));

		chatbotProfilServices.getProfilByPerId().stream().forEach(profil -> addAuthorizationByChatbotProfil(userAuthorizations, profil));
	}

	public void reloadUserAuthorization(final Person person) {
		final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations();
		userAuthorizations.clearRoles();
		addUserAuthorization(person);
	}
}
