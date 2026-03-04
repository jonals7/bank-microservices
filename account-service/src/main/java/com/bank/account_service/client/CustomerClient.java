package com.bank.account_service.client;

import com.bank.account_service.dto.CustomerResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomerClient {

    private final WebClient webClient;

    public CustomerClient(@Value("${customers.service.url}") String customerServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    public Mono<CustomerResponseDto> findCustomerById(Long customerId) {
        log.debug("Calling customer-service for customerId: {}", customerId);
        return webClient.get()
                .uri("/api/v1/customers/{id}", customerId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new RuntimeException("Customer not found with id: " + customerId)))
                .bodyToMono(CustomerResponseDto.class);
    }
}