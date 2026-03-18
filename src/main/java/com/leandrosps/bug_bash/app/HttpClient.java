package com.leandrosps.bug_bash.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Component
@Lazy
public class HttpClient {
	static private RestTemplate restTemplate = new RestTemplate();;
	static private String baseUrl = "http://localhost:8080";
	static HttpClient instance;

	private HttpClient() {
	}

	public void healthCheck() {
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/actuator/health", String.class);
		Assert.isTrue(response.getStatusCode().equals(HttpStatusCode.valueOf(200)),
				"Failed to connect to the server. Please make sure the server is running and accessible.");
		if (!response.getBody().equals("Server is healthy and ready to receive your code.")) {
			throw new IllegalStateException("Unexpected response body: " + response.getBody());
		}
		System.out.println("Server response: " + response.getBody());
	}

	public static HttpClient getInstance() {
		if (instance == null) {
			instance = new HttpClient();
		}
		return instance;
	}
}