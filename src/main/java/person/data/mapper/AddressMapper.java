package person.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import person.data.dto.AddressDTO;
import person.data.entity.Address;

import java.util.List;

@Mapper
public interface AddressMapper {
    AddressDTO toDTO(Address address);
    Address toEntity(AddressDTO addressDTO);

    List<AddressDTO> toDTOList(List<Address> addresses);
    List<Address> toEntityList(List<AddressDTO> addressesDTO);
}

