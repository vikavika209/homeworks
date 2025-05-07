package medical_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import medical_service.dto.VaccinationDTO;
import medical_service.dto.VaccinationEventDto;
import medical_service.entity.Vaccination;
import medical_service.mapper.VaccinationMapper;
import medical_service.repository.VaccinationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class VaccinationService {
    private final VaccinationRepository vaccinationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final VaccinationMapper vaccinationMapper;

    @Value("${kafka.topic.vaccination}")
    private String topic;

    public VaccinationService(VaccinationRepository vaccinationRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, VaccinationMapper vaccinationMapper) {
        this.vaccinationRepository = vaccinationRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.vaccinationMapper = vaccinationMapper;
    }

    public Vaccination create(Vaccination vaccination) {
        log.info("Создание вакцины с id: {}", vaccination.getId());
        log.info("Статус isSentToKafka = {}", vaccination.isSentToKafka());
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

    @Transactional
    public void saveAll(List<Vaccination> vaccinations) {
        for (Vaccination vaccination : vaccinations) {

        }
        vaccinationRepository.saveAll(vaccinations);
    }

    @Transactional(readOnly = true)
    public Page<VaccinationDTO> getAllVaccinationsForPerson(String passport, Pageable pageable) {
        Page<Vaccination> result = vaccinationRepository.findAllByIdentityDocument(passport, pageable);
        log.info("Поиск вакцинаций для гражданина с паспортом: {}", passport);
        Page<VaccinationDTO> dtoPage = result.map(vaccinationMapper::toDto);
        return dtoPage;
    }

    public void sendVaccinationEvent(Vaccination vaccination){
        try {
            VaccinationEventDto dto = new VaccinationEventDto(
                    vaccination.getId(),
                    vaccination.getIdentityDocument(),
                    vaccination.getVaccine().getName(),
                    vaccination.getVaccinationDate()
            );
            String jsonMessage = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(topic, String.valueOf(vaccination.getId()), jsonMessage);
            log.info("Вакцинация отправлена в Kafka: {}", jsonMessage);
        } catch (   JsonProcessingException e) {
            log.error("Ошибка сериализации события вакцинации: {}", e.getMessage());
        }
    }
}
