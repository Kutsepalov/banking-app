package com.kutsepalov.test.banking.mappers;

import com.kutsepalov.test.banking.dtos.account.AccountCreationRequestDto;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.entities.Account;
import com.kutsepalov.test.banking.util.TimeUtil;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {TransactionMapper.class, TimeUtil.class}
)
public interface AccountMapper {

    /**
     * Converts an Account entity to an AccountDto.
     * This method is safe for recursive calls
     *
     * @param entity the Account entity to convert
     * @return the converted AccountDto
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    AccountDto entityToDto(Account entity);

    /**
     * Converts an Account entity to an AccountDto.
     * Provides transactions data.
     * This method is not safe for recursive calls
     *
     * @param entity the Account entity to convert
     * @return the converted AccountDto
     */
    @Mapping(target = "user", ignore = true)
    AccountDto entityToDtoWithTransactions(Account entity);

    /**
     * Converts an AccountDto to an Account entity.
     *
     * @param dto the AccountDto to convert
     * @return the converted Account entity
     */
    @Mapping(target = "transactions", ignore = true)
    Account dtoToEntity(AccountDto dto);

    /**
     * Converts an AccountCreationRequestDto to an AccountDto.
     *
     * @param requestDto the AccountCreationRequestDto to convert
     * @return the converted AccountDto
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "iban", expression = "java(generateIban(requestDto.getCountryCode(), bankCode))")
    @Mapping(target = "balance", source = "requestDto.initialBalance")
    @Mapping(target = "accountName", source = "requestDto.accountName")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    AccountDto creationRequestToDto(AccountCreationRequestDto requestDto, String bankCode);

    default String generateIban(CountryCode countryCode, String bankCode) {
        return new Iban.Builder()
                .countryCode(countryCode)
                .bankCode(bankCode)
                .buildRandom()
                .toString();
    }
}
