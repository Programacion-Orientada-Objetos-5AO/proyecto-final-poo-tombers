package ar.edu.huergo.tombers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.tombers.entity.Project;

/**
 * Repositorio para la gestión de entidades Project.
 * Proporciona métodos para acceder y manipular proyectos en la base de datos.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}


