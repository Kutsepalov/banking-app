package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.entities.User;
import com.kutsepalov.test.banking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for managing user accounts, including registration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registrationRequest the registration request containing user details
     * @return UserDto containing the registered user's information
     */
    public UserDto register(RegistrationRequestDto registrationRequest) {
        log.info("Registering new user with username: {}", registrationRequest.getUsername());
        validateRegistration(registrationRequest);

        User user = User.builder()
                .username(registrationRequest.getUsername())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .build();

        user = userRepository.save(user);

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private void validateRegistration(RegistrationRequestDto registrationRequest) {
        log.debug("Validating registration request for username: {}", registrationRequest.getUsername());
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
    }
}
