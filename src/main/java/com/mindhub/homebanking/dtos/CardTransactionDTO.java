package com.mindhub.homebanking.dtos;

public class CardTransactionDTO {

    private String cardNum;
    private int cardCvv;
    private double amountCardTransaction;
    private String descriptionCardTransaction;

    CardTransactionDTO (){};

    public CardTransactionDTO(String cardNum, int cardCvv, double amountCardTransaction, String descriptionCardTransaction) {
        this.cardNum = cardNum;
        this.cardCvv = cardCvv;
        this.amountCardTransaction = amountCardTransaction;
        this.descriptionCardTransaction = descriptionCardTransaction;
    }

    public String getCardNum() {return cardNum;}

    public int getCardCvv() {return cardCvv;}

    public double getAmountCardTransaction() {return amountCardTransaction;}

    public String getDescriptionCardTransaction() {return descriptionCardTransaction;
    }
}
