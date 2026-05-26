package com.example.banklite.service;

import com.example.banklite.entity.Account;
import com.example.banklite.entity.Transaction;
import com.example.banklite.repository.AccountRepository;
import com.example.banklite.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    public List<Transaction> getTransactionsForAccount(Long accountId) {
        getAccountById(accountId);
        return transactionRepository.findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(accountId, accountId);
    }

    public Account createAccount(String name, BigDecimal openingBalance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name is required");
        }
        if (openingBalance == null) {
            openingBalance = BigDecimal.ZERO;
        }
        if (openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }

        Account account = new Account();
        account.setName(name.trim());
        account.setBalance(openingBalance);
        account.setCreatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        checkPositiveAmount(amount);

        Account account = getAccountById(accountId);
        account.setBalance(account.getBalance().add(amount));

        transactionRepository.save(new Transaction(null, accountId, amount, "DEPOSIT"));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        checkPositiveAmount(amount);

        Account account = getAccountById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Cannot withdraw more than current balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        transactionRepository.save(new Transaction(accountId, null, amount, "WITHDRAW"));
        return accountRepository.save(account);
    }

    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        checkPositiveAmount(amount);

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("From and to accounts must be different");
        }

        Account fromAccount = getAccountById(fromAccountId);
        Account toAccount = getAccountById(toAccountId);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Sender does not have enough balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transactionRepository.save(new Transaction(fromAccountId, toAccountId, amount, "TRANSFER"));
    }

    private void checkPositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
