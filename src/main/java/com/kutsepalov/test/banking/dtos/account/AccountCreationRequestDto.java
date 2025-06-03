package com.kutsepalov.test.banking.dtos.account;

import com.kutsepalov.test.banking.dtos.Country;
import jakarta.validation.constraints.DecimalMin;
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

    @NotNull(message = "Name cannot be null")
    private String accountName;

    @Digits(integer = 19, fraction = 2, message = """
            Balance must be a valid decimal number with up to 19 digits
            before the decimal point and 2 digits after the decimal point
            """)
    @DecimalMin(value = "0.00", message = "Initial balance must be at least 0.00")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @NotNull(message = "Country code cannot be null")
    private Country country;

    public CountryCode getCountryCode() {
        return country.getCountryCode();
    }
}
