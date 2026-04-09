package com.leandrosps.bug_bash.config;

import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.leandrosps.bug_bash.app.auth.DatabaseUserDetailsService;
import com.leandrosps.bug_bash.app.auth.GithubOAuth2UserService;

@Configuration
public class GlobalConfig {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${auth.client-id:minha-api-client}")
    private String authClientId;

    @Value("${auth.client-secret:minha-senha-secreta}")
    private String authClientSecret;

    @Value("${auth.redirect-uri-local:http://localhost:8080/login/oauth2/code/minha-api-client}")
    private String authRedirectUriLocal;

    @Value("${auth.redirect-uri-postman:https://oauth.pstmn.io/v1/callback}")
    private String authRedirectUriPostman;

    @Bean
    @Order(1)
    public SecurityFilterChain authServerFilterChain(HttpSecurity http) throws Exception {
        MediaTypeRequestMatcher htmlRequestMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        http.securityMatcher("/oauth2/authorize", "/oauth2/token", "/oauth2/jwks", "/oauth2/introspect",
                "/oauth2/revoke",
                "/.well-known/**", "/connect/**")
                .oauth2AuthorizationServer(authorizationServer -> authorizationServer.oidc(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor((request, response, authException) -> {
                    String originalRequest = request.getRequestURI();
                    if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
                        originalRequest = originalRequest + "?" + request.getQueryString();
                    }
                    String encodedContinue = URLEncoder.encode(originalRequest, StandardCharsets.UTF_8);
                    response.sendRedirect(frontendUrl + "?continue=" + encodedContinue);
                }, htmlRequestMatcher));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable()).securityMatcher("/api/**", "/public/**")
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/public/**").permitAll().anyRequest().authenticated());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http, GithubOAuth2UserService githubOAuth2UserService,
            DatabaseUserDetailsService databaseUserDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(databaseUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        http.csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/auth/login", "/error",
                        "/login/oauth2/code/**", "/oauth2/authorization/**", "/me").permitAll().anyRequest()
                        .authenticated())
                .authenticationProvider(daoAuthenticationProvider)
                .formLogin(form -> form.loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> response.setStatus(200))
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter()
                                    .write("{\"status\":\"UNAUTHORIZED\",\"message\":\"Invalid username or password\"}");
                        }))
                .oauth2Login(
                        oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(githubOAuth2UserService))
                                .defaultSuccessUrl(frontendUrl, true))
                .logout(
                        logout -> logout.logoutSuccessUrl(frontendUrl).invalidateHttpSession(true)
                                .clearAuthentication(true));
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString()).clientId(authClientId)
                .clientSecret(passwordEncoder().encode(authClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).redirectUri(authRedirectUriLocal)
                .redirectUri(authRedirectUriPostman).scope(OidcScopes.OPENID).scope("profile").scope("read")
                .scope("write")
                .clientSettings(ClientSettings.builder().requireProofKey(false).build()).build();
        return new InMemoryRegisteredClientRepository(client);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
