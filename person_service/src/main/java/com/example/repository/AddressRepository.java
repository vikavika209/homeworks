package com.example.repository;

import com.example.entity.Address;
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
import java.util.Set;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    Page<Address> findAll(Pageable pageable);
    List<Address> findByPersons(Person person);
    @Query("SELECT a.fullAddress FROM Address a")
    List<String> findAllAddresses();
    @Query("SELECT p FROM Person p JOIN p.addresses a WHERE a.region = :region")
    Set<Person> findPersonsByRegion(@Param("region") String region);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Address a WHERE a.id = :id")
    Optional<Address> findByAddressIdForUpdate(@Param("id") Integer id);
}
