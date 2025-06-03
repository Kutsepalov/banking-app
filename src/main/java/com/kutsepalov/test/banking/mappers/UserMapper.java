package com.kutsepalov.test.banking.mappers;

import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user the User entity to convert
     * @return the converted UserDto
     */
    UserDto entityToDto(User user);

    /**
     * Converts a RegistrationRequestDto to a User entity.
     *
     * @param registrationRequest the RegistrationRequestDto to convert
     * @return the converted User entity
     */
    User registrationRequestToEntity(RegistrationRequestDto registrationRequest);
}
