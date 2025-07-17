package com.quantumluke.quantumshops.services.user;

import com.quantumluke.quantumshops.dto.UserDto;
import com.quantumluke.quantumshops.models.User;
import com.quantumluke.quantumshops.request.CreateUserRequest;
import com.quantumluke.quantumshops.request.UpdateUserRequest;

public interface IUserService {

    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);
}
