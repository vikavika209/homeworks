package com.example;


import com.github.javafaker.Faker;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Client> clients = clients();

        System.out.println("Cуммарный возраст для имени Alice: " + sumForTheName(clients, "Alice"));
        System.out.println("Имена клиентов в порядке их упоминания в исходном массиве: " + clientsNames(clients));
        System.out.println("Содержит ли список хотя бы одного клиента, у которого возраст больше 25: " + containsClientWithAgeMoreThatTheNumber(25, clients));
        System.out.println("Содержит ли список хотя бы одного клиента, у которого возраст больше 100: " + containsClientWithAgeMoreThatTheNumber(100, clients));
        System.out.println("Map, где ключ - уникальный идентификатор, значение - имя: " + idNameMap(clients));
        System.out.println("Map, где ключ - возраст, значение - коллекция клиентов с таким возрастом: " + ageClientMap(clients));
        System.out.println("Телефоны всех клиентов: " + allPhonesNumber(clients));
        System.out.println("Cамый возрастной клиент, который пользуется стационарным телефоном: " + theOldClientWithOrdinaryPhone(clients));

    }

    public static void printList(List<Client> clients){
        for (int i = 0; i < clients.size(); i++) {
            System.out.println(i+1 + ". " + clients.get(i).name + " " + clients.get(i).age + " " + clients.get(i).phones);
        }
    }

    public static int sumForTheName (List<Client> clients, String name){
        return clients.stream()
                .filter(client -> client.getName().equalsIgnoreCase(name))
                .mapToInt(Client::getAge)
                .sum();
    }

    public static LinkedHashSet clientsNames (List<Client> clients){
        return clients.stream()
                .map(Client::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static boolean containsClientWithAgeMoreThatTheNumber (int number, List<Client> clients){
        return clients.stream()
                .mapToInt(Client::getAge)
                .anyMatch(age -> age > number);
    }

    public static Map<Integer, String> idNameMap (List<Client> clients){
        return clients.stream()
                .collect(Collectors.toMap(
                        Client::getId,
                        Client::getName,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    public static Map<Integer, Set<Client>> ageClientMap (List<Client> clients){
        return clients.stream()
                .collect(Collectors.groupingBy(Client::getAge, Collectors.toSet()));
    }

    public static String allPhonesNumber(List<Client> clients){
        return clients.stream()
                .filter(client -> client.getPhones() != null)
                .flatMap(client -> client.getPhones().stream())
                .map(Phone::getNumber)
                .collect(Collectors.joining(", "));
    }

    public static Client theOldClientWithOrdinaryPhone(List<Client> clients){
        return clients.stream()
                .filter(client ->
                        client.getPhones() != null &&
                        client.getPhones()
                                .stream()
                                .anyMatch(phone ->
                                        phone.type == Type.ORDINARY_PHONE))
                .max(Comparator.comparingInt(Client::getAge))
                .orElse(null);
    }

    public static List<Client> clients(){
        List<Client> clients = new ArrayList<>();

        clients.add(new Client(1, "Alice", 22, Arrays.asList(
                new Phone("123-456", Type.MOBILE_PHONE),
                new Phone("111-222", Type.ORDINARY_PHONE))));

        clients.add(new Client(2, "Alice", 30, Arrays.asList(
                new Phone("222-333", Type.MOBILE_PHONE))));

        clients.add(new Client(3, "Charlie", 22, Collections.emptyList()));

        clients.add(new Client(4, "Diana", 28, Arrays.asList(
                new Phone("333-444", Type.ORDINARY_PHONE))));

        clients.add(new Client(5, "Eve", 35, null)); // null список телефонов

        clients.add(new Client(6, "Frank", 27, Arrays.asList(
                new Phone("444-555", Type.MOBILE_PHONE),
                new Phone("555-666", Type.ORDINARY_PHONE))));

        clients.add(new Client(7, "Grace", 31, Arrays.asList(
                new Phone("666-777", Type.MOBILE_PHONE))));

        clients.add(new Client(8, "Hank", 26, new ArrayList<>())); // пустой список

        clients.add(new Client(9, "Ivy", 29, Arrays.asList(
                new Phone("777-888", Type.ORDINARY_PHONE))));

        clients.add(new Client(10, "Jack", 24, Arrays.asList(
                new Phone("888-999", Type.MOBILE_PHONE))));

        return clients;
    }

}