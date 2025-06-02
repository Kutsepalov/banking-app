package com.kutsepalov.test.banking.dtos.account;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.CountryCode;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationRequestDto {

    @Nullable
    private String accountName;

    @Nullable
    @Digits(integer = 19, fraction = 2,message = """
            Balance must be a valid decimal number with up to 19 digits
            before the decimal point and 2 digits after the decimal point
            """)
    private BigDecimal initialBalance;

    @NotNull(message = "Country code cannot be null")
    private CountryCode country;
}
