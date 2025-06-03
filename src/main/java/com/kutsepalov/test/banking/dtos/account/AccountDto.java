package com.kutsepalov.test.banking.dtos.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {

    private UUID id;
    private String iban;
    private String accountName;
    private BigDecimal balance;

    private UserDto user;
    private List<TransactionDto> transactions;

}
