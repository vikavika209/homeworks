package com.example.service;

import com.example.dto.PersonDTO;
import com.example.entity.IdentityDocument;
import com.example.entity.Person;
import com.example.exeption.PassportAlreadyExistException;
import com.example.exeption.PersonNotFoundException;
import com.example.mapper.PersonMapper;
import com.example.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;
    private final AddressService addressService;
    private final PersonMapper personMapper;
    private final IdentityDocumentService identityDocumentService;

    public PersonService(
            PersonRepository personRepository,
            AddressService addressService,
            PersonMapper personMapper,
            IdentityDocumentService identityDocumentService
    ) {
        this.personRepository = personRepository;
        this.addressService = addressService;
        this.personMapper = personMapper;
        this.identityDocumentService = identityDocumentService;
    }

    @Transactional
    public PersonDTO save(Person person) {
        log.info(">>> сохранение Person с именем = {}.", person.getFullName());

            if (!isThePassportExist(person.getPassportData())) {
                log.info("Паспорт уникален >>> продолжаем сохранение.");

                person.getDocuments().forEach(doc -> doc.setPerson(person));
                log.info("Установлены документы для Person с id = {}", person.getId());
                person.getContacts().forEach(contact -> contact.setPerson(person));
                log.info("Установлены контакты для Person с id = {}", person.getId());

                person.getAddresses().forEach(address ->
                        log.info("Адреса перед сохранением: id={}, fullAddress={}, region={}", address.getId(), address.getFullAddress(), address.getRegion())
                );
                person.getAddresses().forEach(address -> address.getPersons().add(person));
                log.info("Установлены адреса для Person с id = {}", person.getId());

                Person saved = personRepository.save(person);
                log.info("Person с id = {} успешно сохранен.", saved.getId());
                log.info("ID адреса после сохранения: {}", saved.getAddresses().get(0).getId());

                IdentityDocument passport = new IdentityDocument("Паспорт", person.getPassportData(), person);
                identityDocumentService.save(passport);

                log.info("Сохранен новый гражданин: {}", person);

                PersonDTO personToReturn = personMapper.toDTO(saved);
                log.info("Person >>> PersonDTO успешно.");

                return personToReturn;
            } else {
                throw new PassportAlreadyExistException("Паспорт с таким номером уже существует.");
            }
    }


    @Transactional
    public PersonDTO update(Person person) {
        Person existing = personRepository.findByIdForUpdate(person.getId())
                .orElseThrow(() -> new PersonNotFoundException("Не удалось найти гражданина с id = " + person.getId()));

        existing.setFullName(person.getFullName());
        existing.setPassportData(person.getPassportData());

        existing.getDocuments().clear();
        existing.getDocuments().addAll(person.getDocuments());
        existing.getDocuments().forEach(doc -> doc.setPerson(existing));

        existing.getContacts().clear();
        existing.getContacts().addAll(person.getContacts());
        existing.getContacts().forEach(contact -> contact.setPerson(existing));

        existing.getAddresses().clear();
        existing.getAddresses().addAll(person.getAddresses());
        existing.getAddresses().forEach(address -> address.getPersons().add(existing));

        Person saved = personRepository.save(existing);

        log.info("Гражданин обновлён: {}", saved);
        return personMapper.toDTO(saved);
    }

    public Page<Person> findAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("fullName").ascending());
        Page<Person> persons = personRepository.findAll(pageable);
        log.info("Получен список всех граждан длинной: {}", persons.getTotalElements());
        return persons;
    }

    @Transactional(readOnly = true)
    public Page<PersonDTO> findAllByRegion(String region, Pageable pageable) {
        List<PersonDTO> personsDTOFromTheRegion = new ArrayList<>();

        Set<Person> personsFromTheRegion = addressService.findAllPersonsByRegion(region);

        if (!personsFromTheRegion.isEmpty()) {
            log.info("Получен список граждан из региона {}.", region);
        }
        else {
            log.info("Cписок граждан из региона {} пустой.", region);
        }

        for (Person person : personsFromTheRegion) {
            personsDTOFromTheRegion.add(personMapper.toDTO(person));
        }
        return new PageImpl<>(personsDTOFromTheRegion, pageable, personsFromTheRegion.size());
    }

    public PersonDTO findById(int id) {
        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            log.info("Гражданин с id: {} не найден >>> возврат null.", id);
        }
        else {
            log.info("Найден гражданин с id: {}.", id);
        }
        return personMapper.toDTO(person);
    }

    public void delete(int id) {
        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            log.info("Гражданин не найден, удаление отменено.");
            return;
        }
        else {
            log.info("Удаление гражданина с id: {}", id);
        }
        personRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean verifyIdentity(String name, String documentData) {
        log.info("Проверка связки: имя = {}, документ = {}", name, documentData);

        List<Person> personsWithTheName = personRepository.findByFullName(name);

        if (personsWithTheName.isEmpty()) {
            log.info("Нет граждан с именем: {}", name);
            return false;
        }

        return personsWithTheName.stream()
                .flatMap(person -> person.getDocuments().stream())
                .anyMatch(doc -> {
                    boolean match = Objects.equals(doc.getNumber(), documentData);
                    if (match) {
                        log.info("Найден документ: {} у гражданина с именем: {}", documentData, name);
                    }
                    return match;
                });
    }

    private boolean isThePassportExist(String passportNumber) {
        return identityDocumentService.existsByDocumentNumber(passportNumber);
    }

    public Map<String, String> getByPassport(String passport){
        Map<String, String> result = new HashMap<>();
        log.info("Поиск гражданина с паспортом = {}.", passport);
        Person personFromData = personRepository.findByPassportData(passport).orElse(null);

        if (personFromData == null) {
            log.error("Гражданин с паспортом = {} не найден.", passport);
            throw new PersonNotFoundException("Гражданин с паспортом: " + passport + " не найден.");
        }
        log.info("Найдем гражданин с паспортом = {}; Имя = {}.", passport, personFromData.getFullName());

        result.put("Имя", personFromData.getFullName());
        result.put("Паспорт", personFromData.getPassportData());

        return result;
    }
}
