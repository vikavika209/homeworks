package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Person {
    private int id;
    private String fullName;
    private String passportData;
    private List<IdentityDocumentDTO> documents = new ArrayList<>();
    private List<ContactDTO> contacts = new ArrayList<>();
    private List<AddressDTO> addresses =  new ArrayList<>();
}
