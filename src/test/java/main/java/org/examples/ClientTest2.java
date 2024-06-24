package main.java.org.examples;
import org.example.Client;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

class ClientTest2 {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final Client client = new Client(1, "Иван Иванов", dateFormat.parse("01.01.1990"), "ivan@example.com");

    public ClientTest2() throws Exception {
    }



    @Test
    void getFullName() {
        assertEquals("Иван Иванов", client.getFullName());
    }

    @Test
    void getBirthDate() throws ParseException {
        assertEquals(dateFormat.parse("01.01.1990"), client.getBirthDate());
    }

    @Test
    void getEmail() {
        assertEquals("ivan@example.com", client.getEmail());
    }

    @Test
    void setEmail() {
        client.setEmail("new_email@example.com");
        assertEquals("new_email@example.com", client.getEmail());
    }
}
