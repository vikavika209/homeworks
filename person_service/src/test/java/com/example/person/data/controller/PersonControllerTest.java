package com.example.person.data.controller;

import com.example.controller.PersonController;
import com.example.dto.PersonDTO;
import com.example.entity.Address;
import com.example.entity.Contact;
import com.example.entity.IdentityDocument;
import com.example.entity.Person;
import com.example.exeption.PassportAlreadyExistException;
import com.example.exeption.PersonNotFoundException;
import com.example.service.IdentityDocumentService;
import com.example.service.PersonService;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Contact contact = new Contact();
        contact.setType("PHONE");
        contact.setNumber("+79001234567");

        Address address = new Address();
        address.setRegion("Москва");
        address.setFullAddress("Тверская, 1");

        IdentityDocument doc = new IdentityDocument();
        doc.setName("Паспорт");
        doc.setNumber("1234567890");

    }

    @Test
    void createPerson_shouldReturnCreatedPerson() throws Exception {
        when(personService.save(any(Person.class))).thenReturn(personDTO);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""

                                {
                               "id": 1,
                               "fullName": "Иванов Иван",
                               "passportData": "123456",
                               "documents": [
                                 {
                                   "name": "Паспорт",
                                   "number": "1234567890"
                                 }
                               ],
                               "contacts": [
                                 {
                                   "type": "PHONE",
                                   "number": "+79001234567"
                                 }
                               ],
                               "addresses": [
                                 {
                                   "region": "Москва",
                                   "fullAddress": "Тверская, 1"
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
        when(personService.update(any(Person.class))).thenReturn(personDTO);

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""

                                {
                               "id": 1,
                               "fullName": "Иванов Иван",
                               "passportData": "123456",
                               "documents": [
                                 {
                                   "name": "Паспорт",
                                   "number": "123456"
                                 }
                               ],
                               "contacts": [
                                 {
                                   "type": "PHONE",
                                   "number": "+79001234567"
                                 }
                               ],
                               "addresses": [
                                 {
                                   "region": "Москва",
                                   "fullAddress": "Тверская, 1"
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

    @Test
    void getPersonByPassport_returnsPersonDTO() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("Имя", "Иван Иванов");
        map.put("Паспорт", "1234567890");

        String passport = "1234567890";

        when(personService.getByPassport(passport)).thenReturn(map);

        mockMvc.perform(get("/person/passport/{passport}", passport))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Имя").value("Иван Иванов"))
                .andExpect(jsonPath("$.Паспорт").value("1234567890"));
    }

    @Test
    void getPersonByPassport_returnsNull() throws Exception {

        String passport = "1234567890";
        PersonDTO dto = new PersonDTO();
        dto.setId(1);
        dto.setFullName("Иван Иванов");
        dto.setPassportData(passport);

        when(personService.getByPassport(passport)).thenThrow(PersonNotFoundException.class);

        mockMvc.perform(get("/person/passport/{passport}", passport))
                .andExpect(status().isNotFound());
    }
}
