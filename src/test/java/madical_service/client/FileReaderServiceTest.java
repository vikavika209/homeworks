package madical_service.client;

import madical_service.exception.FileReadingException;
import madical_service.service.FileReaderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

    @Test
    void testReadEmptyVaccinationCsvFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "non_existent.csv",
                "text/csv",
                (byte[]) null
        );

        Assertions.assertThrowsExactly(FileReadingException.class, () -> {
            fileReaderService.getVaccinationInfo(multipartFile);
        });
    }
}

