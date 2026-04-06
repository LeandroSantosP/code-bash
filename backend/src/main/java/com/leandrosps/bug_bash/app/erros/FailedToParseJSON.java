package com.leandrosps.bug_bash.app.erros;

import org.springframework.http.HttpStatus;

public class FailedToParseJSON extends AppException {
	public FailedToParseJSON(String code) {
		super("Failed to parse JSON: " + code, HttpStatus.BAD_REQUEST);
	}
}
