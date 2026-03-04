package com.bank.account_service.repository;

import com.bank.account_service.model.Movement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface MovementRepository extends ReactiveCrudRepository<Movement, Long> {

    Flux<Movement> findByAccountId(Long accountId);

    Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDateTime start, LocalDateTime end);
}
