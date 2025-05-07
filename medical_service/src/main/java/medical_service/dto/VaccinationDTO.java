package medical_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import medical_service.entity.Vaccination;

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

    public VaccinationDTO(Vaccination v) {
        this.id = v.getId();
        this.vaccinationDate = v.getVaccinationDate();
        this.patientFullName = v.getPatientFullName();
        this.identityDocument = v.getIdentityDocument();
        this.vaccineName = v.getVaccine() != null ? v.getVaccine().getName() : null;
        this.vaccinationPointName = v.getVaccinationPoint() != null ? v.getVaccinationPoint().getName() : null;
    }
}
