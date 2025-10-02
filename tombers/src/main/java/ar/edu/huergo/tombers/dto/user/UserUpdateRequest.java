package ar.edu.huergo.tombers.dto.user;

import java.util.List;

import ar.edu.huergo.tombers.entity.Skill;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName;

    private Integer age;
    private String availability;
    private String languages;
    private String specialization;
    private String phone;
    private String linkedin;
    private String github;
    private String portfolio;
    private String bio;
    private List<Skill> skills;
    private List<String> certifications;
    private List<String> interests;
}


