package ar.edu.huergo.tombers.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Storage related configuration properties.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "storage")
public class FileStorageProperties {

    @NotNull
    private Path rootLocation = Paths.get("uploads");

    @NotNull
    private String publicUrlPrefix = "/uploads/";

    private long maxFileSize = 5 * 1024 * 1024; // 5MB

    private List<String> allowedContentTypes = new ArrayList<>(List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    ));

    /**
     * Returns the normalized root path where files will be stored.
     */
    public Path getRootLocation() {
        return rootLocation.toAbsolutePath().normalize();
    }

    /**
     * Returns the public URL prefix ensuring it uses /prefix/ format.
     */
    public String getPublicUrlPrefix() {
        String prefix = publicUrlPrefix == null ? "" : publicUrlPrefix.trim();
        if (prefix.isEmpty()) {
            prefix = "/uploads/";
        }
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix;
    }

    /**
     * Replaces allowed content types fallback to defaults when empty.
     */
    public void setAllowedContentTypes(List<String> allowedContentTypes) {
        if (allowedContentTypes == null || allowedContentTypes.isEmpty()) {
            this.allowedContentTypes = new ArrayList<>(List.of("image/jpeg", "image/png", "image/webp"));
        } else {
            this.allowedContentTypes = new ArrayList<>(allowedContentTypes);
        }
    }
}
