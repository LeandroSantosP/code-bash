package com.leandrosps.bug_bash.app;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthController {

	@GetMapping("/favicon.ico")
	public ResponseEntity<Void> favicon() {
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public Map<String, Object> me(Authentication authentication) {
		Map<String, Object> response = new HashMap<>();
		response.put("authenticated", authentication != null && authentication.isAuthenticated());

		if (authentication == null) {
			response.put("name", null);
			response.put("authorities", null);
			return response;
		}

		response.put("name", authentication.getName());
		response.put("authorities", authentication.getAuthorities());

		if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
			response.put("attributes", oauth2User.getAttributes());
		}

		return response;
	}

	@PostMapping("/register")
	public String createUser(@RequestBody String entity) {
		return entity;
	}

}
