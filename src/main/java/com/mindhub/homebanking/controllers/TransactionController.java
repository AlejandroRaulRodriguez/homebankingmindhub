package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@RestController
@RequestMapping("/api")
public class TransactionController {

    public TransactionController(){}

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

@Transactional
@PostMapping("/clients/current/transactions")
public ResponseEntity<Object> newTransaction(
        @RequestParam Double amount, @RequestParam String description,
        @RequestParam String numberAccountOrigin, @RequestParam String numberAccountTarget,
        Authentication authentication){

    Client currentClient = clientService.getClientByEmail(authentication.getName());
    Set<Account> accountsCurrentClient = currentClient.getAccounts();

    Account OriginAccount = accountService.getAccount(numberAccountOrigin);
    Account TargetAccount = accountService.getAccount(numberAccountTarget);


    if(amount.isNaN() || amount <= 0 || amount.isInfinite()) {
        return new ResponseEntity<>("Invalid amount.", HttpStatus.FORBIDDEN);
    }

    if (description.isEmpty() || numberAccountOrigin.isEmpty() || numberAccountTarget.isEmpty()) {
        return new ResponseEntity<>("Missing data.", HttpStatus.FORBIDDEN);
    }

    if(numberAccountOrigin.equals(numberAccountTarget)){
        return new ResponseEntity<>("A transfer cannot be made to the same account.", HttpStatus.FORBIDDEN);
    }

    if(OriginAccount == null){
        return new ResponseEntity<>("The origin account does not exist.", HttpStatus.FORBIDDEN);
    }

    if(!accountsCurrentClient.contains(OriginAccount)){
        return new ResponseEntity<>("The origin account does not belong to you.", HttpStatus.FORBIDDEN);
    }

    if (TargetAccount == null || TargetAccount.isDisable()){
        return new ResponseEntity<>("The destination account does not exist.", HttpStatus.FORBIDDEN);
    }

    if(OriginAccount.getBalance() < amount){
        return new ResponseEntity<>("You do not have sufficient funds to complete the transfer.", HttpStatus.FORBIDDEN);
    }

    Transaction transactionOriginAccount = new Transaction(TransactionType.DEBIT,amount,description + " " + numberAccountTarget, LocalDateTime.now(),OriginAccount,OriginAccount.getBalance() - amount,false, LocalDate.now());
    transactionService.saveTransaction(transactionOriginAccount);
    OriginAccount.setBalance(OriginAccount.getBalance() - amount);

    Transaction transactionTargetAccount = new Transaction(TransactionType.CREDIT,amount,description + " " + numberAccountOrigin, LocalDateTime.now(),TargetAccount,TargetAccount.getBalance() + amount,false,LocalDate.now());
    transactionService.saveTransaction(transactionTargetAccount);
    TargetAccount.setBalance(TargetAccount.getBalance() + amount);

    return new ResponseEntity<>("Successful transaction.", HttpStatus.CREATED);
}
}
