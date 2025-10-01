package ar.edu.huergo.tombers.service.storage;

/**
 * Custom exception thrown when there is a problem handling stored files.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
