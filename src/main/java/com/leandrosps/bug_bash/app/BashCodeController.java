package com.leandrosps.bug_bash.app;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.leandrosps.bug_bash.app.HttpClient.OllamaRequest;
import com.leandrosps.bug_bash.app.entites.Analysis;
import com.leandrosps.bug_bash.app.entites.Submission;
import com.leandrosps.bug_bash.entriesobj.CodeReviewResponse;

import tools.jackson.databind.ObjectMapper;

@RestController
public class BashCodeController {

	@Autowired
	private SubmissionsRepository submissionsRepository;

	@Autowired
	private HttpClient httpClient;

	public record BashCodeRequest(String code, boolean roastMode) {
	}

	@GetMapping("/actuator/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Server is healthy and ready to receive your code.");
	}

	String prompt = "You are a code reviewer. Review the code snippet delimited by triple backticks."
			+ "The code is: ```%s```. " + "The roastMode is: %s — if true, include a roast take; if false, do not. "
			+ "Always provide feedback and improvement suggestions. "
			+ "Respond ONLY with a raw JSON object. No intro text, no explanation, no markdown. "
			+ "The JSON must follow this exact format: "
			+ "{\"feedbackMessage\": \"string\", \"severity\": \"low|medium|high\", \"score\": 0-10}";

	private String extractJson(String raw) {
		int start = raw.indexOf("{");
		int end = raw.lastIndexOf("}");

		if (start == -1 || end == -1) {
			throw new IllegalStateException("No JSON object found in AI response: " + raw);
		}

		return raw.substring(start, end + 1);
	}

	/* leadn ro */
	@PostMapping("/bash-code")
	public String send_code(@RequestBody BashCodeRequest request) {
		System.out.println("request: " + request);

		var input = new OllamaRequest("codellama",
				String.format(prompt, request.code(), request.roastMode() ? "true" : "false"), false);

		var response = httpClient.sendCodeOllame(input);
		String cleanjson = extractJson(response.response());

		var submission = Submission.builder().code(request.code()).roastMode(request.roastMode()).score(0)
				.createdAt(Instant.now()).build();
		var saved = submissionsRepository.save(submission);
		System.out.println("Saved submission id: " + saved.getId());

		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println("Response: " + response.response());
		CodeReviewResponse codeReviewResponse = objectMapper.readValue(cleanjson, CodeReviewResponse.class);

		/* Todo: Save in te database */
		var analysis = Analysis.builder().submissionId(saved.getId())
				.feedbackMessage(codeReviewResponse.feedbackMessage()).build();

		System.out.println("Code review response: " + analysis.toString());

		return request.code();
	}

}
