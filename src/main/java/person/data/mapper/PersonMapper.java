package person.data.mapper;

import org.mapstruct.Mapper;
import person.data.dto.PersonDTO;
import person.data.entity.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    Person toEntity(PersonDTO personDTO);
    PersonDTO toDTO(Person person);
}
