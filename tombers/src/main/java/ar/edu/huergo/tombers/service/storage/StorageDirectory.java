package ar.edu.huergo.tombers.service.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Directorios logicos utilizados para organizar los archivos almacenados.
 */
@Getter
@RequiredArgsConstructor
public enum StorageDirectory {
    USER_PROFILE("users"),
    PROJECT_BANNER("projects/banners");

    private final String directory;
}
