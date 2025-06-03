package com.kutsepalov.test.banking.services;

import com.kutsepalov.test.banking.dtos.Country;
import com.kutsepalov.test.banking.dtos.account.AccountDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionRequestDto;
import com.kutsepalov.test.banking.dtos.transaction.TransactionType;
import com.kutsepalov.test.banking.dtos.user.RegistrationRequestDto;
import com.kutsepalov.test.banking.entities.User;
import com.kutsepalov.test.banking.mappers.TransactionMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "application.banking.bank-code=111111"
})
class TransactionServiceIntegrationTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    private String username;
    private AccountDto account1;
    private AccountDto account2;

    @BeforeEach
    void setUp() {
        RegistrationRequestDto registration = new RegistrationRequestDto();
        registration.setUsername("testuser");
        registration.setFirstName("Test");
        registration.setLastName("User");
        User user = userMapper.registrationRequestToEntity(registration);
        userRepository.save(user);
        username = registration.getUsername();

        account1 = new AccountDto();
        account1.setIban(generateIban(Country.DE));
        account1.setBalance(BigDecimal.valueOf(1000));
        account1.setAccountName("Account 1");
        account1 = accountService.createAccount(username, account1);

        account2 = new AccountDto();
        account2.setIban(generateIban(Country.DE));
        account2.setBalance(BigDecimal.valueOf(500));
        account2.setAccountName("Account 2");
        account2 = accountService.createAccount(username, account2);
    }

    @AfterEach
    void cleanup() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM TRANSACTIONS WHERE TRUE").executeUpdate();
        em.createNativeQuery("DELETE FROM ACCOUNTS WHERE TRUE").executeUpdate();
        em.createNativeQuery("DELETE FROM USERS WHERE TRUE").executeUpdate();
        em.getTransaction().commit();
    }


    @Test
    void deposit_increasesBalance() {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSourceIban(account1.getIban());
        request.setAmount(BigDecimal.valueOf(200));
        request.setCurrency("EUR");

        TransactionDto transactionDto = transactionMapper.requestToDto(request, TransactionType.DEPOSIT);
        TransactionDto result = transactionService.performOperation(TransactionType.DEPOSIT, username, transactionDto);

        AccountDto updated = accountService.getUserAccountByIban(username, account1.getIban());
        assertEquals(new BigDecimal("1200.00"), updated.getBalance());
        assertEquals(TransactionType.DEPOSIT, result.getType());
    }

    @Test
    void withdraw_decreasesBalance() {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSourceIban(account1.getIban());
        request.setAmount(BigDecimal.valueOf(300));
        request.setCurrency("EUR");

        TransactionDto transactionDto = transactionMapper.requestToDto(request, TransactionType.WITHDRAW);
        TransactionDto result = transactionService.performOperation(TransactionType.WITHDRAW, username, transactionDto);

        AccountDto updated = accountService.getUserAccountByIban(username, account1.getIban());
        assertEquals(new BigDecimal("700.00"), updated.getBalance());
        assertEquals(TransactionType.WITHDRAW, result.getType());
    }

    @Test
    void withdraw_insufficientFunds_throwsException() {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSourceIban(account1.getIban());
        request.setAmount(BigDecimal.valueOf(2000));
        request.setCurrency("EUR");

        TransactionDto transactionDto = transactionMapper.requestToDto(request, TransactionType.WITHDRAW);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.performOperation(TransactionType.WITHDRAW, username, transactionDto));
    }

    @Test
    void transfer_movesFundsBetweenAccounts() {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSourceIban(account1.getIban());
        request.setTargetIban(account2.getIban());
        request.setAmount(BigDecimal.valueOf(400));
        request.setCurrency("EUR");

        TransactionDto transactionDto = transactionMapper.requestToDto(request, TransactionType.TRANSFER);
        TransactionDto result = transactionService.performOperation(TransactionType.TRANSFER, username, transactionDto);

        AccountDto updatedSource = accountService.getUserAccountByIban(username, account1.getIban());
        AccountDto updatedTarget = accountService.getAccountByIban(account2.getIban());

        assertEquals(new BigDecimal("600.00"), updatedSource.getBalance());
        assertEquals(new BigDecimal("900.00"), updatedTarget.getBalance());
        assertEquals(TransactionType.TRANSFER, result.getType());
    }

    @Test
    void transfer_insufficientFunds_throwsException() {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSourceIban(account1.getIban());
        request.setTargetIban(account2.getIban());
        request.setAmount(BigDecimal.valueOf(2000));
        request.setCurrency("EUR");

        TransactionDto transactionDto = transactionMapper.requestToDto(request, TransactionType.TRANSFER);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.performOperation(TransactionType.TRANSFER, username, transactionDto));
    }

    private String generateIban(Country country) {
        return new Iban.Builder()
                .countryCode(country.getCountryCode())
                .buildRandom()
                .toString();
    }
}