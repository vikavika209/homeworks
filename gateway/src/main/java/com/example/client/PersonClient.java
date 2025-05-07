package com.example.client;

import com.example.dto.Person;
import com.example.dto.PersonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "person-client", url = "${person.service.url}")
public interface PersonClient {

    @GetMapping("/person/passport/{passport}")
    ResponseEntity<Map<String, String>> getPersonByPassport(@PathVariable String passport);

    @PostMapping("/person")
    ResponseEntity<PersonDTO> createPerson(@RequestBody Person person);

    @PutMapping("/person")
    ResponseEntity<PersonDTO> updatePerson(@RequestBody Person person);

    @GetMapping("/person/{id}")
    ResponseEntity<PersonDTO> getPersonById(@PathVariable Integer id);

    @GetMapping("/person")
    ResponseEntity<Page<PersonDTO>> getAllPersons(
            @RequestParam(required = false) String region,
            Pageable pageable
    );

    @GetMapping("/person/verify")
   ResponseEntity<Boolean> verifyPerson(
            @RequestParam String name,
            @RequestParam String passport);
}

