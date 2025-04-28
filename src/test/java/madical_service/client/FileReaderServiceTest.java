package madical_service.client;

import madical_service.service.FileReaderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
@ActiveProfiles("test")
public class FileReaderServiceTest {

    @Autowired
    private FileReaderService fileReaderService;

    @Test
    void testReadVaccinationCsvFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("test_vaccination_data.csv").getFile());

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "text/csv",
                new FileInputStream(file)
        );

        fileReaderService.getVaccinationInfo(multipartFile);
    }
}

