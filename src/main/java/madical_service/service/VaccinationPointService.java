package madical_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import madical_service.entity.VaccinationPoint;
import madical_service.repository.VaccinationPointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Getter
@Setter
public class VaccinationPointService {

    private final VaccinationPointRepository vaccinationPointRepository;

    public VaccinationPointService(VaccinationPointRepository vaccinationPointRepository) {
        this.vaccinationPointRepository = vaccinationPointRepository;
    }

    public VaccinationPoint create(VaccinationPoint vaccinationPoint) {
        log.info("Создание точки вакцинации: уникальный номер = {}, название = {}.", vaccinationPoint.getCertificateNumber(), vaccinationPoint.getName());
        return vaccinationPointRepository.save(vaccinationPoint);
    }

    public VaccinationPoint getVaccinationPoint(String certificateNumber) {
        log.info("Поиск точки вакцинации с уникальным номером: {}.", certificateNumber);
        Optional<VaccinationPoint> vaccinationPoint = vaccinationPointRepository.findById(certificateNumber);

        if (vaccinationPoint.isPresent()) {
            log.info("Найдена точка вакцинации с уникальным номером: {}.", certificateNumber);
            return vaccinationPoint.get();
        }
        else {
            log.info("Точка вакцинации с уникальным номером: {} не найдена.", certificateNumber);
            return null;
        }
    }

    @Transactional
    public VaccinationPoint updateVaccinationPoint(VaccinationPoint vaccinationPoint) {
        if (vaccinationPointRepository.findByIdForUpdate(vaccinationPoint.getCertificateNumber()) == null) {
            throw new EntityNotFoundException("Пункт вакцинации с номером " + vaccinationPoint.getCertificateNumber() + " не найден");
        }
        return vaccinationPointRepository.save(vaccinationPoint);
    }

    public void delete(VaccinationPoint vaccinationPoint) {
        log.info("Удаление пункта вакцинации с сертификатом: {}", vaccinationPoint.getCertificateNumber());
        vaccinationPointRepository.delete(vaccinationPoint);
    }
}
