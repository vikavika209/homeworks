package person.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import person.data.entity.Address;
import person.data.entity.Person;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Page<Address> findAll(Pageable pageable);
    List<Address> findByPersons(Person person);
    Address updateById(Address address);
}
