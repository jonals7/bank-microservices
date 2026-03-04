package com.bank.account_service.repository;

import com.bank.account_service.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {

    Flux<Account> findByCustomerId(Long customerId);

    Mono<Boolean> existsByAccountNumber(String accountNumber);

    Mono<Account> findByAccountNumber(String accountNumber);
}
