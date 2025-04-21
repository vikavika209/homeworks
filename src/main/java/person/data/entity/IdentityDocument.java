package person.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "identity_documents")
public class IdentityDocument {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String number;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persons_id", nullable = false)
    private Person person;
}
