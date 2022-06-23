package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
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
import java.util.stream.Collectors;
import static com.mindhub.homebanking.utils.Utils.getRandomNumber;


@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private CardService cardService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> newCard (
            @RequestParam CardType type, @RequestParam CardColor color, Authentication authentication) {
        Client clientCurrent = clientService.getClientByEmail(authentication.getName());

        Set<Card> cards = clientCurrent.getCards().stream().filter(card -> !card.isDisable()).collect(Collectors.toSet());

        Set<Card> cardsType = cards.stream().filter(card -> card.getType().equals(type)).collect(Collectors.toSet());

        Set<Card> cardsColor = cardsType.stream().filter(card -> card.getColor().equals(color)).collect(Collectors.toSet());

        if (cardsType.size() >= 3) {
            return new ResponseEntity<>("You cannot have more than 3 cards of the same type.", HttpStatus.FORBIDDEN);
        }

        if (cardsColor.size() >= 1) {
            return new ResponseEntity<>("You cannot have more than 1 card of the same color and type.", HttpStatus.FORBIDDEN);
        }

        Card newCard = new Card(clientCurrent.getName() + " " + clientCurrent.getLastName(), type, color,getRandomNumber(1000,9999)+"-"+getRandomNumber(1000,9999)+"-"+getRandomNumber(1000,9999)+"-"+getRandomNumber(1000,9999),getRandomNumber(100,999), LocalDate.now(),LocalDate.now().plusYears(5),clientCurrent,false,false);
        cardService.saveCard(newCard);

        return new ResponseEntity<>("Card created successfully.", HttpStatus.CREATED);
    }

    @PatchMapping("/clients/current/cards/{id}")
    public ResponseEntity<Object> deleteCard(@PathVariable long id, Authentication authentication) {

        Client clientCurrent = clientService.getClientByEmail(authentication.getName());

        Set<Card> cardsCurrentClient = clientCurrent.getCards().stream().filter(card -> !card.isDisable()).collect(Collectors.toSet());

        Card cardToDelete = cardService.findById(id);

        if (!cardsCurrentClient.contains(cardToDelete)) {
            return new ResponseEntity<>("This card does not exist", HttpStatus.FORBIDDEN);
        }

        if (cardsCurrentClient.size() == 1) {
            return new ResponseEntity<>("You can't delete your only card", HttpStatus.FORBIDDEN);
        }

        cardToDelete.setDisable(true);
        cardService.saveCard(cardToDelete);
        return new ResponseEntity<>("Card deleted successfully", HttpStatus.ACCEPTED);
    }

    @PatchMapping("/clients/current/cards")
    public void expiredCheck (Authentication authentication) {
        Client currentClient = clientService.getClientByEmail(authentication.getName());
        Set<Card> cardsCurrentClient = currentClient.getCards();
        Set<Card> expiredCards = cardsCurrentClient.stream().filter(card -> card.getThruDate().isBefore(LocalDate.now()) || card.getThruDate().equals(LocalDate.now())).collect(Collectors.toSet());

        if (expiredCards.size() > 0){
            expiredCards.forEach(card -> card.setExpired(true));
            expiredCards.forEach(card -> cardService.saveCard(card));
        }
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/clients/current/cards/cardsTransactions")
    public ResponseEntity<Object> cardTransaction (@RequestBody CardTransactionDTO cardTransactionDTO, Authentication authentication){

        String cardNum = cardTransactionDTO.getCardNum();
        int cardCvv = cardTransactionDTO.getCardCvv();
        double cardTransactionAmount = cardTransactionDTO.getAmountCardTransaction();
        String description = cardTransactionDTO.getDescriptionCardTransaction();

        Client currentClient = clientService.getClientByEmail(authentication.getName());
        Card cardUsed = cardService.getCard(cardNum);
        Account accountToPay = currentClient.getAccounts().stream().filter(account -> account.getBalance() > cardTransactionAmount).findFirst().orElse(null);


        if (cardUsed == null || cardNum.isEmpty()) {
            return new ResponseEntity<>("Card number is incorrect.", HttpStatus.FORBIDDEN);
        }

        if (cardUsed.isDisable()){
            return new ResponseEntity<>("This card is disable.", HttpStatus.FORBIDDEN);
        }

        if (cardUsed.isExpired()) {
            return new ResponseEntity<>("This card is expired.", HttpStatus.FORBIDDEN);
        }

        if (cardUsed.getCvv() != cardCvv) {
            return new ResponseEntity<>("Security code is incorrect.", HttpStatus.FORBIDDEN);
        }

        if (description.isEmpty()) {
            return new ResponseEntity<>("Write a description.", HttpStatus.FORBIDDEN);
        }

        if (accountToPay == null){
            return new ResponseEntity<>("Insufficient funds.", HttpStatus.FORBIDDEN);
        }

        Transaction newTransaction = new Transaction(TransactionType.DEBIT, cardTransactionAmount, description, LocalDateTime.now(),accountToPay,accountToPay.getBalance() - cardTransactionAmount,false,LocalDate.now());
        transactionService.saveTransaction(newTransaction);
        accountToPay.setBalance(accountToPay.getBalance() - cardTransactionAmount);
        return new ResponseEntity<>("Successful payment.", HttpStatus.ACCEPTED);
    }
}