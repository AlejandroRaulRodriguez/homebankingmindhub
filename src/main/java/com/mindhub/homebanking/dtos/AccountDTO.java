package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountDTO {
    private long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;
    private Set<TransactionDTO> transaction = new HashSet<>();
    private boolean disable;
    private AccountType type;

    public AccountDTO(){}

    public AccountDTO(Account account){
        this.id = account.getId();
        this.number = account.getNumber();
        this.creationDate = account.getCreationDate();
        this.balance = account.getBalance();
        this.transaction = account.getTransactions().stream().map(transaction -> new TransactionDTO(transaction)).collect(Collectors.toSet());
        this.disable = account.isDisable();
        this.type = account.getType();
    }

    public long getId(){return id;}

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Set<TransactionDTO> getTransaction() {
        return transaction;
    }
    public void setTransaction(Set<TransactionDTO> transaction) {
        this.transaction = transaction;
    }

    public boolean isDisable() {
        return disable;
    }
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public AccountType getType() {return type;}
    public void setType(AccountType type) {this.type = type;}
}
