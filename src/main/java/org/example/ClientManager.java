package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;


public class ClientManager {

    // Метод для добавления нового клиента
    public static void addNewClient(Scanner scanner, Connection connection, SimpleDateFormat dateFormat, HashMap<Integer, Client> clientCache) {
        try {

            System.out.println("Введите ФИО клиента:");
            String fullName = scanner.nextLine();
            System.out.println("Введите дату рождения клиента (dd.MM.yyyy):");
            String birthDateString = scanner.nextLine();
            Date birthDate = dateFormat.parse(birthDateString);
            System.out.println("Введите email клиента:");
            String email = scanner.nextLine();


            // Проверка на существование клиента
            PreparedStatement checkStmt = connection.prepareStatement("SELECT ClientId FROM users WHERE FullName = ? AND DateOfBirth = ?");
            checkStmt.setString(1, fullName);
            checkStmt.setDate(2, new java.sql.Date(birthDate.getTime()));
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                int clientId = resultSet.getInt("ClientId");
                System.out.println("Клиент уже существует с ID: " + clientId);
                // Возвращаем существующего клиента из кэша
                Client client = clientCache.get(clientId);
                if (client == null) {
                    client = new Client(clientId, fullName, birthDate, email);
                    clientCache.put(clientId, client);
                }
            } else {
                // Добавление нового клиента
                PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO users (FullName, Email, DateOfBirth) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, fullName);
                insertStmt.setString(2, email);
                insertStmt.setDate(3, new java.sql.Date(birthDate.getTime()));
                insertStmt.executeUpdate();

                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newClientId = generatedKeys.getInt(1);
                    Client newClient = new Client(newClientId, email, birthDate, email);
                    clientCache.put(newClientId, newClient);
                    System.out.println("Новый клиент добавлен с ID: " + newClientId);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении клиента: " + e.getMessage());
        }
    }

    // Метод для создания карты
    public static void createCard(Scanner scanner, Connection connection, HashMap<Integer, Client> clientCache, HashMap<Integer, BankCard> cardsCache) {
        try {
            System.out.println("Введите ID клиента:");
            int clientId = scanner.nextInt();
            // Проверяем, существует ли клиент в базе данных users
            PreparedStatement checkClientStmt = connection.prepareStatement("SELECT ClientId FROM users WHERE ClientId = ?");
            checkClientStmt.setInt(1, clientId);
            ResultSet clientResultSet = checkClientStmt.executeQuery();
            if (!clientResultSet.next()) {
                System.out.println("Клиент с таким ID не найден.");
                return;
            }
            // Получаем данные клиента из кэша
            Client client = clientCache.get(clientId);

            // Генерация уникального номера карты
            String cardNumber = generateUniqueCardNumber(connection);

            System.out.println("Введите дату выдачи карты (dd.MM.yyyy):");
            String issueDateString = scanner.next();
            Date issueDate = new SimpleDateFormat("dd.MM.yyyy").parse(issueDateString);

            System.out.println("Введите дату окончания действия карты (dd.MM.yyyy):");
            String expirationDateString = scanner.next();
            Date expirationDate = new SimpleDateFormat("dd.MM.yyyy").parse(expirationDateString);

            // Создание новой карты
            PreparedStatement insertCardStmt = connection.prepareStatement("INSERT INTO cards (ClientId, CardNumber, OpeningDate, ExpirationDate) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertCardStmt.setInt(1, clientId);
            insertCardStmt.setString(2, cardNumber);
            insertCardStmt.setDate(3, new java.sql.Date(issueDate.getTime()));
            insertCardStmt.setDate(4, new java.sql.Date(expirationDate.getTime()));
            insertCardStmt.executeUpdate();

            ResultSet generatedKeys = insertCardStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newCardId = generatedKeys.getInt(1);
                BankCard newCard = new BankCard(client, cardNumber, issueDate, expirationDate);
                cardsCache.put(newCardId, newCard);
                // Измененная строка вывода
                System.out.println("Новая карта добавлена с ID: " + newCardId + ", номер карты: " + cardNumber);
            }

        } catch (Exception e) {
            System.out.println("Ошибка при создании карты: " + e.getMessage());
        }
    }

    private static String generateUniqueCardNumber(Connection connection) throws SQLException {
        String cardNumber;
        ResultSet cardResultSet;
        do {
            cardNumber = String.valueOf((int) (Math.random() * 100000000));
            PreparedStatement checkCardStmt = connection.prepareStatement("SELECT CardId FROM cards WHERE CardNumber = ?");
            checkCardStmt.setString(1, cardNumber);
            cardResultSet = checkCardStmt.executeQuery();
        } while (cardResultSet.next()); // Повторять, если номер уже существует
        return cardNumber;
    }


    public static void cancelCard(Scanner scanner, Connection connection, HashMap<Integer, BankCard> cardsCache) {
        try {
            System.out.println("Введите номер карты для аннулирования:");
            String cardNumber = scanner.next();
            scanner.nextLine(); // Очистка буфера сканера после чтения числа

            // Проверка существования карты
            PreparedStatement checkCardStmt = connection.prepareStatement("SELECT CardId, ClientId FROM cards WHERE CardNumber = ?");
            checkCardStmt.setString(1, cardNumber);
            ResultSet cardResultSet = checkCardStmt.executeQuery();
            if (!cardResultSet.next()) {
                System.out.println("Карта с таким номером не найдена.");
                return;
            }
            int cardId = cardResultSet.getInt("CardId");
            int clientId = cardResultSet.getInt("ClientId");

            // Аннулирование карты
            PreparedStatement cancelCardStmt = connection.prepareStatement("DELETE FROM cards WHERE CardId = ?");
            cancelCardStmt.setInt(1, cardId);
            int affectedRows = cancelCardStmt.executeUpdate();
            if (affectedRows > 0) {
                cardsCache.remove(cardId);
                System.out.println("Карта с номером " + cardNumber + " аннулирована.");

                // Запрос на отправку уведомления клиенту
                System.out.println("Отправить уведомление клиенту? (Да/Нет)");
                String response = scanner.nextLine();
                if ("Да".equalsIgnoreCase(response)) {
                    // Получение данных клиента
                    PreparedStatement getClientStmt = connection.prepareStatement("SELECT FullName, Email FROM users WHERE ClientId = ?");
                    getClientStmt.setInt(1, clientId);
                    ResultSet clientResultSet = getClientStmt.executeQuery();
                    if (clientResultSet.next()) {
                        String fullName = clientResultSet.getString("FullName");
                        String email = clientResultSet.getString("Email");

                        // Создание текстового файла с уведомлением
                        String notification = "Здравствуйте, " + fullName + ", ваша карта с номером: " + cardNumber + " была аннулирована.";
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter("notification.txt"))) {
                            writer.write(notification);
                            System.out.println("Уведомление было отправлено на: " + email);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при аннулировании карты: " + e.getMessage());
        }
    }
}
