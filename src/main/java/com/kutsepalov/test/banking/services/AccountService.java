package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.entities.Account;
import com.kutsepalov.test.banking.mappers.AccountMapper;
import com.kutsepalov.test.banking.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * Service for managing bank accounts.
 * This service will handle operations related to bank accounts such as creating, retrieving, and managing accounts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserService userService;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    /**
     * Retrieves the details of a specific account for a given user.
     *
     * @param username the username of the user
     * @param accountId the ID of the account
     * @return the details of the account as an AccountDto
     */
    public AccountDto getAccountDetails(@NonNull String username, @NonNull UUID accountId) {
        final Long userId = userService.validateAndGet(username).getId();

        return accountRepository.findAccountByUserIdAndId(userId, accountId)
                .map(accountMapper::entityToDtoWithTransactions)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Account with id %s not found for %s", accountId, username)
                ));
    }

    /**
     * Retrieves a specific account by its IBAN for a given user.
     *
     * @param username the username of the user
     * @param iban the IBAN of the account
     * @return the AccountDto representing the account
     */
    public AccountDto getUserAccountByIban(@NonNull String username, @NonNull String iban) {
        final Long userId = userService.validateAndGet(username).getId();

        return accountRepository.findAccountByUserIdAndIban(userId, iban)
                .map(accountMapper::entityToDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found for IBAN: " + iban));
    }

    /**
     * Retrieves an account by its IBAN.
     *
     * @param iban the IBAN of the account
     * @return the AccountDto representing the account
     */
    public AccountDto getAccountByIban(@NonNull String iban) {
        return accountRepository.findAccountByIban(iban)
                .map(accountMapper::entityToDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found for IBAN: " + iban));
    }

    /**
     * Retrieves all accounts associated with a given user.
     *
     * @param username the username of the user
     * @return a list of AccountDto representing the user's accounts
     */
    public List<AccountDto> getAllAccounts(@NonNull String username) {
        final Long userId = userService.validateAndGet(username).getId();

        return accountRepository.findAccountsByUserId(userId).stream()
                .map(accountMapper::entityToDto)
                .toList();
    }

    /**
     * Creates a new bank account for a user.
     *
     * @param username the username of the user
     * @param accountDto the AccountDto containing account details
     * @return the created AccountDto
     */
    @Transactional(isolation = READ_COMMITTED)
    public AccountDto createAccount(@NonNull String username,
                                    @NonNull AccountDto accountDto) {
        log.debug("Creating account for user[username={}]", username);
        final UserDto userDto = userService.validateAndGet(username);

        accountDto.setUser(userDto);

        Account newAccount = accountRepository.save(accountMapper.dtoToEntity(accountDto));

        return accountMapper.entityToDto(newAccount);
    }


    public void updateBalance(@NonNull AccountDto sourceAccount,
                              @NonNull BigDecimal newBalance) {
        log.debug("Updating balance for account[iban={}]", sourceAccount.getIban());
        Account account = accountRepository.findById(sourceAccount.getId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + sourceAccount.getId()));

        account.setBalance(newBalance);
        sourceAccount.setBalance(newBalance);

        accountRepository.save(account);
    }
}
