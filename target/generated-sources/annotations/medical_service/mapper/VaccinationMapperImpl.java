package medical_service.mapper;

import javax.annotation.processing.Generated;
import medical_service.dto.VaccinationDTO;
import medical_service.entity.Vaccination;
import medical_service.entity.VaccinationPoint;
import medical_service.entity.Vaccine;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-06T13:02:05+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class VaccinationMapperImpl implements VaccinationMapper {

    @Override
    public VaccinationDTO toDto(Vaccination vaccination) {
        if ( vaccination == null ) {
            return null;
        }

        VaccinationDTO vaccinationDTO = new VaccinationDTO();

        vaccinationDTO.setVaccineName( vaccinationVaccineName( vaccination ) );
        vaccinationDTO.setVaccinationPointName( vaccinationVaccinationPointName( vaccination ) );
        vaccinationDTO.setId( vaccination.getId() );
        vaccinationDTO.setVaccinationDate( vaccination.getVaccinationDate() );
        vaccinationDTO.setPatientFullName( vaccination.getPatientFullName() );
        vaccinationDTO.setIdentityDocument( vaccination.getIdentityDocument() );

        return vaccinationDTO;
    }

    @Override
    public Vaccination toEntity(VaccinationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Vaccination vaccination = new Vaccination();

        vaccination.setId( dto.getId() );
        vaccination.setVaccinationDate( dto.getVaccinationDate() );
        vaccination.setPatientFullName( dto.getPatientFullName() );
        vaccination.setIdentityDocument( dto.getIdentityDocument() );

        return vaccination;
    }

    private String vaccinationVaccineName(Vaccination vaccination) {
        if ( vaccination == null ) {
            return null;
        }
        Vaccine vaccine = vaccination.getVaccine();
        if ( vaccine == null ) {
            return null;
        }
        String name = vaccine.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String vaccinationVaccinationPointName(Vaccination vaccination) {
        if ( vaccination == null ) {
            return null;
        }
        VaccinationPoint vaccinationPoint = vaccination.getVaccinationPoint();
        if ( vaccinationPoint == null ) {
            return null;
        }
        String name = vaccinationPoint.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
