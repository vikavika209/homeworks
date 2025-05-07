package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Необходимо указать номер документа.")
    @Size(min = 6, max = 20, message = "Номер документа должен содержать от 6 до 20 цифр.")
    @Pattern(regexp = "\\d+", message = "Номер документа должен состоять из цифр.")
    private String number;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persons_id")
    private Person person;

    public IdentityDocument(String name, String number, Person person) {
        this.name = name;
        this.number = number;
        this.person = person;
    }
}
