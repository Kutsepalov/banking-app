package com.kutsepalov.test.banking.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
