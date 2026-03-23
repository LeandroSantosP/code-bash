package com.leandrosps.bug_bash.app;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leandrosps.bug_bash.app.HttpClient.OllamaRequest;
import com.leandrosps.bug_bash.app.entites.Analysis;
import com.leandrosps.bug_bash.app.entites.Submission;
import com.leandrosps.bug_bash.entriesobj.CodeReviewResponse;

@Service
public class BashCode {

	@Autowired
	private SubmissionsRepository submissionsRepository;

	public record RostInput(String code, boolean roastMode) {
	}

	@Autowired
	private HttpClient httpClient;

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

		String json = raw.substring(start, end + 1);
		json = json.replace("\r\n", "\n") // Windows line endings first
				.replace("\n", "\n").replace("\r", "\r").replace("\t", "\t");

		return raw.substring(start, end + 1);
	}

	public String roast(RostInput input) throws JsonMappingException, JsonProcessingException {
		var ollamaRequest = new OllamaRequest("codellama",
				String.format(prompt, input.code(), input.roastMode() ? "true" : "false"), false);

		var response = httpClient.sendCodeOllame(ollamaRequest);
		String cleanjson = extractJson(response.response());

		var submission = Submission.builder().code(input.code()).roastMode(input.roastMode()).score(0)
				.createdAt(Instant.now()).build();
		var saved = submissionsRepository.save(submission);
		ObjectMapper objectMapper = new ObjectMapper();

		System.out.println("Response: " + cleanjson);
		CodeReviewResponse codeReviewResponse = objectMapper.readValue(cleanjson, CodeReviewResponse.class);

		/* Todo: Save in te database */
		var analysis = Analysis.builder().submissionId(saved.getId())
				.feedbackMessage(codeReviewResponse.feedbackMessage()).build();

		System.out.println("Code review response NOW: " + analysis.toString());

		return "CURRENT: " + input.code();
	}
}
