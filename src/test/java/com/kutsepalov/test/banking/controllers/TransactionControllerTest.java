package com.kutsepalov.test.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionRequestDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.mappers.TransactionMapper;
import com.kutsepalov.test.banking.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "application.banking.bank-code=111111"
})
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private TransactionMapper transactionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void performDeposit_returnsTransactionDto() throws Exception {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setSourceIban("SRC123");
        requestDto.setAmount(BigDecimal.valueOf(100));
        requestDto.setCurrency("EUR");

        TransactionDto mappedDto = new TransactionDto();
        mappedDto.setId(UUID.randomUUID());
        mappedDto.setSourceIban("SRC123");
        mappedDto.setAmount(BigDecimal.valueOf(100));
        mappedDto.setType(TransactionType.DEPOSIT);
        mappedDto.setCurrency("EUR");

        when(transactionMapper.requestToDto(ArgumentMatchers.any(), ArgumentMatchers.eq(TransactionType.DEPOSIT)))
                .thenReturn(mappedDto);
        when(transactionService.performOperation(TransactionType.DEPOSIT, "testuser", mappedDto))
                .thenReturn(mappedDto);

        mockMvc.perform(post("/banking/v1/transactions/DEPOSIT")
                        .header("X-Username", "testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceIban").value("SRC123"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }
}