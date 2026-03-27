package com.leandrosps.bug_bash.app.erros;

import org.springframework.http.HttpStatus;

public class SubmissionsNotFoundException extends AppException {
	public SubmissionsNotFoundException(String id) {
		super("Submissions not found: " + id, HttpStatus.NOT_FOUND);
	}
}