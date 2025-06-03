package com.kutsepalov.test.banking.enrichers;

import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import com.kutsepalov.test.banking.services.AccountService;
import com.kutsepalov.test.banking.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionEnricherTest {

    @Mock
    private UserService userService;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionEnricher transactionEnricher;

    @Test
    void enrich_transferTransaction_success() {
        String username = "user";
        String sourceIban = "SRC123";
        String targetIban = "TGT456";

        UserDto userDto = new UserDto();
        AccountDto sourceAccount = new AccountDto();
        AccountDto targetAccount = new AccountDto();

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setType(TransactionType.TRANSFER);
        transactionDto.setSourceIban(sourceIban);
        transactionDto.setTargetIban(targetIban);

        when(userService.validateAndGet(username)).thenReturn(userDto);
        when(accountService.getUserAccountByIban(username, sourceIban)).thenReturn(sourceAccount);
        when(accountService.getAccountByIban(targetIban)).thenReturn(targetAccount);

        TransactionDto enriched = transactionEnricher.enrich(transactionDto, username);

        assertEquals(userDto, enriched.getUser());
        assertEquals(sourceAccount, enriched.getSourceAccount());
        assertEquals(targetAccount, enriched.getTargetAccount());
    }

    @Test
    void enrich_missingSourceIban_throwsException() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setType(TransactionType.DEPOSIT);

        assertThrows(IllegalArgumentException.class, () ->
                transactionEnricher.enrich(transactionDto, "user"));
    }

    @Test
    void enrich_transferMissingTargetIban_throwsException() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setType(TransactionType.TRANSFER);
        transactionDto.setSourceIban("SRC123");

        when(userService.validateAndGet(anyString())).thenReturn(new UserDto());
        when(accountService.getUserAccountByIban(anyString(), anyString())).thenReturn(new AccountDto());

        assertThrows(IllegalArgumentException.class, () ->
                transactionEnricher.enrich(transactionDto, "user"));
    }
}