package medical_service.repository;

import jakarta.persistence.LockModeType;
import medical_service.entity.VaccinationPoint;
import medical_service.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    Optional<Vaccine> findByName(String vaccineName);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vaccine v WHERE v.id = :id")
    VaccinationPoint findByIdForUpdate(@Param("id") Long id);
}
