package person.data.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import person.data.entity.IdentityDocument;
import person.data.entity.Person;
import person.data.mapper.PersonMapper;
import person.data.repository.PersonRepository;
import person.data.service.IdentityDocumentService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private IdentityDocumentService identityDocumentService;

    @Autowired
    PersonMapper personMapper;

    @BeforeEach
    void setup() {
        personRepository.deleteAll();
    }

    @Test
    void createPersonWithExistingPassport_ShouldReturnConflict() throws Exception {
        IdentityDocument doc = new IdentityDocument();
        doc.setName("Паспорт");
        doc.setNumber("123456");
        doc.setPerson(new Person());

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "id": 1,
                          "fullName": "Иванов Иван",
                          "passportData": "123456"
                        }
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void createPersonWithCorrectData() throws Exception {

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "fullName": "Иванов Иван",
                          "passportData": "123456"
                        }
                        """))
                .andExpect(status().isCreated());
    }

}
