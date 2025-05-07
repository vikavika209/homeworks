package medical_service.controller;

import medical_service.exception.PersonServiceResponceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class VaccinationInegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadVaccinationFile_WhenPersonServiceUnavailable_ShouldThrowCustomException() throws Exception {
        ClassPathResource resource = new ClassPathResource("test_vaccination_data.csv");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                resource.getFilename(),
                "text/csv",
                resource.getInputStream()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/vaccinations/upload")
                        .file(multipartFile))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonServiceResponceException));
    }

}
