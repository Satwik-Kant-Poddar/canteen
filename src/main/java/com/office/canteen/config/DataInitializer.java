package com.office.canteen.config;

import com.office.canteen.domain.Role;
import com.office.canteen.domain.User;
import com.office.canteen.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new User("admin",        100L, "admin123", Role.ADMIN));
                userRepository.save(new User("john.doe",       1L, "pass123",  Role.EMPLOYEE));
                userRepository.save(new User("jane.smith",     2L, "pass123",  Role.EMPLOYEE));
                userRepository.save(new User("mike.wilson",    3L, "pass123",  Role.EMPLOYEE));
                userRepository.save(new User("sara.jones",     4L, "pass123",  Role.EMPLOYEE));
                userRepository.save(new User("raj.kumar",      5L, "pass123",  Role.EMPLOYEE));
            }
        };
    }
}
