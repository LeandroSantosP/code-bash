package com.leandrosps.bug_bash.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leandrosps.bug_bash.app.BashCode.RostInput;

@RestController
public class BashCodeController {

	public record BashCodeRequest(String code, boolean roastMode) {
	}

	@Autowired
	private BashCode bashCode;

	@GetMapping("/actuator/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Server is healthy and ready to receive your code.");
	}

	/* leadn ro */
	@PostMapping("/bash-code")
	public String send_code(@RequestBody BashCodeRequest request) {
		try {
			return bashCode.roast(new RostInput(request.code(), request.roastMode()));
		} catch (Exception e) {
			e.printStackTrace();
			return "Error processing code: " + e.getMessage();
		}
	}

	@GetMapping("/get-rost/{roast_id}/{id}")
	public String get_roast_by_id(@RequestParam String roast_id) {
		return new String("leandro");
	}

}
