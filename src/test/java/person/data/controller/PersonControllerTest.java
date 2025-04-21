package person.data.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import person.data.dto.PersonDTO;
import person.data.entity.Person;
import person.data.service.PersonService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private PersonService personService;

    private Person testPerson;
    private PersonDTO personDTO;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setFullName("Иванов Иван");
        testPerson.setPassportData("AB123456");

        personDTO = new PersonDTO();
        personDTO.setId(1);
        personDTO.setFullName("Иванов Иван");
    }

    @Test
    void createPerson_shouldReturnCreatedPerson() throws Exception {
        when(personService.save(any(Person.class))).thenReturn(testPerson);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "fullName": "Иванов Иван",
                              "passportData": "AB123456"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Иванов Иван"));
    }

    @Test
    void updatePerson_shouldReturnUpdatedPerson() throws Exception {
        when(personService.update(any(Person.class))).thenReturn(testPerson);

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "id": 1,
                              "fullName": "Иванов Иван",
                              "passportData": "AB123456"
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
        Page<PersonDTO> page = new PageImpl<>(List.of(personDTO));
        when(personService.findAllByRegion(any())).thenReturn(page);

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
}
