package com.bank.account_service.service;

import com.bank.account_service.dto.MovementRequestDto;
import com.bank.account_service.dto.MovementResponseDto;
import com.bank.account_service.dto.StatementDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface MovementService {

    Flux<MovementResponseDto> findAll();

    Mono<MovementResponseDto> findById(Long id);

    Mono<MovementResponseDto> create(MovementRequestDto request);

    Mono<MovementResponseDto> update(Long id, MovementRequestDto request);

    Mono<Void> delete(Long id);

    Mono<StatementDto> getStatement(Long customerId, LocalDate startDate, LocalDate endDate);
}