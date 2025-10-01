package ar.edu.huergo.tombers.service.storage;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contract for storing binary files and exposing public URLs.
 */
public interface FileStorageService {

    /**
     * Stores a file under the provided logical directory and returns metadata.
     */
    StoredFile store(MultipartFile file, StorageDirectory directory);

    /**
     * Deletes a file using its relative path from the storage root.
     */
    void delete(String relativePath);

    /**
     * Converts a relative path into a public URL.
     */
    String toPublicUrl(String relativePath);

    /**
     * Converts a public URL back into the relative path stored on disk.
     */
    String toRelativePath(String publicUrl);

    /**
     * Deletes a file referenced by its public URL if it exists.
     */
    default void deleteByPublicUrl(String publicUrl) {
        if (!StringUtils.hasText(publicUrl)) {
            return;
        }
        String relativePath = toRelativePath(publicUrl);
        if (StringUtils.hasText(relativePath)) {
            delete(relativePath);
        }
    }
}
