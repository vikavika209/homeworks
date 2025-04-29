package madical_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import madical_service.entity.Vaccination;
import madical_service.repository.VaccinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class VaccinationService {
    private final VaccinationRepository vaccinationRepository;

    public VaccinationService(VaccinationRepository vaccinationRepository) {
        this.vaccinationRepository = vaccinationRepository;
    }

    public Vaccination create(Vaccination vaccination) {
        log.info("Создание вакцины с id: {}", vaccination.getId());
        return vaccinationRepository.save(vaccination);
    }

    public Vaccination getVaccinationById(long id) {
        return vaccinationRepository.findById(id).orElse(null);

    }

    @Transactional
    public Vaccination update(Vaccination vaccination) {
        if(vaccinationRepository.findByIdForUpdate(vaccination.getId()) == null) {
            throw new EntityNotFoundException("Вакцинация с номером " + vaccination.getId() + " не найдена");
        }

        return vaccinationRepository.save(vaccination);
    }

    public void delete(long id) {
        vaccinationRepository.deleteById(id);
    }

    public void saveAll(List<Vaccination> vaccinations){
        vaccinationRepository.saveAll(vaccinations);
    }

    @Transactional(readOnly = true)
    public List<Vaccination> getAllVaccinationsForPerson(String passport) {
        log.info("Поиск вакцинаций для гражданина с паспортом: {}", passport);
        return vaccinationRepository.findAllByIdentityDocument(passport);
    }
}
