package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;


@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {

    Card findById (long id);

    Card findByNumber (String cardNumber);

}
