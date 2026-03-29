package com.leandrosps.bug_bash.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.leandrosps.bug_bash.entriesobj.OllamaResponse;

@Component
public class HttpClient {
	static private RestTemplate restTemplate = new RestTemplate();;
	@Value("${ollama.url:http://localhost:11434}")
	private String baseUrlOllama;

	static HttpClient instance;

	private HttpClient() {
	}

	public record OllamaRequest(@Value("${ollama.model:codellama}") String model, String prompt, boolean stream) {
	}

	public OllamaResponse sendCodeOllame(OllamaRequest input) {
		ResponseEntity<OllamaResponse> response = restTemplate.postForEntity(baseUrlOllama + "/api/generate", input,
				OllamaResponse.class);

		Assert.isTrue(response.getStatusCode().equals(HttpStatusCode.valueOf(200)),
				"Failed to request Ollama API: " + response.getBody());

		return response.getBody();
	}

	public static HttpClient getInstance() {
		if (instance == null) {
			instance = new HttpClient();
		}
		return instance;
	}
}