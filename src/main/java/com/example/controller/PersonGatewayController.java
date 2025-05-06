package com.example.controller;

import com.example.client.PersonClient;
import com.example.dto.Person;
import com.example.dto.PersonDTO;
import com.example.exception.PersonClientException;
import com.example.mapper.PersonMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonGatewayController {

    private final PersonClient personClient;

    @PostMapping
    public ResponseEntity<PersonDTO> create(@RequestBody @Valid Person person) {
        ResponseEntity<PersonDTO> personResponseEntity = personClient.createPerson(person);

        if(!personResponseEntity.getStatusCode().is2xxSuccessful()){
            throw new PersonClientException("Не удалось создать Person с именем: " + person.getFullName());
        }

        return personResponseEntity;
    }

    @PutMapping
    public ResponseEntity<PersonDTO> update(@RequestBody @Valid Person person) {
        ResponseEntity<PersonDTO> personDTOResponseEntity = personClient.updatePerson(person);

        if(!personDTOResponseEntity.getStatusCode().is2xxSuccessful()){
            throw new PersonClientException("Не удалось обновить Person с именем: " + person.getFullName());
        }

        return personDTOResponseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getById(@PathVariable Integer id) {
        ResponseEntity<PersonDTO> personDTOResponseEntity = personClient.getPersonById(id);

        if(!personDTOResponseEntity.getStatusCode().is2xxSuccessful()){
            throw new PersonClientException("Не удалось получить Person по id = " + id);
        }

        return personDTOResponseEntity;
    }

    @GetMapping
    public ResponseEntity<Page<PersonDTO>> getAll(
            @RequestParam(required = false) String region,
            Pageable pageable
    ) {
        ResponseEntity<Page<PersonDTO>> personDTOPageResponseEntity = personClient.getAllPersons(region, pageable);

        if(!personDTOPageResponseEntity.getStatusCode().is2xxSuccessful()){
            throw new PersonClientException("Не удалось получить лист Persons по id");
        }

        return personDTOPageResponseEntity;
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestParam String name, @RequestParam String passport) {
        ResponseEntity<Boolean> isVerified = personClient.verifyPerson(name, passport);

        if(!isVerified.getStatusCode().is2xxSuccessful()){
            throw new PersonClientException("Не удалось аутентифицировать Person с паспортом = " + passport);
        }

        return isVerified;
    }

    @GetMapping("/passport/{passport}")
    public ResponseEntity<Map<String, String>> getByPassport(@PathVariable String passport) {
        ResponseEntity<Map<String, String>> resultMap = personClient.getPersonByPassport(passport);

        if(!resultMap.getStatusCode().is2xxSuccessful()){
            throw new PersonClientException("Не удалось получить информацию о Person с паспортом = " + passport);
        }

        return resultMap;
    }
}

