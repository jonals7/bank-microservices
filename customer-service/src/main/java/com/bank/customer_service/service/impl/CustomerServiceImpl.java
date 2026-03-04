package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.CustomerRequestDto;
import com.bank.customer_service.dto.CustomerResponseDto;
import com.bank.customer_service.exception.CustomerNotFoundException;
import com.bank.customer_service.exception.DuplicateIdentificationException;
import com.bank.customer_service.model.Customer;
import com.bank.customer_service.model.Person;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.repository.PersonRepository;
import com.bank.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final PersonRepository personRepository;
    private final CustomerRepository customerRepository;
    private final DatabaseClient databaseClient; 

    @Override
    public Flux<CustomerResponseDto> findAll() {
        log.debug("Fetching all customers");
        return personRepository.findAll()
                .flatMap(person -> customerRepository.findById(person.getId())
                        .map(customer -> toResponseDto(person, customer)));
    }

    @Override
    public Mono<CustomerResponseDto> findById(Long id) {
        log.debug("Fetching customer with id: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(person -> customerRepository.findById(person.getId())
                        .map(customer -> toResponseDto(person, customer)));
    }

    @Override
    @Transactional
    public Mono<CustomerResponseDto> create(CustomerRequestDto request) {
        log.debug("Creating customer with identification: {}", request.getIdentification());
        return personRepository.existsByIdentification(request.getIdentification())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateIdentificationException(request.getIdentification()));
                    }
                    Person person = toPersonEntity(request);
                    return personRepository.save(person)
                            .flatMap(savedPerson ->
                                databaseClient.sql("INSERT INTO customer (id, password, status) VALUES (:id, :password, :status)")
                                        .bind("id", savedPerson.getId())
                                        .bind("password", request.getPassword())
                                        .bind("status", request.getStatus())
                                        .fetch()
                                        .rowsUpdated()
                                        .map(rows -> {
                                            Customer customer = new Customer(savedPerson.getId(), request.getPassword(), request.getStatus());
                                            return toResponseDto(savedPerson, customer);
                                        })
                            );
                });
    }

    @Override
    @Transactional
    public Mono<CustomerResponseDto> update(Long id, CustomerRequestDto request) {
        log.debug("Updating customer with id: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(person -> {
                    updatePersonFields(person, request);
                    return personRepository.save(person);
                })
                .flatMap(savedPerson -> customerRepository.findById(id)
                        .flatMap(customer -> {
                            customer.setPassword(request.getPassword());
                            customer.setStatus(request.getStatus());
                            return customerRepository.save(customer);
                        })
                        .flatMap(savedCustomer -> personRepository.findById(id)
                                .map(person -> toResponseDto(person, savedCustomer))));
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        log.debug("Deleting customer with id: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(person -> customerRepository.deleteById(id)
                        .then(personRepository.deleteById(id)));
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Person toPersonEntity(CustomerRequestDto dto) {
        Person person = new Person();
        person.setName(dto.getName());
        person.setGender(dto.getGender());
        person.setAge(dto.getAge());
        person.setIdentification(dto.getIdentification());
        person.setAddress(dto.getAddress());
        person.setPhone(dto.getPhone());
        return person;
    }

    private void updatePersonFields(Person person, CustomerRequestDto dto) {
        person.setName(dto.getName());
        person.setGender(dto.getGender());
        person.setAge(dto.getAge());
        person.setIdentification(dto.getIdentification());
        person.setAddress(dto.getAddress());
        person.setPhone(dto.getPhone());
    }

    private CustomerResponseDto toResponseDto(Person person, Customer customer) {
        return new CustomerResponseDto(
                person.getId(),
                person.getName(),
                person.getGender(),
                person.getAge(),
                person.getIdentification(),
                person.getAddress(),
                person.getPhone(),
                customer.getStatus()
        );
    }
}