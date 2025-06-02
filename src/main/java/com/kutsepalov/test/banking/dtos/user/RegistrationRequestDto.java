package com.kutsepalov.test.banking.dtos.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.kutsepalov.test.banking.dtos.RegexValues.NAME;
import static com.kutsepalov.test.banking.dtos.RegexValues.USERNAME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {

    @NotNull(message = "First name cannot be null")
    @Pattern(regexp = NAME, message = "First name can only contain letters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Pattern(regexp = NAME, message = "Last name can only contain letters")
    private String lastName;

    @NotNull(message = "Username cannot be null")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Pattern(regexp = USERNAME, message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;
}
