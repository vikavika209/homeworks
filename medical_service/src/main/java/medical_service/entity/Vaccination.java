package medical_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccination")
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Отсутствует дата")
    private LocalDate vaccinationDate;

    @NotNull(message = "Не указано имя вакцинируемого")
    @Pattern(regexp = "^[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+$", message = "ФИО должно быть в формате: 'Фамилия Имя Отчество'")
    private String patientFullName;

    @NotNull(message = "Не указан номер паспорта")
    @Size(max = 50)
    private String identityDocument;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    @NotNull(message = "Не указана вакцина")
    private Vaccine vaccine;

    @ManyToOne
    @JoinColumn(name = "vaccination_point_certificate")
    @NotNull(message = "Не указан пункт вакцинации")
    private VaccinationPoint vaccinationPoint;

    @Column(nullable = false)
    private boolean isSentToKafka = false;

}
