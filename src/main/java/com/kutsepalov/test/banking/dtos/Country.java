package com.kutsepalov.test.banking.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.iban4j.CountryCode;

import java.util.Arrays;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum Country {

    LV(CountryCode.LV, "EUR"),
    UA(CountryCode.UA, "UAH"),
    US(CountryCode.US, "USD"),
    DE(CountryCode.DE, "EUR"),
    ES(CountryCode.ES, "EUR"),
    FR(CountryCode.FR, "EUR"),
    IT(CountryCode.IT, "EUR");

    private final CountryCode countryCode;
    private final String currencyCode;

    public static Country getDefaultCountry() {
        return US;
    }

    public String getCountry() {
        return countryCode.getName();
    }

    public static Optional<Country> getCountryByCode(CountryCode countryCode) {
        return Arrays.stream(values())
                .filter(country -> country.countryCode.equals(countryCode))
                .findFirst();
    }

    public boolean hasCurrency(String currencyCode) {
        return this.currencyCode.equals(currencyCode);
    }
}
