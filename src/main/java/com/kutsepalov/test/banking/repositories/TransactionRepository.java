package com.kutsepalov.test.banking.repositories;

import com.kutsepalov.test.banking.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
