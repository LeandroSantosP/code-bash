package com.leandrosps.bug_bash.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.leandrosps.bug_bash.app.BashCode.RostInput;
import com.leandrosps.bug_bash.app.query.SubmissionsQuery;
import com.leandrosps.bug_bash.app.query.SubmissionsQuery.GetSubmissionByIdResponse;

@RestController
public class BashCodeController {

	public record BashCodeRequest(String code, boolean roastMode) {
	}

	@Autowired
	private BashCode bashCode;

	@Autowired
	private SubmissionsQuery submissionsQuery;

	@GetMapping("/actuator/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Server is healthy and ready to receive your code.");
	}

	@PostMapping("/bash-code")
	public String send_code(@RequestBody BashCodeRequest request) {
		return bashCode.roast(new RostInput(request.code(), request.roastMode()));
	}

	@GetMapping("/get-rost/{id}")
	public ResponseEntity<GetSubmissionByIdResponse> get_roast_by_id(@PathVariable String id) {
		return ResponseEntity.ok(submissionsQuery.getSubmissionById(id));
	}

}
