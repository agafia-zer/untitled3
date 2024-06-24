package main.java.org.examples;

import static org.junit.jupiter.api.Assertions.*;

import org.example.BankCard;
import org.example.Client;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


class BankCardTest {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final Date issueDate = dateFormat.parse("01.01.2020");
    private final Date expiryDate = dateFormat.parse("01.01.2025");
    private final Client client = new Client(1, "Иван Иванов", dateFormat.parse("01.01.1990"), "ivan@example.com");
    private final BankCard bankCard = new BankCard(client, "1234567890123456", issueDate, expiryDate);

    public BankCardTest() throws ParseException {
    }

    @Test
    void getClient() {
        assertEquals(client, bankCard.getClient());
    }

    @Test
    void getCardNumber() {
        assertEquals("1234567890123456", bankCard.getCardNumber());
    }

    @Test
    void getIssueDate() {
        assertEquals(issueDate, bankCard.getIssueDate());
    }

    @Test
    void getExpiryDate() {
        assertEquals(expiryDate, bankCard.getExpiryDate());
    }


}
