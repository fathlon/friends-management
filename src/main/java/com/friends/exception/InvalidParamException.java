package com.friends.exception;

public class InvalidParamException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5130725449294093766L;
	
	public InvalidParamException() {}
	
	public InvalidParamException(String errorMessage) {
		super(errorMessage);
	}
	
}
