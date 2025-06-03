package com.kutsepalov.test.banking.controllers;

import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Register a new user", responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PostMapping("/register")
    public UserDto register(@RequestBody @Valid RegistrationRequestDto registrationRequest) {
        return userService.register(registrationRequest);
    }
}
