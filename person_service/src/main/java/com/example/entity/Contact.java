package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue
    private int id;
    @NotBlank(message = "Необходимо указать тип номера.")
    private String type;
    @Pattern(regexp = "\\d{10,15}", message = "Телефон должен содержать от 10 до 15 цифр")
    private String number;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persons_id")
    private Person person;
}
