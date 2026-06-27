package com.example.RESEARCH_SERVICE.exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Exception ex) {
        super(message);
    }
}
