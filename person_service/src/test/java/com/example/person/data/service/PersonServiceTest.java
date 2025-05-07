package com.example.person.data.service;

import com.example.dto.PersonDTO;
import com.example.mapper.PersonMapper;
import com.example.entity.Address;
import com.example.entity.Contact;
import com.example.entity.IdentityDocument;
import com.example.entity.Person;
import com.example.exeption.PersonNotFoundException;
import com.example.repository.PersonRepository;
import com.example.service.AddressService;
import com.example.service.ContactService;
import com.example.service.IdentityDocumentService;
import com.example.service.PersonService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    @Mock private IdentityDocumentService identityDocumentService;
    @Mock private ContactService contactService;
    @Mock private AddressService addressService;
    @Mock private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    private Person testPerson;
    private PersonDTO testPersonDTO;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setFullName("Иванов Иван Иванович");
        testPerson.setPassportData("123456");

        testPersonDTO = new PersonDTO();
        testPersonDTO.setId(1);
        testPersonDTO.setFullName("Иванов Иван Иванович");
        testPersonDTO.setPassportData("123456");

        IdentityDocument doc = new IdentityDocument();
        doc.setId(1);
        doc.setName("Паспорт");
        doc.setNumber("123456");
        doc.setPerson(testPerson);

        Contact contact = new Contact();
        contact.setId(1);
        contact.setType("Мобильный");
        contact.setNumber("89001234567");
        contact.setPerson(testPerson);

        Address address = new Address();
        address.setId(1);
        address.setFullAddress("г. Москва, ул. Пушкина, д.1");
        address.setRegion("Москва");
        address.setPersons(new ArrayList<>(List.of(testPerson)));

        testPerson.setDocuments(new ArrayList<>(List.of(doc)));
        testPerson.setContacts(new ArrayList<>(List.of(contact)));
        testPerson.setAddresses(new ArrayList<>(List.of(address)));

        when(personMapper.toDTO(testPerson)).thenReturn(testPersonDTO);

        testPersonDTO = personMapper.toDTO(testPerson);
    }

    @Test
    void save_shouldSavePersonAndCallDependencies() {
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);
        when(personMapper.toDTO(testPerson)).thenReturn(testPersonDTO);

        PersonDTO saved = personService.save(testPerson);

        assertNotNull(saved);
        verify(personRepository).save(testPerson);
    }

    @Test
    void update_shouldUpdate() {
        when(personRepository.findByIdForUpdate(testPerson.getId())).thenReturn(Optional.of(testPerson));
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);

        PersonDTO saved = personService.update(testPerson);

        assertNotNull(saved);
        verify(personRepository).save(testPerson);
        assertEquals(saved.getId(), testPerson.getId());
        assertEquals(saved.getFullName(), testPerson.getFullName());
        assertEquals(saved.getPassportData(), testPerson.getPassportData());
    }

    @Test
    void update_shouldSaveIfPersonNotFound() {
        when(personRepository.findByIdForUpdate(testPerson.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(PersonNotFoundException.class, () -> personService.update(testPerson));
    }

    @Test
    void findById_shouldReturnConvertedPerson() {
        when(personRepository.findById(1)).thenReturn(Optional.of(testPerson));

        PersonDTO result = personService.findById(1);

        assertEquals(testPerson.getFullName(), result.getFullName());
        verify(personRepository).findById(1);
    }

    @Test
    void delete_shouldDeletePersonIfExists() {
        when(personRepository.findById(1)).thenReturn(Optional.of(testPerson));

        personService.delete(1);

        verify(personRepository).deleteById(1);
    }

    @Test
    void verifyIdentity_shouldReturnTrueIfMatch() {
        IdentityDocument doc = new IdentityDocument();
        doc.setName("Паспорт РФ");
        doc.setNumber("ABC123");
        doc.setPerson(testPerson);
        testPerson.setDocuments(List.of(doc));
        when(personRepository.findByFullName("Иван")).thenReturn(List.of(testPerson));

        boolean result = personService.verifyIdentity("Иван", "ABC123");

        assertTrue(result);
    }

    @Test
    void verifyIdentity_shouldReturnFalseIfNoMatch() {
        when(personRepository.findByFullName("Иван")).thenReturn(Collections.emptyList());

        boolean result = personService.verifyIdentity("Иван", "123");

        assertFalse(result);
    }

    @Test
    void getByPassport_whenPersonExists_returnsMap() {

        when(personRepository.findByPassportData("123456"))
                .thenReturn(Optional.of(testPerson));

        Map<String, String> result = personService.getByPassport("123456");

        assertNotNull(result);
        assertEquals("Иванов Иван Иванович", result.get("Имя"));
        assertEquals("123456", result.get("Паспорт"));
    }

    @Test
    void getByPassport_whenPersonNotExists_trowPersonNotFoundException() {

        when(personRepository.findByPassportData("123456"))
                .thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(
                PersonNotFoundException.class,
                () -> personService.getByPassport("123456")
        );
    }
}
