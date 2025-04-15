package com.example;

public class Phone {
    String number;
    Type type;

    public Phone(String number, Type type) {
        this.number = number;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "number='" + number + '\'' +
                ", type=" + type +
                '}';
    }

    public String getNumber() {
        return number;
    }

    public Type getType() {
        return type;
    }
}
