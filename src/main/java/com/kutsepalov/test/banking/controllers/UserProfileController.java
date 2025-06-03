package com.kutsepalov.test.banking.controllers;

import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("banking/v1/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody RegistrationRequestDto registrationRequest) {
        return userService.register(registrationRequest);
    }
}
