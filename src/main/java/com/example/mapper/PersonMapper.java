package com.example.mapper;

import com.example.dto.Person;
import com.example.dto.PersonDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PersonMapper {
    PersonDTO toDto(Person person);
}
