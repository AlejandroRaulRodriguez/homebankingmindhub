package com.mindhub.homebanking.dtos;

public class LoanApplicationDTO {

    private long id;
    private Double amount;
    private Integer payments;
    private String accountDestination;

    public LoanApplicationDTO(){}

    public LoanApplicationDTO(long id, Double amount, Integer payments, String accountDestination) {
        this.id = id;
        this.amount = amount;
        this.payments = payments;
        this.accountDestination = accountDestination;
    }

    public long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getAccountDestination() {
        return accountDestination;
    }

}
