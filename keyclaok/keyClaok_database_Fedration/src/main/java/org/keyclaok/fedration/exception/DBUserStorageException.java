package org.keyclaok.fedration.exception;

public class DBUserStorageException extends RuntimeException {

    public DBUserStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBUserStorageException(Throwable cause) {
        super(cause);
    }

    public DBUserStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
