package ar.edu.huergo.tombers.service.storage;

/**
 * Excepcion personalizada para problemas al manipular archivos almacenados.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
