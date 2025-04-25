package person.data.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import person.data.dto.PersonDTO;
import person.data.entity.Person;
import person.data.service.PersonService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody @Valid PersonDTO person) {
        Person created = personService.save(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<Person> updatePerson(@RequestBody @Valid PersonDTO person) {
        Person updated = personService.update(person);
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

        if (isValid) {
            return ResponseEntity.ok(true);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }
}
