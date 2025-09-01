package ar.edu.huergo.tombers.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ar.edu.huergo.tombers.dto.user.UserResponse;
import ar.edu.huergo.tombers.dto.user.UserUpdateRequest;
import ar.edu.huergo.tombers.entity.User;

public class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user;
    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .build();

        updateRequest = UserUpdateRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .build();
    }

    @Test
    public void testToDto() {
        UserResponse result = userMapper.toDto(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
    }

    @Test
    public void testToEntity() {
        User result = userMapper.toEntity(userResponse);

        assertNotNull(result);
        assertNull(result.getId()); // Should be ignored
        assertEquals(userResponse.getEmail(), result.getEmail());
        assertEquals(userResponse.getUsername(), result.getUsername());
        assertEquals(userResponse.getFirstName(), result.getFirstName());
        assertEquals(userResponse.getLastName(), result.getLastName());
    }

    @Test
    public void testUpdateEntity() {
        User target = new User();
        target.setId(1L);
        target.setFirstName("Original");
        target.setLastName("Original");

        userMapper.updateEntity(target, updateRequest);

        assertEquals("Updated", target.getFirstName());
        assertEquals("User", target.getLastName());
        assertEquals(1L, target.getId()); // Should not be updated
    }
}
