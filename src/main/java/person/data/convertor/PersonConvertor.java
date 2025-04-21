package person.data.convertor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import person.data.dto.PersonDTO;
import person.data.entity.Person;

@Component
public class PersonConvertor {
    private final ModelMapper modelMapper;

    public PersonConvertor() {
        this.modelMapper = new ModelMapper();
    }

    public Person personFromDTO(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    public PersonDTO personToDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
