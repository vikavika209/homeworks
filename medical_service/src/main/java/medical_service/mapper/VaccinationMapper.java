package medical_service.mapper;

import medical_service.dto.VaccinationDTO;
import medical_service.entity.Vaccination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VaccinationMapper {

    @Mapping(target = "vaccineName", source = "vaccine.name")
    @Mapping(target = "vaccinationPointName", source = "vaccinationPoint.name")
    VaccinationDTO toDto(Vaccination vaccination);

    Vaccination toEntity(VaccinationDTO dto);
}
