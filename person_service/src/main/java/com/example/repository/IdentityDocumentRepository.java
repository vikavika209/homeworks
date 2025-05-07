package com.example.repository;

import com.example.entity.IdentityDocument;
import com.example.entity.Person;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Integer> {
    Page<IdentityDocument> findAll(Pageable pageable);
    List<IdentityDocument> findByPerson(Person person);
    @Query("SELECT d.number FROM IdentityDocument d")
    List<String> findAllDocumentNumbers();
    boolean existsByNumber(String documentNumber);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM IdentityDocument d WHERE d.id = :id")
    Optional<IdentityDocument> findByIdForUpdate(@Param("id") Integer id);
}
