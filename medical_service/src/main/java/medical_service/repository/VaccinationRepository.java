package medical_service.repository;

import jakarta.persistence.LockModeType;
import medical_service.entity.Vaccination;
import medical_service.entity.VaccinationPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vaccination v WHERE v.id = :id")
    VaccinationPoint findByIdForUpdate(@Param("id") Long id);
    @Query("""
        SELECT v FROM Vaccination v
        JOIN FETCH v.vaccine
        JOIN FETCH v.vaccinationPoint
        WHERE v.identityDocument = :passport
        """)
    Page<Vaccination> findAllByIdentityDocument(String passport, Pageable pageable);
    List<Vaccination> findAllByIsSentToKafkaFalse();
    }
