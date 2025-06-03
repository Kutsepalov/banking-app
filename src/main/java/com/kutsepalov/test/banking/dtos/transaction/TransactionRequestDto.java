package com.kutsepalov.test.banking.dtos.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {

    @NotNull(message = "Source account ID cannot be null")
    private String sourceIban;
    private String targetIban;

    @Digits(integer = 19, fraction = 2, message = """
            Amount must be a valid decimal number with up to 19 digits
            before the decimal point and 2 digits after the decimal point
            """)
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
    private String currency;
}
