package ar.edu.huergo.tombers.service.storage;

/**
 * Informacion sobre un archivo almacenado en disco.
 */
public record StoredFile(String relativePath, String publicUrl) {
}
