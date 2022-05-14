package com.gallery.exception;

import com.gallery.error.ApiErrorModel;

import java.util.Collection;
import java.util.HashSet;

public class ApiErrorException extends RuntimeException {

    public ApiErrorException() {
    }

    private final Collection<ApiErrorModel> errors = new HashSet<>();

    public void addError(ApiErrorModel error) {
        errors.add(error);
    }

    public Collection<ApiErrorModel> getErrors() {
        return errors;
    }
}
