package com.kutsepalov.test.banking.mappers;

import com.kutsepalov.test.banking.dtos.Country;
import com.kutsepalov.test.banking.dtos.account.AccountCreationRequestDto;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.entities.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountMapperTest {

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    void entityToDto_mapsFieldsCorrectly() {
        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setIban("IBAN123");
        account.setBalance(BigDecimal.TEN);
        account.setAccountName("Test Account");

        AccountDto dto = accountMapper.entityToDto(account);

        assertEquals(account.getId(), dto.getId());
        assertEquals(account.getIban(), dto.getIban());
        assertEquals(account.getBalance(), dto.getBalance());
        assertEquals(account.getAccountName(), dto.getAccountName());
        assertNull(dto.getUser());
        assertNull(dto.getTransactions());
    }

    @Test
    void dtoToEntity_mapsFieldsCorrectly() {
        AccountDto dto = new AccountDto();
        dto.setId(UUID.randomUUID());
        dto.setIban("IBAN456");
        dto.setBalance(BigDecimal.ONE);
        dto.setAccountName("Another Account");

        Account entity = accountMapper.dtoToEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getIban(), entity.getIban());
        assertEquals(dto.getBalance(), entity.getBalance());
        assertEquals(dto.getAccountName(), entity.getAccountName());
        assertNull(entity.getTransactions());
    }

    @Test
    void creationRequestToDto_generatesIbanAndMapsFields() {
        AccountCreationRequestDto request = new AccountCreationRequestDto();
        request.setCountry(Country.DE);
        request.setInitialBalance(BigDecimal.valueOf(100));
        request.setAccountName("New Account");

        String bankCode = "12345678";
        AccountDto dto = accountMapper.creationRequestToDto(request, bankCode);

        assertNotNull(dto.getIban());
        assertTrue(dto.getIban().startsWith("DE"));
        assertEquals(request.getInitialBalance(), dto.getBalance());
        assertEquals(request.getAccountName(), dto.getAccountName());
        assertNull(dto.getUser());
        assertNull(dto.getTransactions());
    }
}