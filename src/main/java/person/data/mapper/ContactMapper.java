package person.data.mapper;

import org.mapstruct.Mapper;
import person.data.dto.ContactDTO;
import person.data.entity.Contact;

import java.util.List;

@Mapper
public interface ContactMapper {
    ContactDTO toDTO(Contact contact);
    Contact toEntity(ContactDTO contactDTO);

    List<ContactDTO> toDTOList(List<Contact> contacts);
    List<Contact> toEntityList(List<ContactDTO> contactsDTO);
}