package madical_service.repository;

import jakarta.persistence.LockModeType;
import madical_service.entity.Vaccination;
import madical_service.entity.VaccinationPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vaccination v WHERE v.id = :id")
    VaccinationPoint findByIdForUpdate(@Param("id") Long id);
    List<Vaccination> findAllByIdentityDocument(String passport);
    List<Vaccination> findAllByIsSentToKafkaFalse();
}
