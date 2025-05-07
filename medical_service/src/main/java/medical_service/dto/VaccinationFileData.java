package medical_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class VaccinationFileData {
    private String fullName;
    private String passport;
    private LocalDate vaccinationDate;
    private String vaccineName;
    private String pointCertificate;
    private String pointName;
    private String pointAddress;
}
