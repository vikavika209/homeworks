package person.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import person.data.convertor.PersonConvertor;
import person.data.dto.PersonDTO;
import person.data.entity.Address;
import person.data.entity.Contact;
import person.data.entity.IdentityDocument;
import person.data.entity.Person;
import person.data.repository.PersonRepository;
import java.util.*;

@Service
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;
    private final IdentityDocumentService identityDocumentService;
    private final ContactService contactService;
    private final AddressService addressService;
    private final PersonConvertor personConvertor;

    @Autowired
    public PersonService(PersonRepository personRepository, IdentityDocumentService identityDocumentService, ContactService contactService, AddressService addressService, PersonConvertor personConvertor) {
        this.personRepository = personRepository;
        this.identityDocumentService = identityDocumentService;
        this.contactService = contactService;
        this.addressService = addressService;
        this.personConvertor = personConvertor;
    }

    @Transactional
    public Person save(Person person) {
        try {
            Person savedPerson = personRepository.save(person);
            savePersonsData(savedPerson);
            log.info("Сохранен новый гражданин: {}", person);
            return savedPerson;
        }catch (Exception e) {
            log.error("Ошибка: {} при сохранении гражданина: {}", e.getMessage(), person);
            throw new RuntimeException("Ошибка при сохранении гражданина: " + person);
        }
    }

    @Transactional
    public Person update(Person person) {
        Optional<Person> existing = personRepository.findById(person.getId());

        if (existing.isEmpty()) {
            log.info("Гражданин не найден, создание нового: {}", person);
            return save(person);
        }

        Person updatedPerson = personRepository.updateById(person);
        log.info("Гражданин обновлён: {}", updatedPerson);
        return updatedPerson;
    }

    public Page<Person> findAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("fullName").ascending());
        Page<Person> persons = personRepository.findAll(pageable);
        log.info("Получен список всех граждан длинной: {}", persons.getTotalElements());
        return persons;
    }

    public Page<PersonDTO> findAllByRegion(String region) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("fullName").ascending());
        Set<PersonDTO> personsFromTheRegion = new HashSet<>();

        Set<Address> allAddressesFromTheRegion = addressService.findByRegion(region);

        for (Address address : allAddressesFromTheRegion) {
            Set<Person> persons = personRepository.findAllByAddress(address);
            Set<PersonDTO> personsDTO = new HashSet<>();

            for (Person person : persons) {
                personsDTO.add(personConvertor.personToDTO(person));
            }

            personsFromTheRegion.addAll(personsDTO);
        }
        if (personsFromTheRegion.isEmpty()){
            log.info("Нет граждан из региона: {}", region);
        }
        else {
            log.info("Список граждан из региона: {} получен.", region);
        }
        return SetToPageService.convertSetToPage(personsFromTheRegion, pageable);
    }

    public PersonDTO findById(int id) {
        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            log.info("Гражданин с id: {} не найден >>> возврат null.", id);
        }
        else {
            log.info("Найден гражданин с id: {}.", id);
        }
        return personConvertor.personToDTO(person);
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

    private void saveIdentityDocument(Person person) {
        List<IdentityDocument> identityDocuments = person.getDocuments();
        Page<IdentityDocument> documentsFromStorage = identityDocumentService.findAll();
        Set<IdentityDocument> existingDocuments = new HashSet<>(documentsFromStorage.getContent());

        for (IdentityDocument document : identityDocuments) {
            boolean exists = existingDocuments.stream().anyMatch(existing ->
                    Objects.equals(existing.getNumber(), document.getNumber()));

            if (!exists) {
                identityDocumentService.save(document);
                log.info("Документ: {} успешно сохранён", document);
            }
            else {
                log.info("Документ: {} уже существует", document);
            }
        }
    }

    private void saveContact(Person person) {
        List<Contact> contacts = person.getContacts();
        Page<Contact> contactsFromStorage = contactService.findAll();
        Set<Contact> existingContacts = new HashSet<>(contactsFromStorage.getContent());

        for (Contact contact : contacts) {
            boolean exists = existingContacts.stream().anyMatch(existing ->
                    Objects.equals(existing.getNumber(), contact.getNumber()));

            if (!exists) {
                contactService.save(contact);
                log.info("Контакт: {} успешно сохранён", contact);
            }
            else {
                log.info("Контакт: {} уже существует", contact);
            }
        }
    }

    private void saveAddress(Person person) {
        List<Address> addresses = person.getAddresses();
        Page<Address> addressesFromStorage = addressService.findAll();
        Set<Address> existingContacts = new HashSet<>(addressesFromStorage.getContent());

        for (Address address : addresses) {
            boolean exists = existingContacts.stream().anyMatch(existing ->
                    Objects.equals(existing.getFullAddress(), address.getFullAddress()));

            if (!exists) {
                addressService.save(address);
                log.info("Адрес: {} успешно сохранён", address);
            }
            else {
                address.getPersons().add(person);
                log.info("Гражданин добавлен в List<Persons> адреса: {}.", address);

            }
        }
    }

    private void savePersonsData(Person person){
        log.info("Сохранение доп. информации пользователя: {}", person);
        saveIdentityDocument(person);
        saveContact(person);
        saveAddress(person);
        log.info("Сохранение доп. информации пользователя: {} успешно завершено.", person);
    }

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
}
