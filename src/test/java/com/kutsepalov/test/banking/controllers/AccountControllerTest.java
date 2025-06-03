package com.kutsepalov.test.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kutsepalov.test.banking.dtos.Country;
import com.kutsepalov.test.banking.dtos.account.AccountCreationRequestDto;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.mappers.AccountMapper;
import com.kutsepalov.test.banking.services.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "application.banking.bank-code=111111"
})
@WebMvcTest(AccountController.class)
class AccountControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AccountMapper accountMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_returnsAccountDto() throws Exception {
        AccountCreationRequestDto creationRequest = new AccountCreationRequestDto();
        creationRequest.setCountry(Country.DE);
        creationRequest.setInitialBalance(BigDecimal.valueOf(100));
        creationRequest.setAccountName("Test Account");

        AccountDto mappedDto = new AccountDto();
        mappedDto.setAccountName("Test Account");
        mappedDto.setBalance(BigDecimal.valueOf(100));
        mappedDto.setIban("DE1234567890");

        when(accountMapper.creationRequestToDto(ArgumentMatchers.any(), ArgumentMatchers.anyString()))
                .thenReturn(mappedDto);
        when(accountService.createAccount(ArgumentMatchers.eq("testuser"), ArgumentMatchers.any()))
                .thenReturn(mappedDto);

        mockMvc.perform(post("/banking/v1/accounts")
                        .header("X-Username", "testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName").value("Test Account"))
                .andExpect(jsonPath("$.balance").value(100))
                .andExpect(jsonPath("$.iban").value("DE1234567890"));
    }

    @Test
    void getAccountDetails_returnsAccountDto() throws Exception {
        UUID accountId = UUID.randomUUID();
        AccountDto dto = new AccountDto();
        dto.setId(accountId);
        dto.setAccountName("Account 1");
        dto.setBalance(BigDecimal.valueOf(200));
        dto.setIban("DE9876543210");

        when(accountService.getAccountDetails("testuser", accountId)).thenReturn(dto);

        mockMvc.perform(get("/banking/v1/accounts/{accountId}", accountId)
                        .header("X-Username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.accountName").value("Account 1"))
                .andExpect(jsonPath("$.balance").value(200))
                .andExpect(jsonPath("$.iban").value("DE9876543210"));
    }

    @Test
    void getAccounts_returnsListOfAccountDto() throws Exception {
        AccountDto dto1 = new AccountDto();
        dto1.setId(UUID.randomUUID());
        dto1.setAccountName("A1");
        dto1.setBalance(BigDecimal.valueOf(50));
        dto1.setIban("DE111");

        AccountDto dto2 = new AccountDto();
        dto2.setId(UUID.randomUUID());
        dto2.setAccountName("A2");
        dto2.setBalance(BigDecimal.valueOf(150));
        dto2.setIban("DE222");

        when(accountService.getAllAccounts("testuser")).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/banking/v1/accounts")
                        .header("X-Username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountName").value("A1"))
                .andExpect(jsonPath("$[1].accountName").value("A2"));
    }

    @Test
    void getSupportedCountries_returnsCountryList() throws Exception {
        mockMvc.perform(get("/banking/v1/accounts/supported-countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}