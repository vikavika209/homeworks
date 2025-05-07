package com.example.utils;

import com.example.repository.PersonRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    private final PersonRepository personRepository;

    public DatabaseCleaner(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PreDestroy
    public void clearDatabase() {
        personRepository.deleteAll();
        System.out.println("База данных очищена перед завершением приложения.");
    }
}

