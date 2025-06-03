package com.kutsepalov.test.banking.controllers;

import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionRequestDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.mappers.TransactionMapper;
import com.kutsepalov.test.banking.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("banking/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @Operation(summary = "Perform a transaction operation (transfer, deposit, withdraw)", responses = {
            @ApiResponse(responseCode = "200", description = "Transaction performed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PostMapping("/{operation}")
    public TransactionDto performOperation(@RequestHeader("X-Username") String username,
                                           @PathVariable("operation") TransactionType operation,
                                           @RequestBody @Valid TransactionRequestDto requestDto) {
        TransactionDto transactionDto = transactionMapper.requestToDto(requestDto, operation);
        return transactionService.performOperation(operation, username, transactionDto);
    }
}
