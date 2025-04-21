package person.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import person.data.convertor.PersonConvertor;
import person.data.dto.PersonDTO;
import person.data.entity.Address;
import person.data.entity.Contact;
import person.data.entity.IdentityDocument;
import person.data.entity.Person;
import person.data.repository.PersonRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    @Mock private IdentityDocumentService identityDocumentService;
    @Mock private ContactService contactService;
    @Mock private AddressService addressService;
    @Mock private PersonConvertor personConvertor;

    @InjectMocks
    private PersonService personService;

    private Person testPerson;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setFullName("Иванов Иван Иванович");
        testPerson.setPassportData("AB123456");

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
        address.setPersons(List.of(testPerson));

        testPerson.setDocuments(List.of(doc));
        testPerson.setContacts(List.of(contact));
        testPerson.setAddresses(List.of(address));
    }

    @Test
    void save_shouldSavePersonAndCallDependencies() {
        when(personRepository.save(testPerson)).thenReturn(testPerson);
        when(identityDocumentService.findAll()).thenReturn(Page.empty());
        when(contactService.findAll()).thenReturn(Page.empty());
        when(addressService.findAll()).thenReturn(Page.empty());

        Person saved = personService.save(testPerson);

        assertNotNull(saved);
        verify(personRepository).save(testPerson);
        verify(identityDocumentService).save(any());
        verify(contactService).save(any());
        verify(addressService).save(any());
    }

    @Test
    void update_shouldSaveIfPersonNotFound() {
        when(personRepository.findById(testPerson.getId())).thenReturn(Optional.empty());
        when(personRepository.save(testPerson)).thenReturn(testPerson);
        when(identityDocumentService.findAll()).thenReturn(Page.empty());
        when(contactService.findAll()).thenReturn(Page.empty());
        when(addressService.findAll()).thenReturn(Page.empty());

        Person result = personService.update(testPerson);

        assertEquals(testPerson, result);
        verify(personRepository).save(testPerson);
    }

    @Test
    void findById_shouldReturnConvertedPerson() {
        when(personRepository.findById(1)).thenReturn(Optional.of(testPerson));
        PersonDTO dto = new PersonDTO();
        when(personConvertor.personToDTO(testPerson)).thenReturn(dto);

        PersonDTO result = personService.findById(1);

        assertEquals(dto, result);
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
}
