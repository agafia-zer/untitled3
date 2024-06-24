package org.example;

import org.example.Client;

import java.util.Date;

public class BankCard {
    private Client client;
    private String cardNumber;
    private Date issueDate;
    private Date expiryDate;


    public BankCard(Client client, String cardNumber, Date issueDate, Date expiryDate) {
        this.client = client;
        this.cardNumber = cardNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    // Геттеры и сеттеры
    public Client getClient() {
        return client;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
}
