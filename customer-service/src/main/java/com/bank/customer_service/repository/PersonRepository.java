package com.bank.customer_service.repository;

import com.bank.customer_service.model.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {

    Mono<Person> findByIdentification(String identification);

    Mono<Boolean> existsByIdentification(String identification);
}