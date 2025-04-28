package madical_service.service;

import com.opencsv.CSVReader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import madical_service.client.PersonClient;
import madical_service.entity.Vaccination;
import madical_service.entity.VaccinationPoint;
import madical_service.entity.Vaccine;
import madical_service.exception.FileReadingException;
import madical_service.exception.PersonServiceResponceException;
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
@Getter
@Setter
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
    public void getVaccinationInfo(MultipartFile file) {
        try(CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))){
            String[] nextLine;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<Vaccination> vaccinationBatch = new ArrayList<>();
            int batchSize = 50;

            while ((nextLine = reader.readNext()) != null) {
                String fullName = nextLine[0];
                String passport = nextLine[1];
                LocalDate vaccinationDate = LocalDate.parse(nextLine[2], formatter);
                String vaccineName = nextLine[3];
                String pointCertificate = nextLine[4];
                String pointName = nextLine[5];
                String pointAddress = nextLine[6];

                if (validatePersonData(fullName, passport)) {

                    VaccinationPoint vaccinationPoint = getOrCreateVaccinationPoint(pointCertificate, pointName, pointAddress);
                    Vaccine vaccine = getOrCreateVaccine(vaccineName);

                    Vaccination vaccination = new Vaccination();
                    vaccination.setPatientFullName(fullName);
                    vaccination.setIdentityDocument(passport);
                    vaccination.setVaccinationDate(vaccinationDate);
                    vaccination.setVaccine(vaccine);
                    vaccination.setVaccinationPoint(vaccinationPoint);

                    vaccinationService.create(vaccination);
                    vaccinationBatch.add(vaccination);

                    if (vaccinationBatch.size() >= batchSize) {
                        vaccinationService.saveAll(vaccinationBatch);
                        vaccinationBatch.clear();
                    }
                }

                if (!vaccinationBatch.isEmpty()) {
                    vaccinationService.saveAll(vaccinationBatch);
                }
            }

        }catch (IOException e) {
            throw new FileReadingException("Ошибка чтения CSV файла: проблемы с доступом или вводом-выводом: " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new FileReadingException("Ошибка парсинга даты: неверный формат даты в файле: " + e.getMessage());
        } catch (Exception e) {
            throw new FileReadingException("Неизвестная ошибка при обработке CSV файла: " + e.getMessage());
        }
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
        else {
            return point;
        }

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

    private void saveAll(List<Vaccination> vaccinations) {
        vaccinationService.saveAll(vaccinations);
    }

    private boolean validatePersonData(String fullName, String passport){
        ResponseEntity<Boolean> response = personClient.verifyPerson(fullName, passport);
        if (response.getStatusCode() == HttpStatus.OK) {
            boolean isValid = response.getBody();
            if (!isValid) {
                throw new FileReadingException("Валидация не пройдена для гражданина: " + fullName + ", паспорт: " + passport);
            } else {
                log.info("Данные для имени: {} и паспорта: {} валидны.", fullName, passport);
                return true;
            }
        }
        else throw new PersonServiceResponceException("Ошибка с доступом к Person Service.");
    }
}
