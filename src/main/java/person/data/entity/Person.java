package person.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue
    private int id;
    private String fullName;
    private String passportData;
    @OneToMany(mappedBy = "person")
    private List<IdentityDocument> documents;
    @OneToMany(mappedBy = "person")
    private List<Contact> contacts;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "person_address",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<Address> addresses;
}
