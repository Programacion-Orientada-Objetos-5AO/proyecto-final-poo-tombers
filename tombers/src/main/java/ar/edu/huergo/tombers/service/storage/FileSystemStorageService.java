package ar.edu.huergo.tombers.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.huergo.tombers.config.FileStorageProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Stores files on the local filesystem keeping public URLs consistent.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileSystemStorageService implements FileStorageService {

    private static final Map<String, String> EXTENSION_CONTENT_TYPES = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp"
    );

    private final FileStorageProperties properties;

    private Path rootLocation;
    private Set<String> allowedContentTypes;

    @PostConstruct
    void init() {
        this.rootLocation = properties.getRootLocation();
        this.allowedContentTypes = properties.getAllowedContentTypes().stream()
                .filter(StringUtils::hasText)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        try {
            Files.createDirectories(rootLocation);
            for (StorageDirectory directory : StorageDirectory.values()) {
                resolveDirectory(directory);
            }
        } catch (StorageException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new StorageException("Could not initialize storage directories", exception);
        }
        log.info("File storage ready at {}", rootLocation);
    }

    @Override
    public StoredFile store(MultipartFile file, StorageDirectory directory) {
        validateFile(file);

        String filename = buildFilename(file.getOriginalFilename());
        Path directoryPath = resolveDirectory(directory);
        Path destinationFile = directoryPath.resolve(filename).normalize();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new StorageException("Failed to store file " + filename, exception);
        }

        String relativePath = directory.getDirectory() + "/" + filename;
        return new StoredFile(relativePath, toPublicUrl(relativePath));
    }

    @Override
    public void delete(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }

        Path target = rootLocation.resolve(relativePath).normalize();
        if (!target.startsWith(rootLocation)) {
            throw new StorageException("Attempt to delete a file outside the storage root");
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException exception) {
            throw new StorageException("Failed to delete file " + relativePath, exception);
        }
    }

    @Override
    public String toPublicUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        String sanitized = relativePath.replace('\\', '/');
        if (sanitized.startsWith("/")) {
            sanitized = sanitized.substring(1);
        }
        return properties.getPublicUrlPrefix() + sanitized;
    }

    @Override
    public String toRelativePath(String publicUrl) {
        if (!StringUtils.hasText(publicUrl)) {
            return null;
        }

        String normalizedUrl = publicUrl.trim();
        String prefix = properties.getPublicUrlPrefix();
        if (normalizedUrl.startsWith(prefix)) {
            return normalizedUrl.substring(prefix.length());
        }

        int index = normalizedUrl.indexOf(prefix);
        if (index >= 0) {
            return normalizedUrl.substring(index + prefix.length());
        }

        return normalizedUrl.replaceFirst("^/+", "");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Uploaded file is empty");
        }

        long maxFileSize = properties.getMaxFileSize();
        if (maxFileSize > 0 && file.getSize() > maxFileSize) {
            throw new StorageException("File exceeds the maximum allowed size of " + maxFileSize + " bytes");
        }

        String contentType = resolveContentType(file);
        if (!allowedContentTypes.isEmpty()) {
            if (!StringUtils.hasText(contentType)) {
                throw new StorageException("Unable to determine file content type");
            }
            if (!allowedContentTypes.contains(contentType)) {
                throw new StorageException("Unsupported content type: " + contentType);
            }
        }
    }

    private Path resolveDirectory(StorageDirectory directory) {
        Path target = rootLocation.resolve(directory.getDirectory()).normalize();
        if (!target.startsWith(rootLocation)) {
            throw new StorageException("Attempt to resolve a directory outside of the storage root");
        }
        try {
            Files.createDirectories(target);
        } catch (IOException exception) {
            throw new StorageException("Could not create storage directory " + directory.getDirectory(), exception);
        }
        return target;
    }

    private String buildFilename(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String base = UUID.randomUUID().toString();
        if (StringUtils.hasText(extension)) {
            return base + "." + extension.toLowerCase(Locale.ROOT);
        }
        return base;
    }

    private String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (StringUtils.hasText(contentType)) {
            return contentType.toLowerCase(Locale.ROOT);
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (StringUtils.hasText(extension)) {
            String mapped = EXTENSION_CONTENT_TYPES.get(extension.toLowerCase(Locale.ROOT));
            if (mapped != null) {
                return mapped;
            }
        }
        return null;
    }
}
