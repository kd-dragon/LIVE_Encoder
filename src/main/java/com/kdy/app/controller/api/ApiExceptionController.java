package com.kdy.app.controller.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ApiExceptionController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	// RequestBody validation 오류 처리
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<String> paramValidationError(MethodArgumentNotValidException ex, HttpServletRequest req, HttpServletResponse response) throws IOException{
		logger.error("API @Validation ExceptionHandler## : "+ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
		
		errorLog(ex.getBindingResult());
		
		return ResponseEntity.badRequest().body(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage().toString());
	}
	
	// ModelAttribute의 validation 오류 처리
	@ExceptionHandler({BindException.class})
	public ResponseEntity<String> paramViolationError(BindException ex, HttpServletRequest req, HttpServletResponse response) throws IOException{
		logger.error("API @Validation ExceptionHandler$$ : "+ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
		
		errorLog(ex.getBindingResult());
		
		return ResponseEntity.badRequest().body(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage().toString());
	}
	
	public void errorLog(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        logger.info(builder.toString());
	}

}
