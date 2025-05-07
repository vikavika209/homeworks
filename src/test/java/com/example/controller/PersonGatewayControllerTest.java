package com.example.controller;

import com.example.client.PersonClient;
import com.example.dto.Person;
import com.example.dto.PersonDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonGatewayController.class)
class PersonGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonClient personClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPerson_returnsOk() throws Exception {
        Person person = new Person();
        person.setFullName("Иван Иванов");
        person.setPassportData("1234567890");

        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName("Иван Иванов");
        personDTO.setPassportData("1234567890");

        when(personClient.createPerson(any(Person.class)))
                .thenReturn(ResponseEntity.ok(personDTO));

        mockMvc.perform(post("/api/v1/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Иван Иванов"));
    }

    @Test
    void getPersonById_returnsOk() throws Exception {
        Person person = new Person();
        person.setFullName("Тест");
        person.setPassportData("111222333");

        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName("Тестовый");
        personDTO.setPassportData("111222333");

        when(personClient.getPersonById(1))
                .thenReturn(ResponseEntity.ok(personDTO));

        mockMvc.perform(get("/api/v1/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passportData").value("111222333"));
    }

    @Test
    void verifyPerson_returnsTrue() throws Exception {
        when(personClient.verifyPerson("Иван", "1234567890"))
                .thenReturn(ResponseEntity.ok(true));

        mockMvc.perform(get("/api/v1/person/verify")
                        .param("name", "Иван")
                        .param("passport", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getByPassport_returnsMap() throws Exception {
        Map<String, String> data = Map.of(
                "Имя", "Иван Иванов",
                "Паспорт", "1234567890"
        );

        when(personClient.getPersonByPassport("1234567890"))
                .thenReturn(ResponseEntity.ok(data));

        mockMvc.perform(get("/api/v1/person/passport/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Имя").value("Иван Иванов"));
    }

    @Test
    void createPerson_returnsError_ifFeignFails() throws Exception {
        Person person = new Person();
        person.setFullName("Ошибка");
        person.setPassportData("0000000000");

        when(personClient.createPerson(any(Person.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        mockMvc.perform(post("/api/v1/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Не удалось создать Person")));
    }
}
