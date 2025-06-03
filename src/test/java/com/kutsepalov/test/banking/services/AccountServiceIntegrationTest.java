package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.Country;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.entities.User;
import com.kutsepalov.test.banking.mappers.UserMapper;
import com.kutsepalov.test.banking.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.iban4j.Iban;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "application.banking.bank-code=111111"
})
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private String username;

    @BeforeEach
    void setUp() {
        // Register a user for testing
        RegistrationRequestDto registration = new RegistrationRequestDto();
        registration.setUsername("testuser");
        registration.setFirstName("Test");
        registration.setLastName("User");

        User user = userMapper.registrationRequestToEntity(registration);
        userRepository.save(user);
        username = registration.getUsername();
    }

    @AfterEach
    void cleanup() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM ACCOUNTS WHERE TRUE").executeUpdate();
        em.createNativeQuery("DELETE FROM USERS WHERE TRUE").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    void createAndRetrieveAccount_success() {
        // Create account
        AccountDto request = new AccountDto();
        request.setIban(generateIban(Country.DE));
        request.setBalance(BigDecimal.valueOf(500));
        request.setAccountName("Test Account");

        // Use AccountService to create account
        AccountDto accountDto = accountService.createAccount(username, request);

        assertNotNull(accountDto.getId());
        assertEquals("Test Account", accountDto.getAccountName());
        assertEquals(BigDecimal.valueOf(500), accountDto.getBalance());
        assertTrue(accountDto.getIban().startsWith("DE"));

        // Retrieve account by IBAN
        AccountDto found = accountService.getUserAccountByIban(username, accountDto.getIban());
        assertEquals(accountDto.getId(), found.getId());
        assertEquals(accountDto.getIban(), found.getIban());
    }

    @Test
    void getAllAccounts_returnsCreatedAccounts() {
        // Create accounts
        AccountDto account1 = new AccountDto();
        account1.setIban(generateIban(Country.LV));
        account1.setBalance(BigDecimal.valueOf(100));
        account1.setAccountName("Test Account 1");

        AccountDto account2 = new AccountDto();
        account2.setIban(generateIban(Country.LV));
        account2.setBalance(BigDecimal.valueOf(200));
        account2.setAccountName("Test Account 2");

        // Use AccountService to create account
        accountService.createAccount(username, account1);
        accountService.createAccount(username, account2);

        List<AccountDto> accounts = accountService.getAllAccounts(username);
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(a -> Objects.nonNull(a.getId())));
    }

    private String generateIban(Country country) {
        return new Iban.Builder()
                .countryCode(country.getCountryCode())
                .buildRandom()
                .toString();
    }
}