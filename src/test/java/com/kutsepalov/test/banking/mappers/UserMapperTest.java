package com.kutsepalov.test.banking.mappers;

import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.entities.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void entityToDto_mapsFieldsCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");

        UserDto dto = userMapper.entityToDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getLastName(), dto.getLastName());
    }

    @Test
    void registrationRequestToEntity_mapsFieldsCorrectly() {
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setUsername("newuser");
        request.setFirstName("Alice");
        request.setLastName("Smith");

        User entity = userMapper.registrationRequestToEntity(request);

        assertEquals(request.getUsername(), entity.getUsername());
        assertEquals(request.getFirstName(), entity.getFirstName());
        assertEquals(request.getLastName(), entity.getLastName());
        // Password mapping may depend on your implementation (e.g., hashing)
    }
}