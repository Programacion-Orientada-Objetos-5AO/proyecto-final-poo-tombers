package ar.edu.huergo.tombers.service.storage;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrato para guardar archivos binarios y exponer sus URLs publicas.
 */
public interface FileStorageService {

    /**
     * Guarda un archivo en el directorio logico indicado y devuelve sus metadatos.
     */
    StoredFile store(MultipartFile file, StorageDirectory directory);

    /**
     * Elimina un archivo utilizando su ruta relativa dentro del almacenamiento.
     */
    void delete(String relativePath);

    /**
     * Convierte una ruta relativa en una URL publica.
     */
    String toPublicUrl(String relativePath);

    /**
     * Convierte una URL publica nuevamente en su ruta relativa en disco.
     */
    String toRelativePath(String publicUrl);

    /**
     * Elimina el archivo referenciado por una URL publica si existe.
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
