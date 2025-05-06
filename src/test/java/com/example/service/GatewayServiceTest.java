package com.example.service;

import com.example.client.MedicalClient;
import com.example.client.PersonClient;
import com.example.client.QRCodeClient;
import com.example.dto.QRCodeDTO;
import com.example.dto.VaccinationDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GatewayServiceTest {

    @Mock
    private PersonClient personClient;

    @Mock
    private MedicalClient medicalClient;

    @Mock
    private QRCodeClient qrCodeClient;

    @InjectMocks
    private GatewayService gatewayService;

    @Test
    public void getPersonVaccinationFullData_allDataAvailable_returnsFullMap() {
        String passport = "1234567890";

        Map<String, String> person = new HashMap<>();
        person.put("Имя", "Иван Иванов");

        VaccinationDTO vaccination = new VaccinationDTO();
        vaccination.setVaccinationDate(LocalDate.of(2025, 5, 1));
        vaccination.setVaccineName("Спутник V");
        vaccination.setVaccinationPointName("Клиника №1");

        QRCodeDTO qrCode = new QRCodeDTO();
        qrCode.setVaccinationId(1L);
        qrCode.setHash("abc123");

        when(personClient.getPersonByPassport(passport))
                .thenReturn(ResponseEntity.ok(person));

        when(medicalClient.getAllVaccinationsByPassport(passport, 0, 10, "vaccinationDate,desc"))
                .thenReturn(new PageImpl<>(List.of(vaccination)));

        when(qrCodeClient.getQRCodeByPassport(passport))
                .thenReturn(ResponseEntity.ok(qrCode));

        Map<String, String> result = gatewayService.getPersonVaccinationFullData(passport);

        assertEquals("Иван Иванов", result.get("Имя"));
        assertEquals("Спутник V", result.get("Название вакцины"));
        assertEquals("Клиника №1", result.get("Пункт вакцинации"));
        assertEquals("abc123", result.get("QR код"));
        assertEquals("1", result.get("Id вакцинации"));
    }

    @Test
    void getPersonVaccinationFullData_noVaccination_returnsDefaultValues() {
        String passport = "1234567890";

        when(personClient.getPersonByPassport(passport))
                .thenReturn(ResponseEntity.ok(new HashMap<>()));

        when(medicalClient.getAllVaccinationsByPassport(passport, 0, 10, "vaccinationDate,desc"))
                .thenReturn(new PageImpl<>(List.of()));

        when(qrCodeClient.getQRCodeByPassport(passport))
                .thenReturn(ResponseEntity.ok(null));

        Map<String, String> result = gatewayService.getPersonVaccinationFullData(passport);

        assertEquals("нет данных", result.get("Дата последней вакцинации"));
        assertEquals("нет данных", result.get("QR код"));
    }

    @Test
    void getPersonVaccinationFullData_personClientReturnsNull_throwsException() {
        String passport = "9999999999";

        when(personClient.getPersonByPassport(passport))
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            gatewayService.getPersonVaccinationFullData(passport);
        });

        assertTrue(ex.getMessage().contains("не найдены"));
    }

    @Test
    void getPersonVaccinationFullData_qrService404_returnsDefaults() {
        String passport = "1234567890";

        when(personClient.getPersonByPassport(passport))
                .thenReturn(ResponseEntity.ok(new HashMap<>()));

        when(medicalClient.getAllVaccinationsByPassport(passport, 0, 10, "vaccinationDate,desc"))
                .thenReturn(new PageImpl<>(List.of()));

        when(qrCodeClient.getQRCodeByPassport(passport))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        Map<String, String> result = gatewayService.getPersonVaccinationFullData(passport);

        assertEquals("нет данных", result.get("QR код"));
        assertEquals("нет данных", result.get("Id вакцинации"));
    }
}
