package com.kutsepalov.test.banking.controllers;

import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionRequestDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.mappers.TransactionMapper;
import com.kutsepalov.test.banking.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("banking/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @PostMapping("/{operation}")
    public TransactionDto performOperation(@RequestHeader("X-Username") String username,
                                           @PathVariable("operation") TransactionType operation,
                                           @RequestBody @Valid TransactionRequestDto requestDto) {
        TransactionDto transactionDto = transactionMapper.requestToDto(requestDto, operation);
        return transactionService.performOperation(operation, username, transactionDto);
    }
}
