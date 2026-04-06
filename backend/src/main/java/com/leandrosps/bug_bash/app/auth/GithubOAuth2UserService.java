package com.leandrosps.bug_bash.app.auth;

import java.time.Instant;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.leandrosps.bug_bash.app.db.AppUserRepository;
import com.leandrosps.bug_bash.app.entites.AppUser;

@Service
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final AppUserRepository appUserRepository;
	private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

	public GithubOAuth2UserService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = delegate.loadUser(userRequest);

		String provider = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oauth2User.getAttributes();

		String providerUserId = String.valueOf(attributes.get("id"));
		String username = (String) attributes.get("login");
		String email = (String) attributes.get("email");
		String avatarUrl = (String) attributes.get("avatar_url");

		AppUser user = appUserRepository.findByProviderAndProviderUserId(provider, providerUserId).orElseGet(() -> {
			AppUser newUser = new AppUser();
			newUser.setProvider(provider);
			newUser.setProviderUserId(providerUserId);
			newUser.setCreatedAt(Instant.now());
			return newUser;
		});

		user.setUsername(username);
		user.setEmail(email);
		user.setAvatarUrl(avatarUrl);
		user.setUpdatedAt(Instant.now());

		var savedUser = appUserRepository.save(user);
		System.out.println("User " + savedUser.getUsername() + " logged in with GitHub");
		return oauth2User;
	}

}
