package person.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import person.data.entity.Address;
import person.data.entity.Person;

import java.util.List;
import java.util.Set;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Page<Person> findAll(Pageable pageable);
    Set<Person> findAllByAddress(Address address);
    Person updateById(Person person);
    List<Person> findByFullName(String fullName);
}
