package com.quantumluke.quantumshops.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.quantumluke.quantumshops.dto.UserDto;
import com.quantumluke.quantumshops.exceptions.AlreadyExistsException;
import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.User;
import com.quantumluke.quantumshops.request.CreateUserRequest;
import com.quantumluke.quantumshops.request.UpdateUserRequest;
import com.quantumluke.quantumshops.response.ApiResponse;
import com.quantumluke.quantumshops.services.user.IUserService;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

class UserControllerTests {
    private IUserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        userController = new UserController(userService);
    }

    @DisplayName("Should return user when found by ID")
    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        UserDto userDto = new UserDto();
        String expectedMessage = "User retrieved successfully";

        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.convertUserToDto(user)).thenReturn(userDto);

        // Act
        ResponseEntity<ApiResponse> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.getMessage());
        assertEquals(userDto, body.getData());
    }

    @DisplayName("Should return not found when user does not exist")
    @Test
    void getUserById_NonExistingUser_ReturnsNotFound() {
        // Arrange
        Long userId = 1L;
        String expectedMessage = "User not found";
        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException(expectedMessage));

        // Act
        ResponseEntity<ApiResponse> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.getMessage());
        assertNull(body.getData());
    }

    @DisplayName("Should return conflict when creating user that already exists")
    @Test
    void createUser_ExistingEmail_ReturnsConflict() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        String expectedMessage = "User already exists";
        when(userService.createUser(request)).thenThrow(new AlreadyExistsException(expectedMessage));

        // Act
        ResponseEntity<ApiResponse> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.getMessage());
        assertNull(body.getData());
    }

    @DisplayName("Should return created when user is created successfully")
    @Test
    void createUser_NewUser_ReturnsCreatedUser() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        User user = new User();
        UserDto userDto = new UserDto();
        String expectedMessage = "User created successfully";
        when(userService.createUser(request)).thenReturn(user);
        when(userService.convertUserToDto(user)).thenReturn(userDto);

        // Act
        ResponseEntity<ApiResponse> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.getMessage());
        assertEquals(userDto, body.getData());
    }

    @DisplayName("Should return not found when updating non-existent user")
    @Test
    void updateUser_NonExistingUser_ReturnsNotFound() {
        // Arrange
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        String expectedMessage = "User not found";
        when(userService.updateUser(userId, request)).thenThrow(new ResourceNotFoundException(expectedMessage));

        // Act
        ResponseEntity<ApiResponse> response = userController.updateUser(userId, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.getMessage());
        assertNull(body.getData());
    }

    @DisplayName("Should return not found when deleting non-existent user")
    @Test
    void deleteUser_NonExistingUser_ReturnsNotFound() {
        // Arrange
        Long userId = 1L;
        String expectedMessage = "User not found";
        doThrow(new ResourceNotFoundException(expectedMessage)).when(userService).deleteUser(userId);

        // Act
        ResponseEntity<ApiResponse> response = userController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.getMessage());
        assertNull(body.getData());
    }
}