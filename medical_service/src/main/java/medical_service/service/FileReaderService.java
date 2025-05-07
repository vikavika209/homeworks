package medical_service.service;

import com.opencsv.CSVReader;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import medical_service.client.PersonClient;
import medical_service.dto.VaccinationFileData;
import medical_service.entity.Vaccination;
import medical_service.entity.VaccinationPoint;
import medical_service.entity.Vaccine;
import medical_service.exception.FileReadingException;
import medical_service.exception.PersonServiceResponceException;
import medical_service.exception.UnknownException;
import medical_service.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileReaderService {

    private final VaccinationService vaccinationService;
    private final VaccinationPointService vaccinationPointService;
    private final VaccineService vaccineService;
    private final PersonClient personClient;

    public FileReaderService(VaccinationService vaccinationService, VaccinationPointService vaccinationPointService, VaccineService vaccineService, PersonClient personClient) {
        this.vaccinationService = vaccinationService;
        this.vaccinationPointService = vaccinationPointService;
        this.vaccineService = vaccineService;
        this.personClient = personClient;
    }

    @Transactional
    public List<VaccinationFileData> getVaccinationInfo(MultipartFile file) {
        List<VaccinationFileData> allVaccinations = parseTheFile(file);
        List<Vaccination> vaccinationBatch = new ArrayList<>();
        int batchSize = 50;

        for (VaccinationFileData vaccinationFileData : allVaccinations) {
            VaccinationPoint vaccinationPoint = getOrCreateVaccinationPoint(
                    vaccinationFileData.getPointCertificate(),
                    vaccinationFileData.getPointName(),
                    vaccinationFileData.getPointAddress()
            );

            Vaccine vaccine = getOrCreateVaccine(vaccinationFileData.getVaccineName());

            Vaccination vaccination = new Vaccination();
            vaccination.setPatientFullName(vaccinationFileData.getFullName());
            vaccination.setIdentityDocument(vaccinationFileData.getPassport());
            vaccination.setVaccinationDate(vaccinationFileData.getVaccinationDate());
            vaccination.setVaccinationPoint(vaccinationPoint);
            vaccination.setVaccine(vaccine);

            vaccinationBatch.add(vaccination);

            if(vaccinationBatch.size() >= batchSize) {
                vaccinationService.saveAll(vaccinationBatch);
                vaccinationBatch.clear();
            }
        }

        if (!vaccinationBatch.isEmpty()) {
            vaccinationService.saveAll(vaccinationBatch);
        }
        return allVaccinations;
    }

    private VaccinationPoint getOrCreateVaccinationPoint(String pointCertificate, String pointName, String pointAddress){
        VaccinationPoint point = vaccinationPointService.getVaccinationPoint(pointCertificate);

        if (point == null) {

            log.info("Пункт не найден >>> создание нового пункта с номером: {}," +
                    "названием: {}, адресом: {}.", pointCertificate, pointName, pointAddress);

            VaccinationPoint newPoint = new VaccinationPoint();
            newPoint.setCertificateNumber(pointCertificate);
            newPoint.setName(pointName);

            String city = pointAddress.split("\\s+")[0];
            newPoint.setCity(city);

            newPoint.setAddress(pointAddress);

            return vaccinationPointService.create(newPoint);
        }
        else return point;
    }

    private Vaccine getOrCreateVaccine(String vaccineName){
        Vaccine vaccine = vaccineService.getVaccineByName(vaccineName);

        if (vaccine == null) {
            log.info("Вакцина не найдена >>> создание вакцины с названием: {}.", vaccineName);

            Vaccine newVaccine = new Vaccine();
            newVaccine.setName(vaccineName);

            return vaccineService.create(newVaccine);
        }
        else {
            return vaccine;
        }
    }

    private boolean validatePersonData(String fullName, String passport){
        try {
            ResponseEntity<Boolean> response = personClient.verifyPerson(fullName, passport);
            if (response.getStatusCode() == HttpStatus.OK) {
                boolean isValid = response.getBody();
                if (!isValid) {
                    throw new ValidationException("Валидация не пройдена для гражданина: " + fullName + ", паспорт: " + passport);
                } else {
                    log.info("Данные для имени: {} и паспорта: {} валидны.", fullName, passport);
                    return true;
                }
            } else throw new PersonServiceResponceException("Статус ответа отличен от Ok.");
        } catch (RetryableException e) {
            throw new PersonServiceResponceException("Ошибка с доступом к Person Service.");
        }
    }

    public List<VaccinationFileData> parseTheFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new FileReadingException("Файл отсутствует или пустой.");
        }

        List<VaccinationFileData> allVaccinations = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {

            String[] nextLine;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while ((nextLine = reader.readNext()) != null) {
                String fullName = nextLine[0];
                String passport = nextLine[1];
                LocalDate vaccinationDate = LocalDate.parse(nextLine[2], formatter);
                String vaccineName = nextLine[3];
                String pointCertificate = nextLine[4];
                String pointName = nextLine[5];
                String pointAddress = nextLine[6];

                if (validatePersonData(fullName, passport)) {
                    VaccinationFileData data = new VaccinationFileData();
                    data.setFullName(fullName);
                    data.setPassport(passport);
                    data.setVaccinationDate(vaccinationDate);
                    data.setVaccineName(vaccineName);
                    data.setPointCertificate(pointCertificate);
                    data.setPointName(pointName);
                    data.setPointAddress(pointAddress);

                    allVaccinations.add(data);
                }
            }
        } catch (IOException e) {
            throw new FileReadingException("Ошибка чтения CSV файла: " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new FileReadingException("Ошибка парсинга даты: " + e.getMessage());
        } catch (PersonServiceResponceException e){
            throw new PersonServiceResponceException("Ошибка с доступом к Person Service.");
        } catch (Exception e) {
            throw new UnknownException("Неизвестная ошибка: " + e);
        }
        return allVaccinations;
    }
}
