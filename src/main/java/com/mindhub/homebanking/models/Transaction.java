package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime date;
    private double currentBalance;
    private boolean disable;
    private LocalDate localDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_id")
    private Account account;

    public Transaction() {
    }

    public Transaction(TransactionType type, double amount, String description, LocalDateTime date, Account account, double currentBalance, boolean disable, LocalDate localDate) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.account = account;
        this.currentBalance = currentBalance;
        this.disable = disable;
        this. localDate = localDate;
    }

    public long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

    public double getCurrentBalance() {return currentBalance;}
    public void setCurrentBalance(double currentBalance) {this.currentBalance = currentBalance;}

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}


