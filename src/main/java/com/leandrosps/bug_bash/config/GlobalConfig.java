package com.leandrosps.bug_bash.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.leandrosps.bug_bash.app.auth.DatabaseUserDetailsService;
import com.leandrosps.bug_bash.app.auth.GithubOAuth2UserService;

@Configuration
public class GlobalConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain authServerFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/oauth2/authorize", "/oauth2/token", "/oauth2/jwks", "/oauth2/introspect", "/oauth2/revoke",
				"/.well-known/**", "/connect/**")
				.oauth2AuthorizationServer(authorizationServer -> authorizationServer.oidc(Customizer.withDefaults()))
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated()).exceptionHandling(
						ex -> ex.defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login"),
								new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(c -> c.disable()).securityMatcher("/api/**")
				.authorizeHttpRequests(auth -> auth.requestMatchers("/public/**").permitAll().anyRequest().authenticated())
				.oauth2ResourceServer(auth -> auth.jwt(Customizer.withDefaults()));
		return http.build();
	}

	@Bean
	@Order(3)
	public SecurityFilterChain defaultFilterChain(HttpSecurity http, GithubOAuth2UserService githubOAuth2UserService,
			DatabaseUserDetailsService databaseUserDetailsService, PasswordEncoder passwordEncoder) throws Exception {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(databaseUserDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/login", "/error", "/login/oauth2/code/**", "/oauth2/authorization/**")
						.permitAll().anyRequest().authenticated())
				.authenticationProvider(daoAuthenticationProvider).formLogin(form -> form.defaultSuccessUrl("/me", true))
				.oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(githubOAuth2UserService))
						.defaultSuccessUrl("/me", true));
		return http.build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString()).clientId("minha-api-client")
				.clientSecret(passwordEncoder().encode("minha-senha-secreta"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://localhost:8080/login/oauth2/code/minha-api-client")
				.redirectUri("https://oauth.pstmn.io/v1/callback").scope(OidcScopes.OPENID).scope("profile").scope("read")
				.scope("write").clientSettings(ClientSettings.builder().requireProofKey(false).build()).build();
		return new InMemoryRegisteredClientRepository(client);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
