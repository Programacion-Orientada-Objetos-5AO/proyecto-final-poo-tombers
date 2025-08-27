package ar.edu.huergo.tombers.repository;

import ar.edu.huergo.tombers.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(Project.ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "EXISTS (SELECT t FROM p.technologies t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "EXISTS (SELECT s FROM p.skillsNeeded s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Project> searchProjects(@Param("query") String query);
    
    @Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Project> findActiveProjectsOrderByCreatedAt();
    
    @Query("SELECT p FROM Project p WHERE p.progress < 100 ORDER BY p.progress DESC")
    List<Project> findIncompleteProjectsOrderByProgress();
}


