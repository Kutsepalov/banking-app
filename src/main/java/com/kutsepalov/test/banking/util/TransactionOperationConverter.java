package com.kutsepalov.test.banking.util;

import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;

public class TransactionOperationConverter implements Converter<String, TransactionType> {

    @Override
    public TransactionType convert(@NonNull String operation) {
        return TransactionType.fromString(operation)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction operation. Supported operations: deposit, withdraw, transfer"));
    }
}
