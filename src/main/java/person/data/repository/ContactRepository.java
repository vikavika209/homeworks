package person.data.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import person.data.entity.Address;
import person.data.entity.Contact;
import person.data.entity.Person;

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
