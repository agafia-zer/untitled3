package org.example;

import java.util.Date;

public class Client {
    private int ClientId;
    private String fullName;
    private Date birthDate;
    private String email;

    // Обновленный конструктор с параметром email
    public Client(int id, String fullName, Date birthDate, String email) {
        this.ClientId = ClientId;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
    }

    // Геттеры и сеттеры
    public int getId() {
        return ClientId;
    }

    public String getFullName() {
        return fullName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getEmail() { // Геттер для email
        return email;
    }

    public void setEmail(String email) { // Сеттер для email
        this.email = email;
    }
}
