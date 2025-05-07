package medical_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccination_point")
public class VaccinationPoint {
    @Id
    @Column(length = 50)
    private String certificateNumber;

    @NotNull(message = "Не указано название пункта вакцинации")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Не указан город пункта вакцинации")
    @Size(max = 50)
    private String city;

    @NotNull(message = "Не указан адрес пункта вакцинации")
    @Size(max = 200)
    private String address;

    @OneToMany(mappedBy = "vaccinationPoint", fetch = FetchType.LAZY)
    private List<Vaccination> vaccinations = new ArrayList<Vaccination>();
}
