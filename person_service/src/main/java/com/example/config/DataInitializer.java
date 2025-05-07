package com.example.config;

import com.example.entity.Address;
import com.example.entity.Contact;
import com.example.entity.Person;
import com.example.service.PersonService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(PersonService personService) {
        return args -> {
            Person ivanov = new Person();
            ivanov.setFullName("Иванов Иван Иванович");
            ivanov.setPassportData("1234567890");
            ivanov.setContacts(generateRandomContacts());
            ivanov.setAddresses(generateRandomAddresses());

            personService.save(ivanov);

            Person petrov = new Person();
            petrov.setFullName("Петров Петр Петрович");
            petrov.setPassportData("0987654321");
            petrov.setContacts(generateRandomContacts());
            petrov.setAddresses(generateRandomAddresses());

            personService.save(petrov);

            System.out.println("Данные успешно добавлены в БД!");

            System.out.println(personService.verifyIdentity("Иванов Иван Иванович", "1234567890"));
        };
    }

    private List<Contact> generateRandomContacts() {
        Contact contact = new Contact();
        contact.setType("mobile");
        contact.setNumber(generateRandomPhoneNumber());
        return List.of(contact);
    }

    private List<Address> generateRandomAddresses() {
        Address address = new Address();
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
