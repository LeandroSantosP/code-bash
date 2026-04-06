package com.leandrosps.bug_bash.app;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leandrosps.bug_bash.app.BashCode.RostInput;
import com.leandrosps.bug_bash.app.db.SubmissionsRepository;
import com.leandrosps.bug_bash.app.query.SubmissionsQuery;
import com.leandrosps.bug_bash.app.query.SubmissionsQuery.GetSubmissionByIdResponse;

@RestController
public class BashCodeController {

	public record BashCodeRequest(String code, boolean roastMode) {
	}

	@Autowired
	private SubmissionsRepository submissionsRepository;

	@Autowired
	private BashCode bashCode;

	@Autowired
	private SubmissionsQuery submissionsQuery;

	@RestController
	public class CallbackController {

		@GetMapping("/login/oauth2/code/minha-api-client")
		public ResponseEntity<String> callback(@RequestParam String code) {
			return ResponseEntity.ok("Seu code: " + code);
		}
	}

	@GetMapping("/api/actuator/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Server is healthy and ready to receive your code.");
	}

	@PostMapping("/api/bash-code")
	public String send_code(@RequestBody BashCodeRequest request) {
		return bashCode.exec(new RostInput(request.code(), request.roastMode()));
	}

	@GetMapping("/get-rost/{id}")
	public ResponseEntity<GetSubmissionByIdResponse> get_roast_by_id(@PathVariable String id) {
		return ResponseEntity.ok(submissionsQuery.getSubmissionById(id));
	}

	@DeleteMapping("/delete-rost/{id}")
	public ResponseEntity<Void> delete_roast_by_id(@PathVariable UUID id) {
		submissionsRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}

}
