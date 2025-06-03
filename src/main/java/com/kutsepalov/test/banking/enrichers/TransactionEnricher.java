package com.kutsepalov.test.banking.enrichers;

import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.services.AccountService;
import com.kutsepalov.test.banking.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.kutsepalov.test.banking.dtos.transaction.TransactionType.TRANSFER;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionEnricher {

    private UserService userService;
    private AccountService accountService;

    public TransactionDto enrich(@NonNull TransactionDto transactionDto, @NonNull String username) {
        log.debug("Enriching transaction[type={}]", transactionDto.getType());

        final UserDto userDto = userService.validateAndGet(username);
        transactionDto.setUser(userDto);

        ofNullable(transactionDto.getSourceIban()).ifPresentOrElse(
            iban -> transactionDto.setSourceAccount(accountService.getUserAccountByIban(username, iban)),
            () -> {
                throw new IllegalArgumentException("Source IBAN cannot be null");
            }
        );

        of(transactionDto)
                .filter(t -> TRANSFER.equals(t.getType()))
                .map(TransactionDto::getTargetIban)
                .ifPresentOrElse(
                        iban -> transactionDto.setTargetAccount(accountService.getAccountByIban(iban)),
                        () -> {
                            if (TRANSFER.equals(transactionDto.getType())) {
                                throw new IllegalArgumentException("Target IBAN cannot be null for transfer transactions");
                            }
                        });

        return transactionDto;
    }

}
