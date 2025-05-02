package madical_service.controller;

import madical_service.entity.Vaccination;
import madical_service.exception.PersonServiceResponceException;
import madical_service.service.FileReaderService;
import madical_service.service.VaccinationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VaccinationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VaccinationService vaccinationService;

    @MockBean
    private FileReaderService fileReaderService;

    @Test
    void getAllVaccinationsByPassport_ReturnsOk() throws Exception {
        List<Vaccination> mockVaccinations = List.of(new Vaccination(), new Vaccination());
        Mockito.when(vaccinationService.getAllVaccinationsForPerson("1234567890"))
                .thenReturn(mockVaccinations);

        mockMvc.perform(MockMvcRequestBuilders.get("/vaccinations/vaccination")
                        .param("document", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    void getAllVaccinationsByPassport_ReturnsNoContent() throws Exception {
        Mockito.when(vaccinationService.getAllVaccinationsForPerson("0000000000"))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/vaccinations/vaccination")
                        .param("document", "0000000000"))
                .andExpect(status().isNoContent());
    }
}