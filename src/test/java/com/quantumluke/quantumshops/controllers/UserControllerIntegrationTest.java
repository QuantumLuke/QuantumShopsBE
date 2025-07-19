package com.quantumluke.quantumshops.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.quantumluke.quantumshops.request.CreateUserRequest;
import com.quantumluke.quantumshops.request.UpdateUserRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturnCreatedStatus() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }

    @Test
    void createUser_withDuplicateEmail_shouldReturnConflict() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        // Create first user
        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try to create second user with same email
        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with email duplicate@example.com already exists."));
    }

    @Test
    void createUser_withMissingFields_shouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(""); // Empty email
        request.setPassword("<PASSWORD>");

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getUserById_shouldReturnUser() throws Exception {
        
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("123456");

        MvcResult result = mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        
        String responseBody = result.getResponse().getContentAsString();
        long userId = ((Integer)JsonPath.read(responseBody, "$.data.id")).longValue();

        
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void getUserById_nonExistent_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void updateUser_shouldSuccess() throws Exception{
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setFirstName("Mike");
        createRequest.setLastName("Smith");
        createRequest.setEmail("mike@smith.com");
        createRequest.setPassword("<PASSWORD>");

        MvcResult result = mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        long userId = ((Integer)JsonPath.read(responseBody, "$.data.id")).longValue();

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Doe");

        mockMvc.perform(put("/api/v1/users/" + userId + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }

    @Test
    void updateUser_nonExistent_shouldReturnNotFound() throws Exception{
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Doe");

        mockMvc.perform(put("/api/v1/users/999/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }
    
    @Test
    void deleteUser_shouldSuccess() throws Exception{
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("delete@example.com");
        request.setPassword("password123");
        request.setFirstName("Delete");
        request.setLastName("User");

        MvcResult result = mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        long userId = ((Integer) JsonPath.read(responseBody, "$.data.id")).longValue();

        mockMvc.perform(delete("/api/v1/users/" + userId + "/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_nonExistent_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/users/999/delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void createUser_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withMissingPassword_shouldReturnBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_withEmptyFields_shouldReturnBadRequest() throws Exception {
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setEmail("test@example.com");
        createRequest.setPassword("password123");
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");

        MvcResult result = mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        long userId = ((Integer) JsonPath.read(responseBody, "$.data.id")).longValue();

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("");
        updateRequest.setLastName("");

        mockMvc.perform(put("/api/v1/users/" + userId + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

}
