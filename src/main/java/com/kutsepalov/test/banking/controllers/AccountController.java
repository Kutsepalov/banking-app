package com.kutsepalov.test.banking.controllers;

import com.kutsepalov.test.banking.dtos.Country;
import com.kutsepalov.test.banking.dtos.account.AccountCreationRequestDto;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.mappers.AccountMapper;
import com.kutsepalov.test.banking.services.AccountService;
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
    public AccountDto createAccount(@RequestHeader("X-Username") String username,
                                    @RequestBody @Valid AccountCreationRequestDto accountCreationRequestDto) {
        return accountService.createAccount(username,
                accountMapper.creationRequestToDto(accountCreationRequestDto, bankCode));
    }

    @GetMapping("/{accountId}")
    public AccountDto getAccountDetails(@RequestHeader("X-Username") String username,
                                        @PathVariable UUID accountId) {
        return accountService.getAccountDetails(username, accountId);
    }

    @GetMapping
    public List<AccountDto> getAccounts(@RequestHeader("X-Username") String username) {
        return accountService.getAllAccounts(username);
    }

    @GetMapping("/supported-countries")
    public List<String> getSupportedCountries() {
        return Arrays.stream(Country.values())
                .map(country -> String.join(" - ", country.name(), country.getCountry()))
                .toList();
    }

}
