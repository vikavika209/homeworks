package com.example.repository;

import com.example.entity.Contact;
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

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Page<Contact> findAll (Pageable pageable);
    List<Contact> findByPerson(Person person);
    @Query("SELECT c.number FROM Contact c")
    List<String> findAllContacts();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Contact c WHERE c.id = :id")
    Optional<Contact> findByIdForUpdate(@Param("id") Integer id);
}
