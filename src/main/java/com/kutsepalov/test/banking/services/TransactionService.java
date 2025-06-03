package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.enrichers.TransactionEnricher;
import com.kutsepalov.test.banking.entities.Transaction;
import com.kutsepalov.test.banking.mappers.TransactionMapper;
import com.kutsepalov.test.banking.repositories.TransactionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static com.kutsepalov.test.banking.dtos.Country.getCountryByCode;
import static com.kutsepalov.test.banking.dtos.transaction.TransactionType.*;
import static com.kutsepalov.test.banking.util.TimeUtil.nowUTC;
import static java.util.Optional.ofNullable;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final TransactionEnricher transactionEnricher;

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    private final Map<TransactionType, Consumer<TransactionDto>> operationHandlers = Map.of(
            DEPOSIT, this::handleDeposit,
            WITHDRAW, this::handleWithdraw,
            TRANSFER, this::handleTransfer
    );

    @Transactional(isolation = REPEATABLE_READ)
    public TransactionDto performOperation(@NonNull final TransactionType operation,
                                           @NonNull final String username,
                                           @NonNull TransactionDto transactionDto) {
        log.debug("Performing transaction[type={}] for account[iban={}]", operation, transactionDto.getSourceIban());
        validateTransaction(operation, transactionDto);

        transactionDto = saveTransaction(transactionEnricher.enrich(transactionDto, username));

        operationHandlers.get(operation).accept(transactionDto);

        return transactionDto;
    }

    private TransactionDto saveTransaction(TransactionDto transaction) {
        log.debug("Saving transaction[type={}] for account[iban={}]", transaction.getType(), transaction.getSourceIban());

        transaction.setTransactionDate(ofNullable(transaction.getTransactionDate()).orElse(nowUTC()));

        Transaction newTransaction = transactionRepository.save(transactionMapper.dtoToEntity(transaction));

        log.debug("Transaction saved with ID: {}", newTransaction.getId());
        return transactionMapper.entityToDto(newTransaction);
    }

    private void handleDeposit(TransactionDto transaction) {
        log.info("Handling deposit for account[iban={}]", transaction.getSourceIban());

        final AccountDto sourceAccount = transaction.getSourceAccount();
        final BigDecimal oldBalance = sourceAccount.getBalance();

        accountService.updateBalance(sourceAccount, oldBalance.add(transaction.getAmount()));
    }

    private void handleWithdraw(TransactionDto transaction) {
        log.info("Handling withdraw for account[iban={}]", transaction.getSourceIban());

        final AccountDto sourceAccount = transaction.getSourceAccount();
        final BigDecimal oldBalance = sourceAccount.getBalance();

        if (oldBalance.compareTo(transaction.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }

        accountService.updateBalance(sourceAccount, oldBalance.subtract(transaction.getAmount()));
    }

    private void handleTransfer(TransactionDto transaction) {
        log.info("Handling transfer from account[iban={}] to account[iban={}] ", transaction.getSourceIban(), transaction.getTargetIban());
        final AccountDto sourceAccount = transaction.getSourceAccount();
        final AccountDto targetAccount = transaction.getTargetAccount();

        final BigDecimal oldSourceBalance = sourceAccount.getBalance();
        accountService.updateBalance(sourceAccount, oldSourceBalance.subtract(transaction.getAmount()));

        final BigDecimal oldTargetBalance = targetAccount.getBalance();
        accountService.updateBalance(targetAccount, oldTargetBalance.add(transaction.getAmount()));

        if (oldSourceBalance.compareTo(transaction.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds for transfer");
        }
    }

    private void validateTransaction(TransactionType type, TransactionDto transactionDto) {
        final String sourceIbanStr = transactionDto.getSourceIban();
        final String targetIbanStr = transactionDto.getTargetIban();

        final CountryCode sourceCountry = Iban.valueOf(sourceIbanStr).getCountryCode();

        if (Objects.equals(sourceIbanStr, targetIbanStr)) {
            throw new IllegalArgumentException("Source and target IBANs cannot be the same");
        }
        if (TRANSFER.equals(type) && !sourceCountry.equals(Iban.valueOf(targetIbanStr).getCountryCode())) {
            throw new IllegalArgumentException("Source and target accounts must be in the same country");
        }
        if (getCountryByCode(sourceCountry).isEmpty()) {
            throw new IllegalArgumentException("Unsupported country for IBAN: " + sourceCountry);
        }
        if (getCountryByCode(sourceCountry).stream().noneMatch(c -> c.hasCurrency(transactionDto.getCurrency()))) {
            throw new IllegalArgumentException("Currency " + transactionDto.getCurrency() + " is not supported for country " + sourceCountry);
        }
    }
}
