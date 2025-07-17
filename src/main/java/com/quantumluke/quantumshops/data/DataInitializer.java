package com.quantumluke.quantumshops.data;

import com.quantumluke.quantumshops.models.User;
import com.quantumluke.quantumshops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createDefaultUserIfNotExists();
    }

    private void createDefaultUserIfNotExists() {
        for (int i = 0; i <= 5; i++){
            String defaultEmail = "user"+i+"@email.com";
            if (userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("user"+i);
            user.setLastName("user"+i);
            user.setEmail(defaultEmail);
            user.setPassword("password"+i);
            userRepository.save(user);
            System.out.println("Created default user "+defaultEmail);
        }
    }
}
