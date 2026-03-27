package com.leandrosps.bug_bash.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.leandrosps.bug_bash.app.erros.ErrorResponse;
import com.leandrosps.bug_bash.app.erros.FailedToParseJSON;
import com.leandrosps.bug_bash.app.erros.SubmissionsNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SubmissionsNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAppException(SubmissionsNotFoundException ex) {
		return ResponseEntity.status(ex.getStatus()).body(new ErrorResponse(ex.getStatus().name(), ex.getMessage()));
	}

	@ExceptionHandler(FailedToParseJSON.class)
	public ResponseEntity<ErrorResponse> handleFailedToParseJSON(FailedToParseJSON ex) {
		return ResponseEntity.status(ex.getStatus()).body(new ErrorResponse(ex.getStatus().name(), ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
	}
}
