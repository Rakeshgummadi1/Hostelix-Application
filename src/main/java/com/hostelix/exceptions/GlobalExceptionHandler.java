package com.hostelix.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.hostelix.util.ApiResponse;

import feign.FeignException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(
			InvalidCredentialsException credentialsException) {
		ApiResponse<Void> response = ApiResponse.<Void>builder().status(HttpStatus.UNAUTHORIZED.value())
				.message(credentialsException.getMessage()).data(null).build();
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException argumentNotValidException) {
		Map<String, String> errors = new HashMap<>();
		argumentNotValidException.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
				.status(HttpStatus.BAD_REQUEST.value()).message(argumentNotValidException.getMessage()).data(errors)
				.build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException userNotFoundException){
		ApiResponse<Void> response = ApiResponse.<Void>builder()
				.status(HttpStatus.NOT_FOUND.value())
				.message(userNotFoundException.getMessage())
				.data(null)
				.build();
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExistsException
	(EmailAlreadyExistsException emailAlreadyExistsException){
		ApiResponse<Void> response = new ApiResponse<Void>();
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setMessage(emailAlreadyExistsException.getMessage());
		response.setData(null);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(PasswordsDoNotMatchException.class)
	public ResponseEntity<ApiResponse<Void>> handlePasswordsDoNotMatchException
	(PasswordsDoNotMatchException passwordsDoNotMatchException){
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setMessage(passwordsDoNotMatchException.getMessage());
		response.setData(null);
		return new ResponseEntity<ApiResponse<Void>>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException
	(HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException){
		ApiResponse<Void> response=new ApiResponse<>();
		response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
		response.setMessage(httpRequestMethodNotSupportedException.getMessage());
		response.setData(null);
		return new ResponseEntity<ApiResponse<Void>>(response,HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ApiResponse<Void>> handleNullPointerException(NullPointerException nullPointerException){
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setMessage(nullPointerException.getMessage());
		response.setData(null);
		return new ResponseEntity<ApiResponse<Void>>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatus(HttpStatus.NOT_FOUND.value());
		response.setMessage(resourceNotFoundException.getMessage());
		response.setData(null);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException noResourceFoundException){
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatus(HttpStatus.NOT_FOUND.value());
		response.setMessage(noResourceFoundException.getMessage());
		response.setData(null);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ApiResponse<Void>> handleFeignException(FeignException exception){
		ApiResponse<Void> response =new ApiResponse<>();
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setMessage(exception.getMessage());
		response.setData(null);
		return new ResponseEntity<ApiResponse<Void>>(response,HttpStatus.BAD_REQUEST);
	}
}
