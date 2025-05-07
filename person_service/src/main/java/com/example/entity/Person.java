package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
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
    @NotBlank(message = "Необходимо указать имя.")
    private String fullName;
    @NotBlank(message = "Необходимо указать паспортные данные.")
    @Size(min = 6, max = 20, message = "Паспортные данные должны содержать от 6 до 20 цифр.")
    @Pattern(regexp = "\\d+", message = "Паспортные данные должны состоять из цифр.")
    private String passportData;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IdentityDocument> documents = new ArrayList<>();
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "person_address",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<Address> addresses =  new ArrayList<>();
}
