package person.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import person.data.dto.IdentityDocumentDTO;
import person.data.entity.IdentityDocument;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IdentityDocumentMapper {
    IdentityDocumentDTO toDTO(IdentityDocument document);
    IdentityDocument toEntity(IdentityDocumentDTO documentDTO);

    List<IdentityDocumentDTO> toDTOList(List<IdentityDocument> documents);
    List<IdentityDocument> toEntityList(List<IdentityDocumentDTO> documentsDTO);
}