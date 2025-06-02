package com.kutsepalov.test.banking.mappers;

import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.entities.Account;
import org.mapstruct.Mapping;

import java.util.List;

public interface AccountMapper {

    /**
     * Converts an Account entity to an AccountDto.
     * Provides transactions data.
     * This method is not safe for recursive calls
     *
     * @param entity the Account entity to convert
     * @return the converted AccountDto
     */
    AccountDto entityToDto(Account entity);

    /**
     * Converts a list of Account entities to a list of AccountDto.
     * This method ignores the transactions field to prevent N+1 query issues
     *
     * @param entity the list of Account entities to convert
     * @return the converted list of AccountDto
     */
    @Mapping(target = "transactions", ignore = true)
    List<AccountDto> entitiesToDtos(List<Account> entity);
}
