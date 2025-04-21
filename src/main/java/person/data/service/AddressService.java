package person.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import person.data.entity.Address;
import person.data.entity.Contact;
import person.data.entity.Person;
import person.data.repository.AddressRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address save(Address address) {
        try {
            Address savedAddress = addressRepository.save(address);
            log.info("Сохранен новый адрес: {}", address);
            return savedAddress;
        }catch (Exception e) {
            log.error("Ошибка: {} при сохранении адреса: {}", e.getMessage(), address);
            throw new RuntimeException("Ошибка при сохранении адреса: " + address);
        }
    }

    @Transactional
    public Address update(Address address) {
        int addressId = address.getId();
        Address foundAddress = findById(addressId);
        if (foundAddress != null) {
            log.info("Адрес не найден: {}", address);
            foundAddress = save(address);
            log.info("Адрес создан: {}", address);
            return foundAddress;
        }
        else {
            log.info("Адрес: {} обновляется", address);
            Address updatedAddress = addressRepository.updateById(address);
            log.info("Адрес обновлён: ", updatedAddress);
            return updatedAddress;
        }
    }

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
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            log.info("Адрес для удаления с id: {} не найден.", id);
        }
        else {
            log.info("Удаление адреса с id: {}", id);
        }
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

    public Set<Address> findByRegion(String region) {
        Page<Address> allAddresses = findAll();
        if (allAddresses.isEmpty()){
            log.debug("Список адресов пустой.");
        }
        else {
            log.info("Получен список всех адресов.");
        }

        Set<Address> allAddressesFromTheRegion = allAddresses.stream()
                .filter(address ->
                        address.getRegion()
                                .equals(region))
                .collect(Collectors.toSet());

        if (allAddressesFromTheRegion.isEmpty()){
            log.debug("Список адресов региона {} пустой.", region);
        }
        else {
            log.info("Получен список всех адресов для региона {}.", region);
        }
        return allAddressesFromTheRegion;
    }
}
