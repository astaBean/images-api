package com.gallery.error;

public class DatabaseOperationError extends RuntimeException {

    public DatabaseOperationError(String message) {
        super(message);
    }
}
