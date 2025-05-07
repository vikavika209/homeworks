package com.example.mapper;

import com.example.dto.PersonDTO;
import com.example.entity.Person;
import org.mapstruct.Mapper;


@Mapper
public interface PersonMapper {

    Person toEntity(PersonDTO personDTO);
    PersonDTO toDTO(Person person);
}
