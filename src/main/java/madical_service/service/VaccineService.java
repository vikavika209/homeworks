package madical_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import madical_service.entity.Vaccine;
import madical_service.repository.VaccineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Getter
@Setter
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
