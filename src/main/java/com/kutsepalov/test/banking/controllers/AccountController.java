package com.kutsepalov.test.banking.controllers;

import com.kutsepalov.test.banking.dtos.account.AccountCreationRequestDto;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("banking/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountDto createAccount(@RequestHeader("X-User-Id") Long userId,
                                    @RequestBody AccountCreationRequestDto accountCreationRequestDto) {
        // Logic to create an account
        return accountService.createAccount(userId, accountCreationRequestDto);
    }

    @GetMapping("/{accountId}")
    public AccountDto getAccountDetails(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable UUID accountId) {
        // Logic to retrieve account details
        return accountService.getAccountDetails(userId, accountId);
    }

    @GetMapping
    public List<AccountDto> getAccounts(@RequestHeader("X-User-Id") Long userId) {
        return accountService.getAllAccounts(userId);
    }

    @GetMapping("/supported-countries")
    public List<String> getSupportedCountries() {
        return Arrays.stream(CountryCode.values())
                .map(country -> String.join(" - ", country.getAlpha2(), country.getName()))
                .toList();
    }

}
