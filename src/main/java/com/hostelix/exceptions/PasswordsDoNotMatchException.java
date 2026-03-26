package com.hostelix.exceptions;

@SuppressWarnings("serial")
public class PasswordsDoNotMatchException extends RuntimeException{

	public PasswordsDoNotMatchException(String message) {
		super(message);
	}
}
