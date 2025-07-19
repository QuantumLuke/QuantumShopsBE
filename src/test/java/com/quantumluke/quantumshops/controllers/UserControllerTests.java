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

    @DisplayName("Get user by ID returns user successfully")
    @Test
    void getUserByIdReturnsUserSuccessfully() {
        Long userId = 1L;
        User mockUser = new User();
        UserDto mockUserDto = new UserDto();
        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(userService.convertUserToDto(mockUser)).thenReturn(mockUserDto);

        ResponseEntity<ApiResponse> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User retrieved successfully", response.getBody().getMessage());
        assertEquals(mockUserDto, response.getBody().getData());
    }

    @DisplayName("Get user by ID returns not found when user does not exist")
    @Test
    void getUserByIdReturnsNotFoundWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<ApiResponse> response = userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @DisplayName("Create user returns conflict when user already exists")
    @Test
    void createUserReturnsConflictWhenUserAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        when(userService.createUser(request)).thenThrow(new AlreadyExistsException("User already exists"));

        ResponseEntity<ApiResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User already exists", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @DisplayName("Create user return successful")
    @Test
    void createUserReturnsSuccessful() {
        CreateUserRequest request = new CreateUserRequest();
        User mockUser = new User();
        UserDto mockUserDto = new UserDto();
        when(userService.createUser(request)).thenReturn(mockUser);
        when(userService.convertUserToDto(mockUser)).thenReturn(mockUserDto);

        ResponseEntity<ApiResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertEquals(mockUserDto, response.getBody().getData());
    }

    @DisplayName("Update user returns not found when user does not exist")
    @Test
    void updateUserReturnsNotFoundWhenUserDoesNotExist() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        when(userService.updateUser(userId, request)).thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<ApiResponse> response = userController.updateUser(userId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @DisplayName("Delete user returns not found when user does not exist")
    @Test
    void deleteUserReturnsNotFoundWhenUserDoesNotExist() {
        Long userId = 1L;
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(userId);

        ResponseEntity<ApiResponse> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}
