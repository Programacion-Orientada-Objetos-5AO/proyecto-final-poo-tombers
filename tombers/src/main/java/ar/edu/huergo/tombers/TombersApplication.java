package ar.edu.huergo.tombers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import ar.edu.huergo.tombers.config.FileStorageProperties;

/**
 * Aplicacion principal de Tombers.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(FileStorageProperties.class)
public class TombersApplication {

    /**
     * Entry point de la aplicacion Spring Boot.
     */
    public static void main(String[] args) {
        SpringApplication.run(TombersApplication.class, args);
    }
}
