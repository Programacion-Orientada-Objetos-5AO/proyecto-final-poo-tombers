package ar.edu.huergo.tombers.repository;

import ar.edu.huergo.tombers.entity.Prestamo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PrestamoRepository
    extends CrudRepository<Prestamo, Long> {
}
