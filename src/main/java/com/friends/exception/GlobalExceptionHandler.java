package com.friends.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.friends.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	public static final String METHOD_NOT_ALLOWED = "HTTP Method not allowed!";
	public static final String INVALID_PARAM_MSG = "Missing required parameter(s) or invalid value(s) specified.";
	public static final String UNEXPECTED_MSG = "An unexpected exception has occurred! Check your request and try again!";
	public static final String EMAIL_NOT_FOUND = "Email address specified not found in DB!";
	
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiResponse errorResponse = new ApiResponse();
		errorResponse.setError(METHOD_NOT_ALLOWED);
		return new ResponseEntity<Object>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
}
