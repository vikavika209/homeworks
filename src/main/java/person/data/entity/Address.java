package person.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue
    private int id;
    private String fullAddress;
    private String region;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "persons_id")
    private List<Person> persons;
}
