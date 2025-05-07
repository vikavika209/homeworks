package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VaccinationDTO {
    private Long id;
    private LocalDate vaccinationDate;
    private String patientFullName;
    private String identityDocument;
    private String vaccineName;
    private String vaccinationPointName;
}
