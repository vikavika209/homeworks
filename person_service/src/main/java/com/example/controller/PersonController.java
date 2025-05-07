package com.example.controller;

import com.example.dto.PersonDTO;
import com.example.entity.Person;
import com.example.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@RequestBody @Valid Person person) {
        PersonDTO created = personService.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<PersonDTO> updatePerson(@RequestBody @Valid Person person) {
        PersonDTO updated = personService.update(person);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Integer id) {
        return ResponseEntity.ok(personService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PersonDTO>> getAllPersons(
            @RequestParam(required = false) String region,
            Pageable pageable
    ) {

        Page<PersonDTO> page = personService.findAllByRegion(region, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyPerson(
            @RequestParam String name,
            @RequestParam String passport) {

        boolean isValid = personService.verifyIdentity(name, passport);

        return ResponseEntity.status(HttpStatus.OK).body(isValid);

    }

    @GetMapping("/passport/{passport}")
    public ResponseEntity<Map<String, String>> getPersonByPassport(@PathVariable String passport) {
        Map<String, String> personDTO = personService.getByPassport(passport);
        return ResponseEntity.ok(personDTO);
    }
}
