package medical_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medical_service.entity.Vaccination;
import medical_service.repository.VaccinationRepository;
import medical_service.service.VaccinationService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccinationEventScheduler {

    private final VaccinationRepository vaccinationRepository;
    private final VaccinationService vaccinationService;

    @Scheduled(fixedDelayString = "${scheduler.vaccination.delay:10000}")
    @SchedulerLock(name = "VaccinationEventScheduler_processNewVaccinations",
            lockAtMostFor = "1m",
            lockAtLeastFor = "10s")
    @Transactional
    public void processNewVaccinations() {
        List<Vaccination> unsentVaccinations = vaccinationRepository.findAllByIsSentToKafkaFalse();
        log.info("Найдено {} новых вакцинаций для отправки в Kafka", unsentVaccinations.size());

        for (Vaccination vaccination : unsentVaccinations) {
            try {
                vaccinationService.sendVaccinationEvent(vaccination);
                vaccination.setSentToKafka(true);
            } catch (Exception e) {
                log.error("Ошибка при отправке вакцинации с id={} в Kafka: {}", vaccination.getId(), e.getMessage());
            }
        }
        vaccinationRepository.saveAll(unsentVaccinations);
    }
}
