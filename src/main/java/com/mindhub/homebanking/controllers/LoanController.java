package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.LoanService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ClientLoanRepository clientLoanRepository;


    @GetMapping("/loans")
    public List<LoanDTO> getLoans (){
        return loanService.getLoansDTO();
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> newLoan (@RequestBody LoanApplicationDTO loanApplicationDTO,
                                           Authentication authentication){

        long id = loanApplicationDTO.getId();

        Double amount = loanApplicationDTO.getAmount();

        Integer payments = loanApplicationDTO.getPayments();

        String numberAccountDestination = loanApplicationDTO.getAccountDestination();

        Client currentClient = clientService.getClientByEmail(authentication.getName());

        Loan loanRequested = loanService.getLoan(id);

        double amountToPay = ((loanRequested.getInterestRate() * amount)/100) + amount;

        Account account = accountService.getAccount(numberAccountDestination);

        Set<Loan> loansCurrentClient = currentClient.getLoans();

        loansCurrentClient = loansCurrentClient.stream().filter(loan -> loan.getName().equals(loanRequested.getName())).collect(Collectors.toSet());

        Set<Account> accountsCurrentClient = currentClient.getAccounts();

        List<Integer> paymentsList = loanRequested.getPayments();

        List<Integer> paymentSelected = paymentsList.stream().filter(paymentslist -> paymentslist.equals(payments)).collect(Collectors.toList());


        if (amount.isNaN() || amount.isInfinite() || amount <= 0 || payments <= 0){
            return new ResponseEntity<>("Invalid amount.", HttpStatus.FORBIDDEN);
        }

        if (!loanService.getLoans().contains(loanRequested)){
            return new ResponseEntity<>("That type of loan does not exist.", HttpStatus.FORBIDDEN);
        }

        if (amount > loanRequested.getMaxAmount()){
            return new ResponseEntity<>("The requested amount exceeds the maximum of this type of loan.", HttpStatus.FORBIDDEN);
        }

        if(paymentSelected.size() == 0){
            return new ResponseEntity<>("The number of selected payments is not available for this type of loan.", HttpStatus.FORBIDDEN);
        }

        if (loansCurrentClient.size() >= 1){
            return new ResponseEntity<>("You cannot have more than one loan of the same type.", HttpStatus.FORBIDDEN);
        }

        if (account == null){
            return new ResponseEntity<>("Destination account does not exist.", HttpStatus.FORBIDDEN);
        }

        if (!accountsCurrentClient.contains(account)){
            return new ResponseEntity<>("The destination account does not belong to you.", HttpStatus.FORBIDDEN);
        }

        ClientLoan newClientLoan = new ClientLoan(amountToPay,payments,currentClient,loanRequested);
        clientLoanRepository.save(newClientLoan);

        Transaction newTransaction = new Transaction(TransactionType.CREDIT,amount,loanRequested.getName() + " APPROVED LOAN", LocalDateTime.now(),account,account.getBalance() + amount,false, LocalDate.now());
        transactionService.saveTransaction(newTransaction);

        account.setBalance(account.getBalance()+amount);
        return new ResponseEntity<>("Successfully requested loan.", HttpStatus.CREATED);
    }

    @PostMapping("/loans/createLoan")
    public ResponseEntity<Object> createLoan (
            @RequestParam String name, @RequestParam Integer maxAmount,
            @RequestParam double interestRate,@RequestParam List<Integer> payments) {

        if (name.isEmpty() || maxAmount < 1 || interestRate < 1 || payments.isEmpty()) {
            return new ResponseEntity<>("Incorrect data.",HttpStatus.FORBIDDEN);
        }

        Loan newLoan = new Loan(name,maxAmount,interestRate,payments);
        loanService.saveNewLoan(newLoan);

        return new ResponseEntity<>("Loan created successfully.", HttpStatus.CREATED);
    }
}
