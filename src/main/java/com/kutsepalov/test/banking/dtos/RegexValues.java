package com.kutsepalov.test.banking.dtos;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RegexValues {

    public static final String NAME = "^[a-zA-Z]+$";
    public static final String USERNAME = "^[a-zA-Z0-9._-]{4,20}$";
    public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$";

}
