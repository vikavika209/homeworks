package person.data.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import person.data.dto.PersonDTO;
import person.data.entity.Address;
import person.data.entity.Contact;
import person.data.entity.Person;
import person.data.exeption.PassportAlreadyExistException;
import person.data.service.IdentityDocumentService;
import person.data.service.PersonService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private PersonService personService;
    @SuppressWarnings("removal")
    @MockBean
    private IdentityDocumentService identityDocumentService;

    private Person testPerson;
    private PersonDTO personDTO;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setFullName("Иванов Иван");
        testPerson.setPassportData("123456");
        testPerson.setAddresses(Collections.singletonList(new Address()));
        testPerson.setContacts(Collections.singletonList(new Contact()));

        personDTO = new PersonDTO();
        personDTO.setId(1);
        personDTO.setFullName("Иванов Иван");
        testPerson.setAddresses(Collections.singletonList(new Address()));
    }

    @Test
    void createPerson_shouldReturnCreatedPerson() throws Exception {
        when(personService.save(any(PersonDTO.class))).thenReturn(testPerson);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""

                                {
                               "id": 1,
                               "fullName": "Иванов Иван",
                               "passportData": "123456",
                               "documents": [
                                 {
                                   "type": "Паспорт",
                                   "number": "1234567890"
                                 }
                               ],
                               "contacts": [
                                 {
                                   "type": "PHONE",
                                   "value": "+79001234567"
                                 }
                               ],
                               "addresses": [
                                 {
                                   "city": "Москва",
                                   "street": "Тверская",
                                   "house": "1"
                                 }
                               ]
                             }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Иванов Иван"));
    }

    @Test
    void updatePerson_shouldReturnUpdatedPerson() throws Exception {
        when(personService.update(any(PersonDTO.class))).thenReturn(testPerson);

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""

                                {
                               "id": 1,
                               "fullName": "Иванов Иван",
                               "passportData": "123456",
                               "documents": [
                                 {
                                   "type": "Паспорт",
                                   "number": "1234567890"
                                 }
                               ],
                               "contacts": [
                                 {
                                   "type": "PHONE",
                                   "value": "+79001234567"
                                 }
                               ],
                               "addresses": [
                                 {
                                   "city": "Москва",
                                   "street": "Тверская",
                                   "house": "1"
                                 }
                               ]
                             }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Иванов Иван"));
    }

    @Test
    void getPersonById_shouldReturnDTO() throws Exception {
        when(personService.findById(1)).thenReturn(personDTO);

        mockMvc.perform(get("/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Иванов Иван"));
    }

    @Test
    void getAllPersons_shouldReturnPageOfDTOs() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonDTO> page = new PageImpl<>(List.of(personDTO));
        when(personService.findAllByRegion(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/person?region=Москва"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Иванов Иван"));
    }

    @Test
    void verifyPerson_shouldReturnTrue() throws Exception {
        when(personService.verifyIdentity("Иван", "AB123456")).thenReturn(true);

        mockMvc.perform(get("/person/verify")
                        .param("name", "Иван")
                        .param("passport", "AB123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void createPersonWithExistingPassport() throws Exception {
        when(personService.save(any())).thenThrow(new PassportAlreadyExistException("123456"));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""

                        {
                               "id": 1,
                               "fullName": "Иванов Иван",
                               "passportData": "123456",
                               "documents": [
                                 {
                                   "type": "Паспорт",
                                   "number": "1234567890"
                                 }
                               ],
                               "contacts": [
                                 {
                                   "type": "PHONE",
                                   "value": "+79001234567"
                                 }
                               ],
                               "addresses": [
                                 {
                                   "city": "Москва",
                                   "street": "Тверская",
                                   "house": "1"
                                 }
                               ]
                             }
                            """))
                .andExpect(status().isConflict());
    }
}
