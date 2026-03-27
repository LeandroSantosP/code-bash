package com.leandrosps.bug_bash.app;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leandrosps.bug_bash.app.HttpClient.OllamaRequest;
import com.leandrosps.bug_bash.app.db.AnalysisRepository;
import com.leandrosps.bug_bash.app.db.SubmissionsRepository;
import com.leandrosps.bug_bash.app.entites.Analysis;
import com.leandrosps.bug_bash.app.entites.Submission;
import com.leandrosps.bug_bash.app.erros.FailedToParseJSON;
import com.leandrosps.bug_bash.entriesobj.CodeReviewResponse;

@Service
public class BashCode {

	@Autowired
	private SubmissionsRepository submissionsRepository;

	@Autowired
	private AnalysisRepository analysisRepository;

	public record RostInput(String code, boolean roastMode) {
	}

	@Autowired
	private HttpClient httpClient;
	String prompt = "You are a code reviewer. Review the code snippet: ```%s```. "
			+ "RoastMode is %s (if true, include a sarcastic roast). " + "Provide feedback and improvements. "
			+ "Return ONLY a valid JSON object. "
			+ "IMPORTANT: All newlines inside the 'feedbackMessage' string MUST be escaped as '\\n'. "
			+ "Do not use raw line breaks or unescaped double quotes inside the JSON values. "
			+ "JSON format: {\"feedbackMessage\": \"string\", \"severity\": \"low|medium|high\", \"score\": 0-10}";

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

	public String roast(RostInput input) {

		var ollamaRequest = new OllamaRequest("codellama",
				String.format(prompt, input.code(), input.roastMode() ? "true" : "false"), false);

		var response = httpClient.sendCodeOllame(ollamaRequest);
		String cleanjson = extractJson(response.response());

		ObjectMapper objectMapper = new ObjectMapper();
		CodeReviewResponse codeReviewResponse = null;

		try {
			codeReviewResponse = objectMapper.readValue(cleanjson, CodeReviewResponse.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new FailedToParseJSON(cleanjson);
		}

		var submission = Submission.builder().code(input.code()).roastMode(input.roastMode())
				.score(codeReviewResponse.score()).createdAt(Instant.now()).build();

		var saved = submissionsRepository.save(submission);

		codeReviewResponse.severity();
		/* Todo: Save in te database */
		var analysis = Analysis.builder().submissionId(saved.getId()).severity(codeReviewResponse.severity())
				.feedbackMessage(codeReviewResponse.feedbackMessage()).build();

		var analysisSaved = analysisRepository.save(analysis);

		return codeReviewResponse.feedbackMessage() + "\n\n(Submission ID: " + saved.getId() + ", Analysis ID: "
				+ analysisSaved.getId() + ")";
	}
}
