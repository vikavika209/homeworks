package com.example;

import java.util.List;
import java.util.Random;

public class User {
    private Long id;
    private String name;
    private List<User> friends;

    public User(String name) {
        this.name = name;
        this.id = new Random().nextLong();
    }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<User> getFriends() {
        return friends;
    }
    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
