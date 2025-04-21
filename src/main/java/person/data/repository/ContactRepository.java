package person.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import person.data.entity.Contact;
import person.data.entity.Person;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Page<Contact> findAll (Pageable pageable);
    List<Contact> findByPerson(Person person);
    Contact updateById(Contact contact);
}
