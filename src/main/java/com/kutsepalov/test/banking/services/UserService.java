package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.entities.User;
import com.kutsepalov.test.banking.mappers.UserEntityMapper;
import com.kutsepalov.test.banking.repositories.UserRepository;
import lombok.NonNull;
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
    private final UserEntityMapper userEntityMapper;

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registrationRequest the registration request containing user details
     * @return UserDto containing the registered user's information
     */
    public UserDto register(@NonNull RegistrationRequestDto registrationRequest) {
        log.info("Registering new user with username: {}", registrationRequest.getUsername());
        validateRegistration(registrationRequest);

        User newUser = userRepository.save(
                userEntityMapper.registrationRequestToEntity(registrationRequest)
        );

        return userEntityMapper.entityToDto(newUser);
    }

    private void validateRegistration(RegistrationRequestDto registrationRequest) {
        log.debug("Validating registration request for username: {}", registrationRequest.getUsername());
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
    }
}
