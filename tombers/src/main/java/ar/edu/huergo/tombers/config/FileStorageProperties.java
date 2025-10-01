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
 * Propiedades de configuracion para el almacenamiento de archivos.
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

    private long maxFileSize = 5 * 1024 * 1024; // 5 MB

    private List<String> allowedContentTypes = new ArrayList<>(List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    ));

    /**
     * Devuelve la ruta raiz normalizada donde se guardaran los archivos.
     */
    public Path getRootLocation() {
        return rootLocation.toAbsolutePath().normalize();
    }

    /**
     * Devuelve el prefijo de URL publica garantizando el formato /prefijo/.
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
     * Reemplaza la lista de tipos permitidos y vuelve a los valores por defecto si esta vacia.
     */
    public void setAllowedContentTypes(List<String> allowedContentTypes) {
        if (allowedContentTypes == null || allowedContentTypes.isEmpty()) {
            this.allowedContentTypes = new ArrayList<>(List.of("image/jpeg", "image/png", "image/webp"));
        } else {
            this.allowedContentTypes = new ArrayList<>(allowedContentTypes);
        }
    }
}
