package ar.edu.huergo.tombers.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.huergo.tombers.dto.project.InterestedUsersResponse;
import ar.edu.huergo.tombers.dto.project.ManageInterestedRequest;
import ar.edu.huergo.tombers.dto.project.ProjectCreateRequest;
import ar.edu.huergo.tombers.dto.project.ProjectResponse;
import ar.edu.huergo.tombers.dto.project.ProjectMemberSummary;
import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.entity.Project;
import ar.edu.huergo.tombers.entity.Skill;
import ar.edu.huergo.tombers.entity.User;
import ar.edu.huergo.tombers.mapper.ProjectMapper;
import ar.edu.huergo.tombers.mapper.UserMapper;
import ar.edu.huergo.tombers.repository.ProjectRepository;
import ar.edu.huergo.tombers.repository.UserRepository;

import ar.edu.huergo.tombers.service.storage.FileStorageService;
import ar.edu.huergo.tombers.service.storage.StorageDirectory;
import ar.edu.huergo.tombers.service.storage.StoredFile;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    /**
     * Obtiene una lista de todos los proyectos disponibles en el sistema.
     *
     * @return una lista de objetos ProjectResponse que representan todos los proyectos
     */
    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(this::buildDetailedResponse)
                .toList();
    }

    /**
     * Obtiene un proyecto especÃ­fico por su identificador.
     *
     * @param id el identificador del proyecto
     * @return un objeto ProjectResponse que representa el proyecto encontrado
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
        return buildDetailedResponse(project);
    }

    /**
     * Crea un nuevo proyecto basado en la solicitud proporcionada.
     *
     * @param request la solicitud de creacion del proyecto
     * @param bannerFile archivo con el banner del proyecto
     * @return un objeto ProjectResponse que representa el proyecto creado
     */
    public ProjectResponse createProject(ProjectCreateRequest request, MultipartFile bannerFile) {
        if (bannerFile == null || bannerFile.isEmpty()) {
            throw new IllegalArgumentException("El banner del proyecto es obligatorio");
        }

        String userEmail = getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Project project = projectMapper.toEntity(request);
        project.setStatus(resolveStatus(request.getStatus()));
        project.setProgress(resolveProgress(request.getProgress()));
        project.setObjectives(sanitizeStringList(project.getObjectives()));
        project.setTechnologies(sanitizeStringList(project.getTechnologies()));
        project.setSkillsNeeded(sanitizeSkills(project.getSkillsNeeded()));
        if (project.getTeamCurrent() == null) {
            project.setTeamCurrent(0);
        }
        project.setCreatorId(user.getId());

        StoredFile storedBanner = fileStorageService.store(bannerFile, StorageDirectory.PROJECT_BANNER);
        project.setBannerUrl(storedBanner.publicUrl());
        project.setCreatedAt(LocalDate.now());
        project.setUpdatedAt(LocalDate.now());

        Project savedProject = projectRepository.save(project);

        if (user.getCreatedProjectIds() == null) {
            user.setCreatedProjectIds(new ArrayList<>());
        }
        if (!user.getCreatedProjectIds().contains(savedProject.getId())) {
            user.getCreatedProjectIds().add(savedProject.getId());
        }
        userRepository.save(user);

        return buildDetailedResponse(savedProject);
    }

    /**
     * Actualiza un proyecto existente con la informacion proporcionada.
     *
     * @param id el identificador del proyecto a actualizar
     * @param request la solicitud de actualizacion del proyecto
     * @param bannerFile archivo opcional con un nuevo banner para el proyecto
     * @return un objeto ProjectResponse que representa el proyecto actualizado
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public ProjectResponse updateProject(Long id, ProjectCreateRequest request, MultipartFile bannerFile) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        String previousBannerUrl = project.getBannerUrl();

        projectMapper.updateEntity(project, request);

        if (request.getStatus() != null) {
            project.setStatus(resolveStatus(request.getStatus()));
        } else if (project.getStatus() == null) {
            project.setStatus(Project.ProjectStatus.ACTIVE);
        }

        if (request.getProgress() != null) {
            project.setProgress(resolveProgress(request.getProgress()));
        } else if (project.getProgress() == null) {
            project.setProgress(0);
        }

        project.setObjectives(sanitizeStringList(project.getObjectives()));
        project.setTechnologies(sanitizeStringList(project.getTechnologies()));
        project.setSkillsNeeded(sanitizeSkills(project.getSkillsNeeded()));
        project.setUpdatedAt(LocalDate.now());

        if (bannerFile != null && !bannerFile.isEmpty()) {
            StoredFile storedBanner = fileStorageService.store(bannerFile, StorageDirectory.PROJECT_BANNER);
            String newBannerUrl = storedBanner.publicUrl();
            project.setBannerUrl(newBannerUrl);
            if (previousBannerUrl != null && !previousBannerUrl.equals(newBannerUrl)) {
                fileStorageService.deleteByPublicUrl(previousBannerUrl);
            }
        }

        Project updatedProject = projectRepository.save(project);
        return buildDetailedResponse(updatedProject);
    }

    /**
     * Elimina un proyecto por su identificador.
     *
     * @param id el identificador del proyecto a eliminar
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void deleteProject(Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        String projectBannerUrl = project.getBannerUrl();

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getNombre().equalsIgnoreCase("ADMIN"));
        boolean isOwner = user.getCreatedProjectIds() != null && user.getCreatedProjectIds().contains(id);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("No tiene permisos para eliminar este proyecto");
        }

        projectRepository.deleteById(id);
        fileStorageService.deleteByPublicUrl(projectBannerUrl);
    }

    /**
     * Permite a un usuario dar like a un proyecto.
     * Agrega el ID del usuario a la lista de likes del proyecto y
     * agrega el ID del proyecto a la lista de proyectos likeados del usuario.
     *
     * @param projectId el identificador del proyecto al que se le quiere dar like
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void likeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Inicializar listas si son null
        if (user.getLikedProjectIds() == null) {
            user.setLikedProjectIds(new ArrayList<>());
        }
        if (project.getLikeIds() == null) {
            project.setLikeIds(new ArrayList<>());
        }

        // Verificar que el usuario no haya dado like ya
        if (user.getLikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario ya le dio like a este proyecto");
        }

        // Verificar que el usuario no haya dado dislike (no puede tener ambos)
        if (user.getDislikedProjectIds() != null && user.getDislikedProjectIds().contains(projectId)) {
            user.getDislikedProjectIds().remove(projectId);
        }

        // Agregar like: usuario -> proyecto y proyecto -> usuario
        user.getLikedProjectIds().add(projectId);
        project.getLikeIds().add(user.getId());

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

    /**
     * Permite a un usuario quitar el like de un proyecto.
     * Remueve el ID del usuario de la lista de likes del proyecto y
     * remueve el ID del proyecto de la lista de proyectos likeados del usuario.
     *
     * @param projectId el identificador del proyecto del que se quiere quitar el like
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void unlikeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Verificar que el usuario haya dado like previamente
        if (user.getLikedProjectIds() == null || !user.getLikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario no le ha dado like a este proyecto");
        }

        // Remover like: usuario -> proyecto y proyecto -> usuario
        user.getLikedProjectIds().remove(projectId);
        if (project.getLikeIds() != null) {
            project.getLikeIds().remove(user.getId());
        }

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

    /**
     * Permite a un usuario dar dislike a un proyecto.
     * Agrega el ID del usuario a la lista de dislikes del proyecto y
     * agrega el ID del proyecto a la lista de proyectos dislikeados del usuario.
     *
     * @param projectId el identificador del proyecto al que se le quiere dar dislike
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void dislikeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Inicializar listas si son null
        if (user.getDislikedProjectIds() == null) {
            user.setDislikedProjectIds(new ArrayList<>());
        }

        // Verificar que el usuario no haya dado dislike ya
        if (user.getDislikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario ya le dio dislike a este proyecto");
        }

        // Verificar que el usuario no haya dado like (no puede tener ambos)
        if (user.getLikedProjectIds() != null && user.getLikedProjectIds().contains(projectId)) {
            user.getLikedProjectIds().remove(projectId);
            if (project.getLikeIds() != null) {
                project.getLikeIds().remove(user.getId());
            }
        }

        // Agregar dislike: usuario -> proyecto
        user.getDislikedProjectIds().add(projectId);

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

    /**
     * Permite a un usuario quitar el dislike de un proyecto.
     * Remueve el ID del proyecto de la lista de proyectos dislikeados del usuario.
     *
     * @param projectId el identificador del proyecto del que se quiere quitar el dislike
     * @throws EntityNotFoundException si el proyecto no existe
     */
    public void undislikeProject(Long projectId) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Verificar que el usuario haya dado dislike previamente
        if (user.getDislikedProjectIds() == null || !user.getDislikedProjectIds().contains(projectId)) {
            throw new IllegalArgumentException("El usuario no le ha dado dislike a este proyecto");
        }

        // Remover dislike: usuario -> proyecto
        user.getDislikedProjectIds().remove(projectId);

        // Guardar cambios
        userRepository.save(user);
        projectRepository.save(project);
    }

    /**
     * Permite al creador o admin de un proyecto ver la lista de usuarios interesados.
     * Los interesados son usuarios que dieron like al proyecto pero no son miembros.
     *
     * @param projectId el identificador del proyecto
     * @return un objeto InterestedUsersResponse con la lista de usuarios interesados
     * @throws EntityNotFoundException si el proyecto no existe
     * @throws AccessDeniedException si el usuario no es el creador o admin del proyecto
     */
    public InterestedUsersResponse getInterestedUsers(Long projectId) {
        // Verificar si el proyecto existe primero
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Obtener el usuario autenticado con validaciones adicionales
        String userEmail = getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el usuario es el creador del proyecto o admin
        boolean isOwnerOrAdmin = isUserOwnerOrAdmin(user, projectId);

        if (!isOwnerOrAdmin) {
            throw new AccessDeniedException("Solo el creador o admin del proyecto puede ver los usuarios interesados. Usuario: " + user.getEmail() + ", Proyecto: " + projectId);
        }

        // Obtener usuarios interesados (quienes dieron like pero no son miembros)
        List<Long> interestedUserIds = new ArrayList<>();
        if (project.getLikeIds() != null) {
            for (Long userId : project.getLikeIds()) {
                if (project.getMemberIds() == null || !project.getMemberIds().contains(userId)) {
                    interestedUserIds.add(userId);
                }
            }
        }

        // Convertir IDs a UserResponse
        List<User> interestedUsers = userRepository.findAllById(interestedUserIds);
        List<UserResponse> userResponses = interestedUsers.stream()
                .map(userMapper::toDto)
                .toList();

        return InterestedUsersResponse.builder()
                .projectId(projectId)
                .projectTitle(project.getTitle())
                .interestedUsers(userResponses)
                .totalInterested(interestedUserIds.size())
                .build();
    }

    /**
     * Permite al creador o admin de un proyecto aceptar o rechazar a un usuario interesado.
     * Si acepta: el usuario se convierte en miembro del proyecto y se actualizan las listas correspondientes.
     * Si rechaza: se remueve el like del usuario.
     *
     * @param projectId el identificador del proyecto
     * @param request DTO con el ID del usuario y la acciÃ³n (ACCEPT/REJECT)
     * @throws EntityNotFoundException si el proyecto o usuario no existen
     * @throws AccessDeniedException si el usuario no es el creador o admin del proyecto
     * @throws IllegalArgumentException si el usuario no estÃ¡ en la lista de interesados
     */
    public void manageInterestedUser(Long projectId, ManageInterestedRequest request) {
        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        // Verificar si el usuario es el creador del proyecto o admin
        boolean isOwnerOrAdmin = isUserOwnerOrAdmin(user, projectId);

        if (!isOwnerOrAdmin) {
            throw new AccessDeniedException("Solo el creador o admin del proyecto puede gestionar usuarios interesados. Usuario: " + user.getEmail() + ", Proyecto: " + projectId);
        }

        // Obtener el usuario interesado
        User interestedUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario interesado no encontrado"));

        // Verificar que el usuario estÃ© en la lista de interesados
        if (project.getLikeIds() == null || !project.getLikeIds().contains(request.getUserId())) {
            throw new IllegalArgumentException("El usuario no estÃ¡ en la lista de interesados de este proyecto");
        }

        if (request.getAction() == ManageInterestedRequest.Action.ACCEPT) {
            // Aceptar: agregar como miembro
            if (project.getMemberIds() == null) {
                project.setMemberIds(new ArrayList<>());
            }
            if (!project.getMemberIds().contains(request.getUserId())) {
                project.getMemberIds().add(request.getUserId());
            }

            // Agregar proyecto a la lista de proyectos participando del usuario
            if (interestedUser.getParticipatingProjectIds() == null) {
                interestedUser.setParticipatingProjectIds(new ArrayList<>());
            }
            if (!interestedUser.getParticipatingProjectIds().contains(projectId)) {
                interestedUser.getParticipatingProjectIds().add(projectId);
            }

            // Actualizar contador de miembros actuales
            if (project.getTeamCurrent() == null) {
                project.setTeamCurrent(0);
            }
            project.setTeamCurrent(project.getTeamCurrent() + 1);

        } else if (request.getAction() == ManageInterestedRequest.Action.REJECT) {
            // Rechazar: remover el like
            project.getLikeIds().remove(request.getUserId());
            interestedUser.getLikedProjectIds().remove(projectId);
        }

        // Guardar cambios
        userRepository.save(interestedUser);
        projectRepository.save(project);
    }

    /**
     * MÃ©todo auxiliar para obtener el email del usuario autenticado con validaciones adicionales
     */
    /**
     * Convierte el estado recibido en el request a la enumeración de la entidad.
     */
    private ProjectResponse buildDetailedResponse(Project project) {
        Long creatorId = resolveCreatorId(project);
        ProjectResponse response = projectMapper.toResponse(project);
        response.setCreatorId(creatorId);
        response.setMembers(resolveMembers(project, creatorId));
        return response;
    }

    private Long resolveCreatorId(Project project) {
        if (project.getCreatorId() != null) {
            return project.getCreatorId();
        }
        if (project.getId() == null) {
            return null;
        }
        return userRepository.findByProjectId(project.getId())
                .map(User::getId)
                .orElse(null);
    }

    private List<ProjectMemberSummary> resolveMembers(Project project, Long creatorId) {
        Set<Long> participantIds = new LinkedHashSet<>();
        if (creatorId != null) {
            participantIds.add(creatorId);
        }
        if (project.getMemberIds() != null) {
            participantIds.addAll(project.getMemberIds());
        }
        if (participantIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> users = userRepository.findAllById(participantIds);
        Map<Long, User> usersById = users.stream()
                .filter(user -> user != null && user.getId() != null)
                .collect(Collectors.toMap(User::getId, Function.identity(), (existing, replacement) -> existing));

        List<ProjectMemberSummary> members = new ArrayList<>();
        for (Long userId : participantIds) {
            boolean isCreator = creatorId != null && creatorId.equals(userId);
            User member = usersById.get(userId);

            if (member == null) {
                members.add(ProjectMemberSummary.builder()
                        .id(userId)
                        .fullName(isCreator ? "Creador sin datos" : "Integrante sin datos")
                        .creator(isCreator)
                        .build());
                continue;
            }

            String firstName = StringUtils.hasText(member.getFirstName()) ? member.getFirstName().trim() : "";
            String lastName = StringUtils.hasText(member.getLastName()) ? member.getLastName().trim() : "";
            String fullName = (firstName + " " + lastName).trim();
            if (!StringUtils.hasText(fullName)) {
                String username = member.getUsernameField();
                fullName = StringUtils.hasText(username) ? username : member.getEmail();
            }

            members.add(ProjectMemberSummary.builder()
                    .id(member.getId())
                    .fullName(fullName)
                    .email(member.getEmail())
                    .profilePictureUrl(member.getProfilePictureUrl())
                    .creator(isCreator)
                    .build());
        }

        return members;
    }

    private Project.ProjectStatus resolveStatus(ProjectCreateRequest.ProjectStatus status) {
        if (status == null) {
            return Project.ProjectStatus.ACTIVE;
        }
        return Project.ProjectStatus.valueOf(status.name());
    }

    /**
     * Normaliza el avance asegurando que quede en el rango 0 - 100.
     */
    private int resolveProgress(Integer progress) {
        if (progress == null) {
            return 0;
        }
        int sanitized = Math.min(100, Math.max(0, progress));
        return sanitized;
    }

    /**
     * Elimina elementos vacíos y espacios extra de las listas de texto.
     */
    private List<String> sanitizeStringList(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        return values.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Asegura que las habilidades cuenten con nombre y nivel válidos.
     */
    private List<Skill> sanitizeSkills(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .filter(skill -> skill != null && StringUtils.hasText(skill.getNombre()))
                .map(skill -> Skill.builder()
                        .nombre(skill.getNombre().trim())
                        .nivel(StringUtils.hasText(skill.getNivel()) ? skill.getNivel().trim() : "Intermedio")
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Método auxiliar para obtener el email del usuario autenticado con validaciones adicionales.
     */
    private String getAuthenticatedUserEmail() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new AccessDeniedException("Usuario no autenticado");
            }
            String email = authentication.getName();
            if (!StringUtils.hasText(email)) {
                throw new AccessDeniedException("Email del usuario autenticado no encontrado");
            }
            return email.trim();
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (Exception e) {
            throw new AccessDeniedException("Error al obtener usuario autenticado: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para verificar si un usuario es dueño de un proyecto.
     */
    private boolean isUserOwnerOfProject(User user, Long projectId) {
        if (user == null || projectId == null) {
            return false;
        }

        if (user.getCreatedProjectIds() != null) {
            return user.getCreatedProjectIds().contains(projectId);
        }

        return false;
    }

    /**
     * Método auxiliar para verificar si un usuario es dueño de un proyecto o admin.
     */
    private boolean isUserOwnerOrAdmin(User user, Long projectId) {
        if (user == null || projectId == null) {
            return false;
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getNombre()));

        boolean isOwner = user.getCreatedProjectIds() != null && user.getCreatedProjectIds().contains(projectId);

        return isAdmin || isOwner;
    }

}
