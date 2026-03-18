package com.leandrosps.bug_bash.app;

import org.springframework.web.bind.annotation.RestController;

import com.leandrosps.bug_bash.app.entites.Submission;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class BashCodeController {

	@Autowired
	private SubmissionsRepository submissionsRepository;

	public record BashCodeRequest(String code, boolean roastMode) {
	}

	@GetMapping("/actuator/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Server is healthy and ready to receive your code.");
	}

	@PostMapping("/bash-code")
	public String send_code(@RequestBody BashCodeRequest request) {
		System.out.println(request);

		var submission = Submission.builder().code(request.code()).roastMode(request.roastMode())
				.score(0).createdAt(Instant.now()).build();

		var saved = submissionsRepository.save(submission);
		System.out.println("Saved submission id: " + saved.getId());
		submissionsRepository.findById(saved.getId());
		return request.code();
	}

}
