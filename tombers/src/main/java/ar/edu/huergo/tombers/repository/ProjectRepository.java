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

    List<Project> findByStatus(Project.ProjectStatus status);

    /**
     * Busca proyectos que coincidan con una consulta en título, descripción, tecnologías o habilidades necesarias.
     *
     * @param query la cadena de búsqueda
     * @return una lista de proyectos que coinciden con la consulta
     */
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "EXISTS (SELECT t FROM p.technologies t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "EXISTS (SELECT s FROM p.skillsNeeded s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Project> searchProjects(@Param("query") String query);

    /**
     * Obtiene proyectos activos ordenados por fecha de creación descendente.
     *
     * @return una lista de proyectos activos ordenados por fecha de creación
     */
    @Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Project> findActiveProjectsOrderByCreatedAt();

    @Query("SELECT p FROM Project p WHERE p.progress < 100 ORDER BY p.progress DESC")
    List<Project> findIncompleteProjectsOrderByProgress();
}


