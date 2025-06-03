package com.kutsepalov.test.banking.dtos.transaction;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public enum TransactionType {

    DEPOSIT("deposit"),
    WITHDRAW("withdraw"),
    TRANSFER("transfer");

    private final String operation;

    public static Optional<TransactionType> fromString(String operation) {
        return Arrays.stream(TransactionType.values())
                .filter(op -> op.operation.equalsIgnoreCase(operation))
                .findFirst();
    }
}
