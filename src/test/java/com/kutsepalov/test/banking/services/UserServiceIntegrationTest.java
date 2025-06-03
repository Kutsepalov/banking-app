package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.mappers.UserMapper;
import com.kutsepalov.test.banking.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "application.banking.bank-code=111111"
})
class UserServiceIntegrationTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @AfterEach
    void cleanup() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM USERS WHERE TRUE").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    void register_and_retrieve_user_success() {
        RegistrationRequestDto registration = new RegistrationRequestDto();
        registration.setUsername("testuser");
        registration.setFirstName("Test");
        registration.setLastName("User");

        UserDto userDto = userService.register(registration);

        assertNotNull(userDto.getId());
        assertEquals("testuser", userDto.getUsername());
        assertEquals("Test", userDto.getFirstName());
        assertEquals("User", userDto.getLastName());

        UserDto found = userService.validateAndGet("testuser");
        assertEquals(userDto.getId(), found.getId());
    }

    @Test
    void register_duplicateUsername_throwsException() {
        RegistrationRequestDto registration = new RegistrationRequestDto();
        registration.setUsername("duplicate");
        registration.setFirstName("First");
        registration.setLastName("Last");

        userService.register(registration);

        RegistrationRequestDto duplicate = new RegistrationRequestDto();
        duplicate.setUsername("duplicate");
        duplicate.setFirstName("Other");
        duplicate.setLastName("User");

        assertThrows(IllegalArgumentException.class, () -> userService.register(duplicate));
    }

    @Test
    void validateAndGet_nonexistentUser_throwsException() {
        assertThrows(RuntimeException.class, () -> userService.validateAndGet("notfound"));
    }
}