package com.kutsepalov.test.banking.repositories;

import com.kutsepalov.test.banking.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findAccountsByUserId(Long userId);
    Optional<Account> findAccountByUserIdAndId(Long userId, UUID id);
    Optional<Account> findAccountByUserIdAndIban(Long userId, String iban);
    Optional<Account> findAccountByIban(String iban);
}
