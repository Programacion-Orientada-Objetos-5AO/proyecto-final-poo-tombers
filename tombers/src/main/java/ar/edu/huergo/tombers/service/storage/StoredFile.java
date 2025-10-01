package ar.edu.huergo.tombers.service.storage;

/**
 * Information about a file stored on disk.
 */
public record StoredFile(String relativePath, String publicUrl) {
}
