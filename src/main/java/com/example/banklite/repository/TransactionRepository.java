package com.example.banklite.repository;

import com.example.banklite.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(Long fromAccountId, Long toAccountId);
}
