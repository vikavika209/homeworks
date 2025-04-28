package person.data.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import person.data.dto.AddressDTO;
import person.data.dto.ContactDTO;
import person.data.dto.PersonDTO;
import person.data.service.PersonService;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(PersonService personService) {
        return args -> {
            PersonDTO ivanov = new PersonDTO();
            ivanov.setFullName("Иванов Иван Иванович");
            ivanov.setPassportData("1234567890");
            ivanov.setContacts(generateRandomContacts());
            ivanov.setAddresses(generateRandomAddresses());

            personService.save(ivanov);

            PersonDTO petrov = new PersonDTO();
            petrov.setFullName("Петров Петр Петрович");
            petrov.setPassportData("0987654321");
            petrov.setContacts(generateRandomContacts());
            petrov.setAddresses(generateRandomAddresses());

            personService.save(petrov);

            System.out.println("Данные успешно добавлены в БД!");

            System.out.println(personService.verifyIdentity("Иванов Иван Иванович", "1234567890"));
        };
    }

    private List<ContactDTO> generateRandomContacts() {
        ContactDTO contact = new ContactDTO();
        contact.setType("mobile");
        contact.setNumber(generateRandomPhoneNumber());
        return List.of(contact);
    }

    private List<AddressDTO> generateRandomAddresses() {
        AddressDTO address = new AddressDTO();
        address.setFullAddress("г. " + randomCity() + ", ул. Ленина, д. " + (1 + (int)(Math.random() * 100)));
        address.setRegion(randomCity());
        return List.of(address);
    }

    private String generateRandomPhoneNumber() {
        return "79" + (int)(100000000 + Math.random() * 900000000);
    }

    private String randomCity() {
        String[] cities = {"Москва", "Санкт-Петербург", "Казань", "Новосибирск", "Екатеринбург"};
        int idx = (int)(Math.random() * cities.length);
        return cities[idx];
    }
}
