package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;

import java.util.Set;


public interface CardService {

    void saveCard (Card card);

    Card findById (long id);

    Card getCard (String cardNumber);


}
