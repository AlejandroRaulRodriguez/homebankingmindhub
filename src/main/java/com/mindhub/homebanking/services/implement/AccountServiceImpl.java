package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<AccountDTO> getAccountsDto(){
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccounts() {
        return new ArrayList<>(accountRepository.findAll());
    }

    @Override
    public AccountDTO getAccountDto (long id){
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    @Override
    public void saveAccount(Account account){accountRepository.save(account);
    }

    @Override
    public Account getAccount (String number){return accountRepository.findByNumber(number);
    }

    @Override
    public Account getAccount (long id) {return accountRepository.findById(id).orElse(null);}
}
