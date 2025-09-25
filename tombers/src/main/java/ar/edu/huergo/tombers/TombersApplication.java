package ar.edu.huergo.tombers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal de la aplicación Spring Boot Tombers.
 * Configura y lanza la aplicación con auditoría JPA habilitada.
 */
@SpringBootApplication
@EnableJpaAuditing
public class TombersApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * @param args Argumentos de línea de comandos pasados al iniciar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(TombersApplication.class, args);
    }
}
