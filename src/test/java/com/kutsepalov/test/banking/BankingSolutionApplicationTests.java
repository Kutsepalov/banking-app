package com.kutsepalov.test.banking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "application.banking.bank-code=111111"
})
class BankingSolutionApplicationTests {

    @Test
    void contextLoads() {
        // This test is used to check if the application context loads successfully.
        // No additional assertions are needed as a failure to load the context will
        // automatically fail this test.
    }
}
