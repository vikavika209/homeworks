package medical_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import medical_service.entity.Vaccine;
import medical_service.repository.VaccineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class VaccineService {
    private final VaccineRepository vaccineRepository;

    public VaccineService(VaccineRepository vaccineRepository) {
        this.vaccineRepository = vaccineRepository;
    }

    public Vaccine create(Vaccine vaccine) {
        log.info("Создание вакцины с названием: {}", vaccine.getName());
        return vaccineRepository.save(vaccine);
    }

    public Vaccine getVaccineByName(String name) {
        Optional<Vaccine> optionalVaccine = vaccineRepository.findByName(name);

        if (optionalVaccine.isPresent()) {
            log.info("Найдена вакцина с названием: {}.", name);
            return optionalVaccine.get();
        }

        else {
            log.info("Вакцина с названием: {} не найдена.", name);
            return null;
        }
    }

    @Transactional
    public Vaccine updateVaccine(Vaccine vaccine) {
        if(vaccineRepository.findByIdForUpdate(vaccine.getId()) == null) {
            throw new EntityNotFoundException("Вакцина с номером " + vaccine.getId()+ " не найдена");
        }
        return vaccineRepository.save(vaccine);
    }

    public void delete(Vaccine vaccine) {
        vaccineRepository.delete(vaccine);
    }
}
