package ar.edu.huergo.tombers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TombersApplication {

    public static void main(String[] args) {
        SpringApplication.run(TombersApplication.class, args);
    }
}


