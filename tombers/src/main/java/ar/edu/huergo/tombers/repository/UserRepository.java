package ar.edu.huergo.tombers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.tombers.entity.User;

/**
 * Repositorio para la gestión de entidades User.
 * Proporciona métodos para acceder y manipular usuarios en la base de datos.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email la dirección de correo electrónico del usuario
     * @return un Optional que contiene el usuario si se encuentra, vacío en caso contrario
     */
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    /**
     * Verifica si existe un usuario con la dirección de correo electrónico especificada.
     *
     * @param email la dirección de correo electrónico a verificar
     * @return true si existe un usuario con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     *
     * @param username el nombre de usuario a verificar
     * @return true si existe un usuario con ese username, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Obtiene una lista de usuarios con estado DISPONIBLE.
     *
     * @return una lista de usuarios disponibles
     */
    @Query("SELECT u FROM User u WHERE u.status = 'DISPONIBLE'")
    List<User> findAvailableUsers();

    /**
     * Busca usuarios que coincidan con una consulta en nombre, apellido, especialización o habilidades.
     *
     * @param query la cadena de búsqueda
     * @return una lista de usuarios que coinciden con la consulta
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "EXISTS (SELECT s FROM u.skills s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<User> searchUsers(@Param("query") String query);
}


