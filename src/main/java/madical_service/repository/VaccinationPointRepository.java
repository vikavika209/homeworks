package madical_service.repository;

import jakarta.persistence.LockModeType;
import madical_service.entity.VaccinationPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VaccinationPointRepository extends JpaRepository<VaccinationPoint, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VaccinationPoint v WHERE v.certificateNumber = :id")
    VaccinationPoint findByIdForUpdate(@Param("id") String id);
}
