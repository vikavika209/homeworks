package medical_service.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PersonClientTest {

    @Autowired
    private PersonClient personClient;

    @Test
    void testVerifyPerson() {
        ResponseEntity<Boolean> isValid = personClient.verifyPerson("Иванов Иван Иванович", "1234567890");
        Assertions.assertTrue(Boolean.TRUE.equals(isValid.getBody()));
    }

    @Test
    void testNotVerifyPerson() {
        ResponseEntity<Boolean> isValid = personClient.verifyPerson("Петров Иван Иванович", "1234567890");
        Assertions.assertFalse(Boolean.TRUE.equals(isValid.getBody()));
    }
}
