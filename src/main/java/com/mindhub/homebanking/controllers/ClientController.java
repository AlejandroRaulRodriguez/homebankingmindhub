package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.utils.Utils.getRandomNumber;


@RestController
@RequestMapping("/api")
public class ClientController {


    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientService.getClientsDto();
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientService.getClientDto(id);
    }

    @GetMapping("/clients/current")
    public ClientDTO getCurrentClient (Authentication authentication){
        Client currentClient = clientService.getClientCurrent(authentication);
        return new ClientDTO(currentClient);
    };

    @PostMapping("/clients")
    public ResponseEntity<Object> register(

            @RequestParam String name, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Fill in all the fields.", HttpStatus.FORBIDDEN);
        }

        if (clientService.getClientByEmail(email) !=  null) {
            return new ResponseEntity<>("Email in use.", HttpStatus.FORBIDDEN);
        }

        if(!email.contains("@")){
            return new ResponseEntity<>("Email invalid.", HttpStatus.FORBIDDEN);
        }

        Client newClient = new Client(name, lastName, email, passwordEncoder.encode(password));
        clientService.saveClient(newClient);

        Account newAccount = new Account("VIN-" + getRandomNumber(10000000,99999999), LocalDateTime.now(),0,newClient,false, AccountType.CURRENT);
        accountService.saveAccount(newAccount);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
