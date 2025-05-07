package com.example.service;

import com.example.entity.IdentityDocument;
import com.example.entity.Person;
import com.example.repository.IdentityDocumentRepository;
import lombok.extern.slf4j.Slf4j;
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
public class IdentityDocumentService {
    private final IdentityDocumentRepository identityDocumentRepository;

    public IdentityDocumentService(IdentityDocumentRepository identityDocumentRepository) {
        this.identityDocumentRepository = identityDocumentRepository;
    }

    @Transactional
    public IdentityDocument save(IdentityDocument identityDocument) {
            IdentityDocument savedIdentityDocument = identityDocumentRepository.save(identityDocument);
            log.info("Сохранен новый документ: {}", identityDocument);
            return savedIdentityDocument;
    }

    public Page<IdentityDocument> findAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<IdentityDocument> identityDocuments = identityDocumentRepository.findAll(pageable);
        log.info("Получен список всех документов: {}", identityDocuments.getTotalElements());
        return identityDocuments;
    }

    @Transactional
    public IdentityDocument update(IdentityDocument identityDocument) {
        int identityDocumentId = identityDocument.getId();
        IdentityDocument foundIdentityDocument = identityDocumentRepository.findByIdForUpdate(identityDocumentId).orElse(null);

        if (foundIdentityDocument == null) {
            log.info("Документ не найден: {}", identityDocument);
            foundIdentityDocument = identityDocumentRepository.save(identityDocument);
            log.info("Документ создан: {}", identityDocument);
            return foundIdentityDocument;
        }
        else {
            log.info("Документ: {} обновляется", identityDocument);
            IdentityDocument updatedIdentityDocument = identityDocumentRepository.save(identityDocument);
            log.info("Документ обновлён: ", updatedIdentityDocument);
            return updatedIdentityDocument;
        }
    }

    public IdentityDocument findById(int id) {
        IdentityDocument identityDocument = identityDocumentRepository.findById(id).orElse(null);
        if (identityDocument == null) {
            log.info("Документ с id: {} не найден >>> возврат null.", id);
        }
        else {
            log.info("Найден документ с id: {}.", id);
        }
        return identityDocument;
    }

    public void delete(int id) {
        identityDocumentRepository.deleteById(id);
    }

    public List<IdentityDocument> findByPerson(Person person) {
        List<IdentityDocument> identityDocuments = identityDocumentRepository.findByPerson(person);
        if (identityDocuments.isEmpty()) {
            log.info("Документы для гражданина с id: {} не найдены.", person.getId());
        }
        else {
            log.info("Документы в кол-ве: {} для гражданина с id: {} найдены.", identityDocuments.size(), person.getId());
        }
        return identityDocuments;
    }

    public Set<String> getAllDocumentNumbers(){
        return new HashSet<>(identityDocumentRepository.findAllDocumentNumbers());
    }

    public boolean existsByDocumentNumber (String documentNumber) {
        return identityDocumentRepository.existsByNumber(documentNumber);
    }
}
