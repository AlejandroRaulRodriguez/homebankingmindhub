package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.Utils.getRandomNumber;


@RestController
@RequestMapping("/api")
public class AccountController {

    public AccountController() {}

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountService.getAccountsDto();
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount (@PathVariable Long id){
        return accountService.getAccountDto(id);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(@RequestParam AccountType type, Authentication authentication) {

        Client currentClient = clientService.getClientByEmail(authentication.getName());

        Set<Account> accountsCurrentClient = currentClient.getAccounts().stream().filter(account -> !account.isDisable()).collect(Collectors.toSet());

        if (accountsCurrentClient.size() >= 3){
            return new ResponseEntity<>("You already have the maximum number of possible accounts (3).", HttpStatus.FORBIDDEN);}

        Account newAccount = new Account("VIN-" + getRandomNumber(10000000,99999999), LocalDateTime.now(),0,currentClient,false,type);
        accountService.saveAccount(newAccount);

        return new ResponseEntity<>("Account created successfully.", HttpStatus.CREATED);
    };

    @PatchMapping("/clients/current/accounts/{id}")
    public ResponseEntity<Object> deleteAccount (@PathVariable long id, Authentication authentication) {

        Client currentClient = clientService.getClientByEmail(authentication.getName());

        Set<Account> accountsCurrentClient = currentClient.getAccounts().stream().filter(account -> !account.isDisable()).collect(Collectors.toSet());

        Account accountToDelete = accountService.getAccount(id);

        Set<Transaction> transactionsAccountToDelete = accountToDelete.getTransactions();

        if (accountsCurrentClient.size() == 1) {
            return new ResponseEntity<>("You can't delete your only account", HttpStatus.FORBIDDEN);
        }

        if (!accountsCurrentClient.contains(accountToDelete)) {
            return new ResponseEntity<>("This account does not exist", HttpStatus.FORBIDDEN);
        }

        if (accountToDelete.getBalance() > 0){
            return new ResponseEntity<>("You cannot delete an account with a balance greater than $0", HttpStatus.FORBIDDEN);
        }

        transactionsAccountToDelete.forEach(transaction -> transaction.setDisable(true));
        transactionsAccountToDelete.forEach(transaction -> transactionService.saveTransaction(transaction));

        accountToDelete.setDisable(true);
        accountService.saveAccount(accountToDelete);
        return new ResponseEntity<>("Account deleted successfully", HttpStatus.ACCEPTED);
    }
}
