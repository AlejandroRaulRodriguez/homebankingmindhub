package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder(){return PasswordEncoderFactories.createDelegatingPasswordEncoder();};

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository,
									  LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {
			Client client1 = new Client("Melba","Morel","melba@mindhub.com",passwordEncoder().encode("1234"));
			Client client2 = new Client("Juan","Perez","JuanP@gmail.com",passwordEncoder().encode("dsad12312223"));
			Client admin = new Client("admin","admin","admin@admin.com",passwordEncoder().encode("1234"));
			clientRepository.save(client1);
			clientRepository.save(client2);
			clientRepository.save(admin);

			Account account1 = new Account("VIN001", LocalDateTime.now(),5000,client1,false,AccountType.CURRENT);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1),7500,client1,false,AccountType.SALARY);
			Account account3 = new Account("VIN003", LocalDateTime.now(), 10000,client2,false,AccountType.SAVINGS);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);

			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 4000, "Bonus", LocalDateTime.now(), account1,account1.getBalance(),false,LocalDate.now());
			Transaction transaction2 = new Transaction(TransactionType.DEBIT, 3000, "Public taxes", LocalDateTime.now(),account1,account1.getBalance(),false,LocalDate.now());
			Transaction transaction3 = new Transaction(TransactionType.DEBIT, 1500, "Riot Games payment", LocalDateTime.now(),account2,account2.getBalance(),false,LocalDate.now());
			Transaction transaction4 = new Transaction(TransactionType.CREDIT, 5000, "Salary", LocalDateTime.now(), account2,account2.getBalance(),false,LocalDate.now());
			Transaction transaction5 = new Transaction(TransactionType.DEBIT,2000,"Internet payment",LocalDateTime.now(),account3,account3.getBalance(),false,LocalDate.now());
			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);

			Loan loan1 = new Loan("Mortgage",500000,25,List.of(12,24,36,48,60));
			Loan loan2 = new Loan("Personal",100000,15,List.of(6,12,24));
			Loan loan3 = new Loan("Automotive",300000,20,List.of(6,12,24,36));
			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			ClientLoan clientLoan1 = new ClientLoan(400000d,60,client1,loan1);
			clientLoanRepository.save(clientLoan1);
			ClientLoan clientLoan2 = new ClientLoan(50000d,12,client1,loan2);
			clientLoanRepository.save(clientLoan2);
			ClientLoan clientLoan3 = new ClientLoan(100000d,24,client2,loan2);
			clientLoanRepository.save(clientLoan3);
			ClientLoan clientLoan4 = new ClientLoan(200000d,36,client2,loan3);
			clientLoanRepository.save(clientLoan4);

			Card card1 = new Card(client1.getName() + " " + client1.getLastName(), CardType.DEBIT, CardColor.GOLD, "2345-2235-2221-3344", 654, LocalDate.now(),LocalDate.now().plusYears( 5),client1,false,false);
			cardRepository.save(card1);
			Card card2 = new Card(client1.getName() + " " + client1.getLastName(), CardType.DEBIT, CardColor.TITANIUM, "2346-2235-2221-3377", 880, LocalDate.now(),LocalDate.now(),client1,false,false);
			cardRepository.save(card2);
			Card card4 = new Card(client1.getName() + " " + client1.getLastName(), CardType.CREDIT, CardColor.SILVER, "2346-2235-2221-3327", 820, LocalDate.now(),LocalDate.of(2021,8,15),client1,false,false);
			cardRepository.save(card4);
			Card card3 = new Card(client2.getName() + " " + client2.getLastName(), CardType.CREDIT, CardColor.SILVER, "2350-1135-2321-3245", 112, LocalDate.now(),LocalDate.now().plusYears( 5),client2,false,false);
			cardRepository.save(card3);
		};
	}
}
