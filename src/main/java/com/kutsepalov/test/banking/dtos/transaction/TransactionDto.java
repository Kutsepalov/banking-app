package com.kutsepalov.test.banking.dtos.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private UUID id;
    private String sourceIban;
    private String targetIban;
    private BigDecimal amount;
    private TransactionType type;
    private String currency;
    private ZonedDateTime transactionDate;

    @JsonProperty("sender")
    private UserDto user;
    private AccountDto sourceAccount;
    private AccountDto targetAccount;

}
