package com.kutsepalov.test.banking.controllers;

import com.kutsepalov.test.banking.dtos.Country;
import com.kutsepalov.test.banking.dtos.account.AccountCreationRequestDto;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.mappers.AccountMapper;
import com.kutsepalov.test.banking.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("banking/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    @Value("${application.banking.bank-code}")
    private String bankCode;

    private final AccountMapper accountMapper;
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new bank account", responses = {
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public AccountDto createAccount(@RequestHeader("X-Username") String username,
                                    @RequestBody @Valid AccountCreationRequestDto accountCreationRequestDto) {
        return accountService.createAccount(username,
                accountMapper.creationRequestToDto(accountCreationRequestDto, bankCode));
    }

    @Operation(summary = "Get account details by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/{accountId}")
    public AccountDto getAccountDetails(@RequestHeader("X-Username") String username,
                                        @PathVariable UUID accountId) {
        return accountService.getAccountDetails(username, accountId);
    }

    @Operation(summary = "Get all accounts for the authenticated user", responses = {
            @ApiResponse(responseCode = "200", description = "List of accounts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping
    public List<AccountDto> getAccounts(@RequestHeader("X-Username") String username) {
        return accountService.getAllAccounts(username);
    }

    @Operation(summary = "Get all supported countries for account creation", responses = {
            @ApiResponse(responseCode = "200", description = "List of supported countries retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/supported-countries")
    public List<String> getSupportedCountries() {
        return Arrays.stream(Country.values())
                .map(country -> String.join(" - ", country.name(), country.getCountry()))
                .toList();
    }
}
