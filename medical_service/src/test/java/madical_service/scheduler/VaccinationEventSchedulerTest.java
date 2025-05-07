package medical_service.scheduler;

import medical_service.entity.Vaccination;
import medical_service.repository.VaccinationRepository;
import medical_service.service.VaccinationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaccinationEventSchedulerTest {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private VaccinationService vaccinationService;

    @InjectMocks
    private VaccinationEventScheduler vaccinationEventScheduler;

    @Test
    void testProcessNewVaccinations_setsSentToKafkaTrue() {
        Vaccination v1 = new Vaccination();
        v1.setSentToKafka(false);

        Vaccination v2 = new Vaccination();
        v2.setSentToKafka(false);

        List<Vaccination> vaccinations = List.of(v1, v2);
        when(vaccinationRepository.findAllByIsSentToKafkaFalse()).thenReturn(vaccinations);

        vaccinationEventScheduler.processNewVaccinations();

        verify(vaccinationService).sendVaccinationEvent(v1);
        verify(vaccinationService).sendVaccinationEvent(v2);

        assertTrue(v1.isSentToKafka());
        assertTrue(v2.isSentToKafka());

        verify(vaccinationRepository).saveAll(vaccinations);
    }
}
