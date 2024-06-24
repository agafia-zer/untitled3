package org.example;

import java.util.HashMap;
import java.util.Scanner;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.example.ClientManager.checkAndCancelExpiredCards;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection = DatabaseManager.getConnection();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static HashMap<Integer, Client> clientCache = new HashMap<>();
    private static HashMap<Integer, BankCard> cardsCache = new HashMap<>();

    public static void main(String[] args) {
        // Инициализация планировщика для автоматической отмены просроченных карт
        initializeCardCancellationScheduler();
        runUserInteractionSession();
    }

    private static void initializeCardCancellationScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            checkAndCancelExpiredCards(connection, cardsCache);
        }, 0, 1, TimeUnit.DAYS);
    }

    private static void runUserInteractionSession() {
        while (true) {
            System.out.println("Добавить нового клиента? (Да/Нет)");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("Да")) {
                ClientManager.addNewClient(scanner, connection, dateFormat, clientCache);

                // После добавления клиента, спрашиваем о создании карты
                System.out.println("Создать карту для клиента ? (Да/Нет)");
                String cardResponse = scanner.nextLine();
                if (cardResponse.equalsIgnoreCase("Да")) {
                    ClientManager.createCard(scanner, connection, clientCache, cardsCache);
                }
            } else if (response.equalsIgnoreCase("Нет")) {
                System.out.println("Добавить карту для существующего клиента? (Да/Нет)");
                String cardResponse = scanner.nextLine();
                if (cardResponse.equalsIgnoreCase("Да")) {
                    ClientManager.createCard(scanner, connection, clientCache, cardsCache);
                } else if (cardResponse.equalsIgnoreCase("Нет")) {
                    System.out.println("Удалить карту существующего клиента? (Да/Нет)");
                    String deleteResponse = scanner.nextLine();
                    if (deleteResponse.equalsIgnoreCase("Да")) {
                        ClientManager.cancelCard(scanner, connection, cardsCache);
                    } else if (deleteResponse.equalsIgnoreCase("Нет")) {
                        break; // Выход из цикла, если пользователь не хочет удалять карту
                    }
                }
            } else {
                System.out.println("Некорректный ввод! Введите 'Да' или 'Нет'");
            }
        }
    }
}
