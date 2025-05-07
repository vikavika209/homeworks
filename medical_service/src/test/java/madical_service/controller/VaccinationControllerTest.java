package medical_service.controller;

import medical_service.dto.VaccinationDTO;
import medical_service.service.FileReaderService;
import medical_service.service.VaccinationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VaccinationController.class)
class VaccinationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VaccinationService vaccinationService;

    @MockBean
    private FileReaderService fileReaderService;

    @Test
    void getAllVaccinationsByPassport_ReturnsOk() throws Exception {
        Page<VaccinationDTO> page = new PageImpl<>(List.of(new VaccinationDTO(), new VaccinationDTO()));
        Mockito.when(vaccinationService.getAllVaccinationsForPerson(eq("1234567890"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/vaccinations/vaccination")
                        .param("document", "1234567890"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

    }

    @Test
    void getAllVaccinationsByPassport_ReturnsNoContent() throws Exception {
        Page<VaccinationDTO> emptyPage = new PageImpl<>(List.of());

        Mockito.when(vaccinationService.getAllVaccinationsForPerson(eq("0000000000"), any(Pageable.class)))
                .thenReturn(emptyPage);


        mockMvc.perform(MockMvcRequestBuilders.get("/vaccinations/vaccination")
                        .param("document", "0000000000"))
                .andExpect(status().isNoContent());
    }
}