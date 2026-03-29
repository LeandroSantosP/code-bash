package com.leandrosps.bug_bash.app;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leandrosps.bug_bash.app.HttpClient.OllamaRequest;
import com.leandrosps.bug_bash.app.db.AnalysisRepository;
import com.leandrosps.bug_bash.app.db.FixSuggestionRepository;
import com.leandrosps.bug_bash.app.db.SubmissionsRepository;
import com.leandrosps.bug_bash.app.entites.Analysis;
import com.leandrosps.bug_bash.app.entites.FixSuggestion;
import com.leandrosps.bug_bash.app.entites.Submission;
import com.leandrosps.bug_bash.app.erros.FailedToParseJSON;
import com.leandrosps.bug_bash.entriesobj.CodeReviewResponse;

@Service
public class BashCode {

	@Autowired
	private SubmissionsRepository submissionsRepository;

	@Autowired
	private AnalysisRepository analysisRepository;

	@Autowired
	private FixSuggestionRepository fixSuggestionRepository;

	public record RostInput(String code, boolean roastMode) {
	}

	/* All of this should be in the Domain entites, but in lazy for now */
	@Autowired
	private HttpClient httpClient;

	String prompt = """
			You are a code reviewer that ONLY outputs valid JSON.

			DO NOT write explanations outside JSON.
			DO NOT use markdown.
			DO NOT include code blocks.
			DO NOT include text before or after JSON.

			If you break these rules, your answer is invalid.

			---------------------

			Review this code:
			%s

			RoastMode: %s

			---------------------

			Return EXACTLY this JSON structure (but with actual values):

			{
			  "feedbackMessage": "string",
			  "severity": "low",
			  "suggestedCode": "string",
			  "explanation": "string",
			  "score": number
			}

			---------------------

			STRICT RULES:

			- Output MUST start with { and end with }
			- NO ``` blocks
			- NO commentary outside JSON
			- suggestedCode MUST be a SINGLE LINE string
			- DO NOT use real line breaks inside suggestedCode
			- Replace every line break with \\n
			- NEVER include actual newline characters (no ENTER)
			- ALL quotes inside strings MUST be escaped as \"
			- suggestedCode MUST be full corrected code
			- explanation MUST explain the fixes
			- score MUST be integer (0-10)
			- severity MUST be one of: low, medium, high
			- NEVER return placeholder values like "string"

			---------------------

			If your response is not valid JSON, regenerate it.

			Now respond.
			""";

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

	private CodeReviewResponse parseCodeReviewResponse(String response) {
		String json = extractJson(response);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, CodeReviewResponse.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new FailedToParseJSON(json);
		}
	}

	public String exec(RostInput input) {

		var ollamaRequest = new OllamaRequest("codellama",
				String.format(prompt, input.code(), input.roastMode() ? "true" : "false"), false);

		var response = httpClient.sendCodeOllame(ollamaRequest);
		System.out.println("Raw AI response: " + response.response());
		CodeReviewResponse codeReviewResponse = this.parseCodeReviewResponse(response.response());
		System.out.println("Parsed code review response: " + codeReviewResponse);

		/* Todo: Save in te database */
		var submission = Submission.builder().code(input.code()).roastMode(input.roastMode())
				.score(codeReviewResponse.score()).createdAt(Instant.now()).build();

		var saved = submissionsRepository.save(submission);

		var analysis = Analysis.builder().submissionId(saved.getId()).severity(codeReviewResponse.severity())
				.feedbackMessage(codeReviewResponse.feedbackMessage()).build();

		var analysisSaved = analysisRepository.save(analysis);

		var fixSuggestion = FixSuggestion.builder().originalCode(input.code()).analysisId(analysisSaved.getId())
				.explanation(codeReviewResponse.explanation()).suggestedCode(codeReviewResponse.suggestedCode()).build();

		var fixSuggestionSaved = fixSuggestionRepository.save(fixSuggestion);

		return codeReviewResponse.feedbackMessage() + "\n\n(Submission ID: " + saved.getId() + ", Analysis ID: "
				+ analysisSaved.getId() + ", FixSuggestion ID: " + fixSuggestionSaved.getId() + ")";
	}
}
