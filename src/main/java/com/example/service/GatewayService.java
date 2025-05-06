package com.example.service;

import com.example.client.MedicalClient;
import com.example.client.PersonClient;
import com.example.client.QRCodeClient;
import com.example.dto.QRCodeDTO;
import com.example.dto.VaccinationDTO;
import com.example.exception.NoInformationHasBeenFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Slf4j
public class GatewayService {
    private final PersonClient personClient;
    private final MedicalClient medicalClient;
    private final QRCodeClient qrCodeClient;

    public GatewayService(PersonClient personClient, MedicalClient medicalClient, QRCodeClient qrCodeClient) {
        this.personClient = personClient;
        this.medicalClient = medicalClient;
        this.qrCodeClient = qrCodeClient;
    }

    private Map<String, String> personVaccinationData (String passport){
        log.info("Получение данных о вакцинации для Person с паспортом = {}.", passport);

        ResponseEntity<Map<String, String>> response = personClient.getPersonByPassport(passport);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Не удалось получить данные о Person по паспорту = {}.", passport);
            throw new NoInformationHasBeenFoundException("Данные о человеке не найдены.");
        }

        Map<String, String> personData = response.getBody();
        if (personData == null) {
            log.error("Данные о Person по паспорту = {} не найдены.", passport);
            throw new NoInformationHasBeenFoundException("Данные о человеке не найдены");
        }

        Page<VaccinationDTO> vaccinations = medicalClient.getAllVaccinationsByPassport(
                passport, 0, 10, "vaccinationDate,desc"
        );

        if (!vaccinations.isEmpty()) {
            log.info("Данные о вакцинации для Person с паспортом = {} получены.", passport);
            VaccinationDTO last = vaccinations.getContent().get(0);
            String formattedDate = last.getVaccinationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            personData.put("Дата последней вакцинации", formattedDate);
            personData.put("Название вакцины", last.getVaccineName());
            personData.put("Пункт вакцинации", last.getVaccinationPointName());
        } else {
            log.error("Данные о вакцинации для Person с паспортом = {} отсутствуют.", passport);
            personData.put("Дата последней вакцинации", "нет данных");
            personData.put("Название вакцины", "нет данных");
            personData.put("Пункт вакцинации", "нет данных");
        }
        log.info("Поиск данных о вакцинации для Person с паспортом = {} закончен.", passport);
        return personData;
    }

    public Map<String, String> getPersonVaccinationFullData (String passport){
        log.info("Получение данных о QR коде для Person с паспортом = {}.", passport);
        Map<String, String> personData = personVaccinationData(passport);
        ResponseEntity<QRCodeDTO> responseEntity = qrCodeClient.getQRCodeByPassport(passport);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            QRCodeDTO qrCodeDTO = responseEntity.getBody();
            if (qrCodeDTO != null) {
                log.info("Данные о QR коде для Person с паспортом = {} получены.", passport);
                personData.put("Id вакцинации", qrCodeDTO.getVaccinationId().toString());
                personData.put("QR код", qrCodeDTO.getHash());
            } else {
                log.info("Данные о QR коде для Person с паспортом = {} пустые.", passport);
                personData.put("Id вакцинации", "нет данных");
                personData.put("QR код", "нет данных");
            }

        }else {
            log.error("Данные о QR коде для Person с паспортом = {} отсутствуют.", passport);
            personData.put("Id вакцинации", "нет данных");
            personData.put("QR код", "нет данных");
        }
        log.info("Агрегированные данные по паспорту = {} успешно собраны", passport);
        return personData;
    }
}
