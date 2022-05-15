package com.gallery.interceptor;

import com.gallery.error.ApiErrorModel;
import com.gallery.exception.ApiErrorException;
import com.gallery.exception.DatabaseOperationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    private static final Logger logger = LogManager.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(ApiErrorException.class)
    public ResponseEntity<Object> handleApiErrorException(ApiErrorException e, ServletWebRequest request) {
        final String message = String.format("Validation errors [%s] found for request [%s]",e.getErrors(), request.getRequest().getRequestURI());
        logger.warn(message);
        return new ResponseEntity<>(e.getErrors(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<Object> handleApiErrorException(DatabaseOperationException e) {
        return new ResponseEntity<>(new ApiErrorModel(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e, ServletWebRequest request) {
        final String message = String.format("Error happened [%s] for request [%s]",e.getMessage(), request.getRequest().getRequestURI());
        logger.error(message,e);
        return new ResponseEntity<>(new ApiErrorModel(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
