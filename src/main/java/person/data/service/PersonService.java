package person.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import person.data.dto.PersonDTO;
import person.data.entity.Person;
import person.data.exeption.PassportAlreadyExistException;
import person.data.mapper.PersonMapper;
import person.data.repository.PersonRepository;

import java.util.*;

@Service
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;
    private final AddressService addressService;
    private final PersonMapper personMapper;
    private IdentityDocumentService identityDocumentService;

    public PersonService(PersonRepository personRepository, AddressService addressService, PersonMapper personMapper, IdentityDocumentService identityDocumentService) {
        this.personRepository = personRepository;
        this.addressService = addressService;
        this.personMapper = personMapper;
        this.identityDocumentService = identityDocumentService;
    }

    @Transactional
    public Person save(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);

        if (!isThePassportExist(person.getPassportData())) {

            person.getDocuments().forEach(doc -> doc.setPerson(person));
            person.getContacts().forEach(contact -> contact.setPerson(person));
            person.getAddresses().forEach(address -> address.getPersons().add(person));

            personRepository.save(person);

            log.info("Сохранен новый гражданин: {}", person);

            return person;
        }

        else {
            throw new PassportAlreadyExistException("Паспорт с таким номером уже существует.");
        }
    }


    @Transactional
    public Person update(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);
        Optional<Person> existing = personRepository.findByIdForUpdate(person.getId());

        if (existing.isEmpty()) {
            log.info("Гражданин не найден, создание нового: {}", person);
            return personRepository.save(person);
        }

        Person updatedPerson = personRepository.save(person);
        log.info("Гражданин обновлён: {}", updatedPerson);
        return updatedPerson;
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
}
