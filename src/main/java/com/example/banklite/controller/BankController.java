package com.example.banklite.controller;

import com.example.banklite.entity.Account;
import com.example.banklite.entity.Transaction;
import com.example.banklite.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("accounts", bankService.getAllAccounts());
        return "index";
    }

    @GetMapping("/web/accounts/new")
    public String createAccountForm() {
        return "create-account";
    }

    @PostMapping("/web/accounts")
    public String createAccountFromPage(@RequestParam String name,
                                        @RequestParam(defaultValue = "0") BigDecimal balance,
                                        RedirectAttributes redirectAttributes) {
        try {
            bankService.createAccount(name, balance);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully");
            return "redirect:/";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/web/accounts/new";
        }
    }

    @GetMapping("/web/deposit")
    public String depositForm(Model model) {
        model.addAttribute("accounts", bankService.getAllAccounts());
        return "deposit";
    }

    @PostMapping("/web/deposit")
    public String depositFromPage(@RequestParam Long accountId,
                                  @RequestParam BigDecimal amount,
                                  RedirectAttributes redirectAttributes) {
        try {
            bankService.deposit(accountId, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Deposit completed successfully");
            return "redirect:/";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/web/deposit";
        }
    }

    @GetMapping("/web/withdraw")
    public String withdrawForm(Model model) {
        model.addAttribute("accounts", bankService.getAllAccounts());
        return "withdraw";
    }

    @PostMapping("/web/withdraw")
    public String withdrawFromPage(@RequestParam Long accountId,
                                   @RequestParam BigDecimal amount,
                                   RedirectAttributes redirectAttributes) {
        try {
            bankService.withdraw(accountId, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Withdrawal completed successfully");
            return "redirect:/";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/web/withdraw";
        }
    }

    @GetMapping("/web/transfer")
    public String transferForm(Model model) {
        model.addAttribute("accounts", bankService.getAllAccounts());
        return "transfer";
    }

    @PostMapping("/web/transfer")
    public String transferFromPage(@RequestParam Long fromAccountId,
                                   @RequestParam Long toAccountId,
                                   @RequestParam BigDecimal amount,
                                   RedirectAttributes redirectAttributes) {
        try {
            bankService.transfer(fromAccountId, toAccountId, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Transfer completed successfully");
            return "redirect:/";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/web/transfer";
        }
    }

    @GetMapping("/web/accounts/{id}/transactions")
    public String transactionPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("account", bankService.getAccountById(id));
            model.addAttribute("transactions", bankService.getTransactionsForAccount(id));
            return "transactions";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/";
        }
    }

    @ResponseBody
    @PostMapping("/accounts")
    public Account createAccount(@RequestParam String name,
                                 @RequestParam(defaultValue = "0") BigDecimal balance) {
        return bankService.createAccount(name, balance);
    }

    @ResponseBody
    @PostMapping("/accounts/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return bankService.deposit(id, amount);
    }

    @ResponseBody
    @PostMapping("/accounts/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return bankService.withdraw(id, amount);
    }

    @ResponseBody
    @PostMapping("/transfer")
    public Map<String, String> transfer(@RequestParam Long fromAccountId,
                                        @RequestParam Long toAccountId,
                                        @RequestParam BigDecimal amount) {
        bankService.transfer(fromAccountId, toAccountId, amount);
        return Map.of("message", "Transfer completed successfully");
    }

    @ResponseBody
    @GetMapping("/accounts/{id}/transactions")
    public List<Transaction> transactions(@PathVariable Long id) {
        return bankService.getTransactionsForAccount(id);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleError(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }
}
