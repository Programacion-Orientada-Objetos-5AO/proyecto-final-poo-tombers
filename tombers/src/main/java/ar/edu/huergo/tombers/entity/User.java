package ar.edu.huergo.tombers.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa a un usuario en el sistema Tombers.
 * Implementa UserDetails para la integración con Spring Security.
 * Contiene información personal, credenciales y roles del usuario.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del usuario.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Apellido del usuario.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Correo electrónico del usuario, único en el sistema.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Nombre de usuario único.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Contraseña encriptada del usuario.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Lista de habilidades del usuario.
     */
    @ElementCollection
    private List<Skill> skills;

    /**
     * Edad del usuario.
     */
    private Integer age;

    /**
     * Fecha de nacimiento del usuario.
     */
    @Column(name = "birth_date")
    private String birthDate;

    /**
     * Idiomas que habla el usuario.
     */
    private String languages;

    /**
     * Especialización del usuario.
     */
    private String specialization;

    /**
     * Número de teléfono del usuario.
     */
    private String phone;

    /**
     * Enlace a LinkedIn del usuario.
     */
    private String linkedin;

    /**
     * Enlace a GitHub del usuario.
     */
    private String github;

    /**
     * Enlace al portafolio del usuario.
     */
    private String portfolio;

    /**
     * Biografía del usuario.
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Estado actual del usuario.
     */
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * Lista de certificaciones del usuario.
     */

    @ElementCollection
    private List<String> certifications;

    /**
     * Lista de intereses del usuario.
     */
    @ElementCollection
    private List<String> interests;

    /**
     * Conjunto de roles asignados al usuario.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    /**
     * Lista de IDs de proyectos creados por el usuario.
     */
    @ElementCollection
    @CollectionTable(name = "user_created_projects", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "project_id")
    private List<Long> createdProjectIds;

    /**
     * Lista de IDs de proyectos que le gustan al usuario (likeados).
     */
    @ElementCollection
    @CollectionTable(name = "user_liked_projects", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "project_id")
    private List<Long> likedProjectIds;

    /**
     * Lista de IDs de proyectos que no le gustan al usuario (dislikeados).
     */
    @ElementCollection
    @CollectionTable(name = "user_disliked_projects", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "project_id")
    private List<Long> dislikedProjectIds;

    /**
     * Lista de IDs de proyectos en los que el usuario participa.
     */
    @ElementCollection
    @CollectionTable(name = "user_participating_projects", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "project_id")
    private List<Long> participatingProjectIds;



    /**
     * Fecha de creación del usuario.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización del usuario.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum que define los posibles estados de un usuario.
     */
    public enum UserStatus {
        DISPONIBLE, OCUPADO, INACTIVO
    }

    /**
     * Obtiene el nombre de usuario para autenticación, que es el correo electrónico.
     * @return El correo electrónico del usuario.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Obtiene el campo de nombre de usuario (distinto del username para autenticación).
     * @return El nombre de usuario único.
     */
    public String getUsernameField() {
        return username;
    }

    /**
     * Obtiene las autoridades (roles) del usuario para Spring Security.
     * @return Una colección de GrantedAuthority basada en los roles del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombre()))
            .collect(Collectors.toList());
    }

    /**
     * Verifica si la cuenta del usuario no ha expirado.
     * @return Siempre true, ya que no se implementa expiración de cuentas.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Verifica si la cuenta del usuario no está bloqueada.
     * @return Siempre true, ya que no se implementa bloqueo de cuentas.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    /**
     * Verifica si las credenciales del usuario no han expirado.
     * @return Siempre true, ya que no se implementa expiración de credenciales.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Verifica si el usuario está habilitado.
     * @return Siempre true, ya que no se implementa deshabilitación de usuarios.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
