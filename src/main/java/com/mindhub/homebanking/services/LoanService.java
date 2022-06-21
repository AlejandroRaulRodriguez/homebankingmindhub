package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    List<LoanDTO> getLoansDTO();

    Loan getLoan (long id);

    List<Loan> getLoans ();

    void saveNewLoan(Loan loan);
}
