package com.bank.customer_service;

import com.bank.customer_service.dto.CustomerRequestDto;
import com.bank.customer_service.dto.CustomerResponseDto;
import com.bank.customer_service.exception.CustomerNotFoundException;
import com.bank.customer_service.exception.DuplicateIdentificationException;
import com.bank.customer_service.model.Customer;
import com.bank.customer_service.model.Person;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.repository.PersonRepository;
import com.bank.customer_service.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequestDto requestDto;
    private Person person;
    private Customer customer;

    @BeforeEach
    void setUp() {
        requestDto = new CustomerRequestDto(
                "Jose Lema", "Male", 30,
                "1234567890", "Otavalo sn y principal",
                "098254785", "1234", true
        );

        person = new Person(1L, "Jose Lema", "Male", 30,
                "1234567890", "Otavalo sn y principal", "098254785");

        customer = new Customer(1L, "1234", true);
    }

    @Test
    @DisplayName("Should create customer successfully")
    void createCustomer_success() {
        when(personRepository.existsByIdentification(anyString())).thenReturn(Mono.just(false));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.just(person));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.create(requestDto))
                .expectNextMatches(response ->
                        response.getName().equals("Jose Lema") &&
                        response.getStatus().equals(true)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw DuplicateIdentificationException when identification already exists")
    void createCustomer_duplicateIdentification() {
        when(personRepository.existsByIdentification(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(customerService.create(requestDto))
                .expectError(DuplicateIdentificationException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
    void findById_notFound() {
        when(personRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(customerService.findById(99L))
                .expectError(CustomerNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should return customer when found by id")
    void findById_success() {
        when(personRepository.findById(1L)).thenReturn(Mono.just(person));
        when(customerRepository.findById(1L)).thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.findById(1L))
                .expectNextMatches(response -> response.getId().equals(1L))
                .verifyComplete();
    }
}