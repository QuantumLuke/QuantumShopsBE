package com.quantumluke.quantumshops.services.user;

import com.quantumluke.quantumshops.exceptions.AlreadyExistsException;
import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.User;
import com.quantumluke.quantumshops.repository.UserRepository;
import com.quantumluke.quantumshops.request.CreateUserRequest;
import com.quantumluke.quantumshops.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService{
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req ->{
                    User newUser = new User();
                    newUser.setFirstName(req.getFirstName());
                    newUser.setLastName(req.getLastName());
                    newUser.setEmail(req.getEmail());
                    newUser.setPassword(req.getPassword());
                    return userRepository.save(newUser);
                }).orElseThrow(() -> new AlreadyExistsException("User with email " + request.getEmail() + " already exists."));
    }

    @Override
    public User updateUser(Long userId, UpdateUserRequest request) {
        return userRepository.findById(userId).map(existingUser ->{
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(
                userRepository::delete,
                () -> { throw new ResourceNotFoundException("User not found with id: " + userId);}
        );
    }
}
