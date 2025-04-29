package person.data.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import person.data.entity.Address;
import person.data.entity.Contact;
import person.data.entity.IdentityDocument;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonDTO {
    private int id;
    @NotBlank(message = "Необходимо указать имя.")
    private String fullName;
    @NotBlank(message = "Необходимо указать паспортные данные.")
    @Size(min = 6, max = 20, message = "Паспортные данные должны содержать от 6 до 20 цифр.")
    @Pattern(regexp = "\\d+", message = "Паспортные данные должны состоять из цифр.")
    private String passportData;
    @Valid
    private List<IdentityDocumentDTO> documents = new ArrayList<>();
    @Size(min = 1, message = "Список контактов не может быть пустым.")
    @Valid
    private List<ContactDTO> contacts = new ArrayList<>();
    @Size(min = 1, message = "Список адресов не может быть пустым.")
    @Valid
    private List<AddressDTO> addresses =  new ArrayList<>();
}
