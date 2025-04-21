package person.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import person.data.entity.IdentityDocument;
import person.data.entity.Person;

import java.util.List;

public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Integer> {
    Page<IdentityDocument> findAll(Pageable pageable);
    List<IdentityDocument> findByPerson(Person person);
    IdentityDocument updateById(IdentityDocument identityDocument);
}
