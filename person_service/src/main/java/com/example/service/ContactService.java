package com.example.service;

import com.example.entity.Contact;
import com.example.entity.Person;
import com.example.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional
    public Contact save(Contact contact) {
            Contact savedContact = contactRepository.save(contact);
            log.info("Сохранен новый контакт: {}", contact);
            return savedContact;
    }

    @Transactional
    public Contact update(Contact contact) {
        int contactId = contact.getId();
        Contact foundContact = contactRepository.findByIdForUpdate(contactId).orElse(null);
        if (foundContact == null) {
            log.info("Контакт не найден: {}", contact);
            foundContact = contactRepository.save(contact);
            log.info("Контакт создан: {}", contact);
            return foundContact;
        }
        else {
            log.info("Контакт: {} обновляется", contact);
            Contact updatedContact = contactRepository.save(contact);
            log.info("Контакт обновлён: ", updatedContact);
            return updatedContact;
        }
    }

    public Page<Contact> findAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Contact> contacts = contactRepository.findAll(pageable);
        log.info("Получен список всех контактов: {}", contacts.getTotalElements());
        return contacts;
    }

    public Contact findById(int id) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact == null) {
            log.info("Контакт с id: {} не найден >>> возврат null.", id);
        }
        else {
            log.info("Найден контакт с id: {}.", id);
        }
        return contact;
    }

    public void delete(int id) {
        contactRepository.deleteById(id);
    }

    public List<Contact> findByPerson(Person person) {
        List<Contact> contacts = contactRepository.findByPerson(person);

        if (contacts.isEmpty()) {
            log.info("Контакты для гражданина с id: {} не найдены.", person.getId());
        }
        else {
            log.info("Контакты в кол-ве: {} для гражданина с id: {} найдены.", contacts.size(), person.getId());
        }
        return contacts;
    }

    public Set<String> getAllPhoneNumbers(){
        return new HashSet<>(contactRepository.findAllContacts());
    }
}
