package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;

import java.util.List;

public interface AccountService {

    List<AccountDTO> getAccountsDto();

    List<Account> getAccounts();

    AccountDTO getAccountDto (long id);

    void saveAccount (Account account);

    Account getAccount (String number);

    Account getAccount (long id);
}
