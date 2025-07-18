package com.quantumluke.quantumshops.data;

import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.Role;
import com.quantumluke.quantumshops.models.User;
import com.quantumluke.quantumshops.repository.RoleRepository;
import com.quantumluke.quantumshops.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        createDefaultRoleIfNotExists(defaultRoles);
        createDefaultUsersIfNotExists();
    }

    private void createDefaultUsersIfNotExists() {
        List<Role> roles = roleRepository.findAll();
        for (int i = 0; i <= 5; i++){
            String defaultEmail = i == 0 ? "admin"+i+"@email.com" : "user"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("user"+i);
            user.setLastName("user"+i);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("password"+i));
            String roleName = i == 0 ? "ROLE_ADMIN" : "ROLE_USER";

            user.setRoles(Set.of(roles.stream()
                    .filter(role -> role.getName().equals(roleName))
                    .findFirst()
                    .orElseThrow( () -> new ResourceNotFoundException("Role not found with name " + roleName))));

            userRepository.save(user);
            System.out.println("Created default user "+defaultEmail);
        }
    }

    private void createDefaultRoleIfNotExists(Set<String> roles) {
        roles.stream()
                .filter(role -> roleRepository.findByName(role).isEmpty())
                .map(Role:: new).forEach(roleRepository::save);
    }
}
