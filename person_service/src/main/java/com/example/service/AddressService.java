package com.example.service;

import com.example.entity.Address;
import com.example.entity.Person;
import com.example.repository.AddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;


    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Address save(Address address) {

            Address savedAddress = addressRepository.save(address);
            log.info("Сохранен новый адрес: {}", address);
            return savedAddress;
    }

    @Transactional
    public Address update(Address address) {
        int addressId = address.getId();
        Address foundAddress = addressRepository.findByAddressIdForUpdate(addressId).orElse(null);

        if (foundAddress == null) {
            log.info("Адрес не найден: {}", address);
            foundAddress = addressRepository.save(address);
            log.info("Адрес создан: {}", address);
            return foundAddress;
        }
        else {
            log.info("Адрес: {} обновляется", address);
            Address updatedAddress = addressRepository.save(address);
            log.info("Адрес обновлён: ", updatedAddress);
            return updatedAddress;
        }
    }

    @Transactional(readOnly = true)
    public Page<Address> findAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Address> addresses = addressRepository.findAll(pageable);
        log.info("Получен список всех адресов: {}", addresses.getTotalElements());
        return addresses;
    }

    public Address findById(int id) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            log.info("Адрес с id: {} не найден >>> возврат null.", id);
        }
        else {
            log.info("Найден контакт с id: {}.", id);
        }
        return address;
    }

    public void delete(int id) {
        addressRepository.deleteById(id);
    }

    public List<Address> findByPerson(Person person) {
        List<Address> addresses = addressRepository.findByPersons(person);
        if (addresses.isEmpty()) {
            log.info("Адреса для гражданина с id: {} не найдены.", person.getId());
        }
        else {
            log.info("Адреса в кол-ве: {} для гражданина с id: {} найдены.", addresses.size(), person.getId());
        }
        return addresses;
    }

    public Set<Person> findAllPersonsByRegion(String region) {
        return addressRepository.findPersonsByRegion(region);
    }

    public Set<String> getAllFullAddresses(){
        return new HashSet<>(addressRepository.findAllAddresses());
    }
}
