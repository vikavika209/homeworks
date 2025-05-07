package com.example.controller;

import com.example.client.MedicalClient;
import com.example.dto.VaccinationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalGatewayController.class)
class MedicalGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalClient medicalClient;

    @Test
    void getAllVaccinations_returnsPageImpl_whenDataExists() throws Exception {
        VaccinationDTO dto = new VaccinationDTO();
        dto.setVaccineName("Спутник");
        dto.setVaccinationDate(LocalDate.of(2024, 1, 1));
        dto.setVaccinationPointName("Клиника №1");

        PageImpl<VaccinationDTO> page = new PageImpl<>(List.of(dto));

        when(medicalClient.getAllVaccinationsByPassport("1234567890", 0, 10, "vaccinationDate,desc"))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/medical/vaccination")
                        .param("document", "1234567890")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "vaccinationDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].vaccineName").value("Спутник"))
                .andExpect(jsonPath("$.content[0].vaccinationPointName").value("Клиника №1"));
    }

    @Test
    void getAllVaccinations_throwsException_whenNoData() throws Exception {
        PageImpl<VaccinationDTO> emptyPage = new PageImpl<>(Collections.emptyList());

        when(medicalClient.getAllVaccinationsByPassport("0000000000", 0, 10, "vaccinationDate,desc"))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/medical/vaccination")
                        .param("document", "0000000000")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "vaccinationDate,desc"))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Не удалось получить вакцинации")));
    }

    @Test
    void upload_returnsOk_whenSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "данные".getBytes());

        when(medicalClient.saveVaccination(any(MultipartFile.class)))
                .thenReturn(ResponseEntity.ok("Файл успешно обработан"));

        mockMvc.perform(multipart("/api/v1/medical/vaccinations/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Файл успешно обработан"));
    }

    @Test
    void upload_throwsException_whenError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "данные".getBytes());

        when(medicalClient.saveVaccination(any(MultipartFile.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка загрузки"));

        mockMvc.perform(multipart("/api/v1/medical/vaccinations/upload")
                        .file(file))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Ошибка с получением информации из medical service: Не удалось обработать файл."
                )));
    }
}
