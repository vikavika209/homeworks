package person.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import person.data.entity.Contact;
import person.data.entity.IdentityDocument;
import person.data.entity.Person;
import person.data.repository.IdentityDocumentRepository;
import java.util.List;

@Service
@Slf4j
public class IdentityDocumentService {
    private final IdentityDocumentRepository identityDocumentRepository;

    @Autowired
    public IdentityDocumentService(IdentityDocumentRepository identityDocumentRepository) {
        this.identityDocumentRepository = identityDocumentRepository;
    }

    public IdentityDocument save(IdentityDocument identityDocument) {
        try {
            IdentityDocument savedIdentityDocument = identityDocumentRepository.save(identityDocument);
            log.info("Сохранен новый документ: {}", identityDocument);
            return savedIdentityDocument;
        }catch (Exception e) {
            log.error("Ошибка: {} при сохранении документа: {}", e.getMessage(), identityDocument);
            throw new RuntimeException("Ошибка при сохранении документа: " + identityDocument);
        }
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
        IdentityDocument foundIdentityDocument = findById(identityDocumentId);
        if (foundIdentityDocument != null) {
            log.info("Документ не найден: {}", identityDocument);
            foundIdentityDocument = save(identityDocument);
            log.info("Документ создан: {}", identityDocument);
            return foundIdentityDocument;
        }
        else {
            log.info("Документ: {} обновляется", identityDocument);
            IdentityDocument updatedIdentityDocument = identityDocumentRepository.updateById(identityDocument);
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
        IdentityDocument identityDocument = identityDocumentRepository.findById(id).orElse(null);
        if (identityDocument == null) {
            log.info("Документ для удаления с id: {} не найден.", id);
        }
        else {
            log.info("Удаление документа с id: {}", id);
        }
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
}
