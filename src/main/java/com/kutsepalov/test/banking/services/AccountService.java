package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.account.AccountCreationRequestDto;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.entities.Account;
import com.kutsepalov.test.banking.mappers.AccountMapper;
import com.kutsepalov.test.banking.repositories.AccountRepository;
import com.kutsepalov.test.banking.repositories.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * Service for managing bank accounts.
 * This service will handle operations related to bank accounts such as creating, retrieving, and managing accounts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    @Value("${application.banking.bank-code}")
    private String bankCode;

    private final UserRepository userService;

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    /**
     * Retrieves the details of a specific account for a given user.
     *
     * @param userId    the ID of the user
     * @param accountId the ID of the account
     * @return the details of the account as an AccountDto
     */
    public AccountDto getAccountDetails(@NonNull Long userId, @NonNull UUID accountId) {
        validateUser(userId);
        return accountRepository.findAccountByUserIdAndId(userId, accountId)
                .map(accountMapper::entityToDto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found for userId: " + userId + " and accountId: " + accountId
                ));
    }

    /**
     * Retrieves all accounts associated with a given user.
     *
     * @param userId the ID of the user
     * @return a list of AccountDto representing the user's accounts
     */
    public List<AccountDto> getAllAccounts(@NonNull Long userId) {
        validateUser(userId);
        return accountMapper.entitiesToDtos(accountRepository.findAccountsByUserId(userId));
    }

    /**
     * Creates a new bank account for a user.
     *
     * @param userId    the ID of the user
     * @param requestDto the details of the account to be created
     * @return the created AccountDto
     */
    @Transactional(isolation = READ_COMMITTED)
    public AccountDto createAccount(@NonNull Long userId,
                                    @NonNull AccountCreationRequestDto requestDto) {
        validateUser(userId);

        Account newAccount = accountRepository.save(Account.builder()
                .accountName(requestDto.getAccountName())
                .balance(requestDto.getInitialBalance())
                .iban(generateIban(requestDto.getCountry()))
                .userId(userId)
                .build());

        return accountMapper.entityToDto(newAccount);
    }

    private String generateIban(CountryCode countryCode) {
        return new Iban.Builder()
                .countryCode(countryCode)
                .bankCode(bankCode)
                .buildRandom()
                .toString();
    }

    private void validateUser(Long userId) {
        if (!userService.existsById(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
    }
}
