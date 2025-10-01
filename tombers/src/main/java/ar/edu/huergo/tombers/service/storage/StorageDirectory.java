package ar.edu.huergo.tombers.service.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Logical directories used to organize stored files.
 */
@Getter
@RequiredArgsConstructor
public enum StorageDirectory {
    USER_PROFILE("users"),
    PROJECT_BANNER("projects/banners");

    private final String directory;
}
