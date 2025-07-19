package com.quantumluke.quantumshops.services.user;

import com.quantumluke.quantumshops.dto.UserDto;
import com.quantumluke.quantumshops.exceptions.AlreadyExistsException;
import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.User;
import com.quantumluke.quantumshops.repository.UserRepository;
import com.quantumluke.quantumshops.request.CreateUserRequest;
import com.quantumluke.quantumshops.request.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private User testUser;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encoded_password");

        createRequest = new CreateUserRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john@example.com");
        createRequest.setPassword("password");

        updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe Updated");
    }

    @Test
    void getUserById_existingUser_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    void getUserById_nonExistingUser_returnsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void createUser_newUser_returnsCreatedUser() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(testUser);

        User result = userService.createUser(createRequest);

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void createUser_existingEmail_returnsConflict() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> userService.createUser(createRequest));
    }

    @Test
    void updateUser_existingUser_returnsUpdatedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenReturn(testUser);

        User result = userService.updateUser(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getFirstName(), result.getFirstName());
    }

    @Test
    void updateUser_nonExistingUser_returnsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateRequest));
    }

    @Test
    void deleteUser_existingUser_deletesSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_nonExistingUser_returnsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void convertUserToDto_validUser_returnsUserDto() {
        UserDto userDto = new UserDto();
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.convertUserToDto(testUser);

        assertNotNull(result);
        verify(modelMapper).map(testUser, UserDto.class);
    }

    @Test
    void getAuthenticatedUser_ValidUser_ReturnsUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);

        User result = userService.getAuthentivatedUser();

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }
}
