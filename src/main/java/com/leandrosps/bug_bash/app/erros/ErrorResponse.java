package com.leandrosps.bug_bash.app.erros;

import java.time.Instant;

public record ErrorResponse(String status, String message, Instant timestamp) {
	public ErrorResponse(String status, String message) {
		this(status, message, Instant.now());
	}
}