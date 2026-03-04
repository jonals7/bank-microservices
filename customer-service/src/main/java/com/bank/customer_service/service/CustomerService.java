package com.bank.customer_service.service;

import com.bank.customer_service.dto.CustomerRequestDto;
import com.bank.customer_service.dto.CustomerResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<CustomerResponseDto> findAll();

    Mono<CustomerResponseDto> findById(Long id);

    Mono<CustomerResponseDto> create(CustomerRequestDto request);

    Mono<CustomerResponseDto> update(Long id, CustomerRequestDto request);

    Mono<Void> delete(Long id);
}