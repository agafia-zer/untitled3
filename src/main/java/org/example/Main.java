package org.example;

import java.util.HashMap;
import java.util.Scanner;
import java.sql.Connection;
import java.text.SimpleDateFormat;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = DatabaseManager.getConnection();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        HashMap<Integer, Client> clientCache = new HashMap<>();
        HashMap<Integer, BankCard> cardsCache = new HashMap<>();

        while (true) {
            System.out.println("Добавить нового клиента? (Да/Нет)");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("Да")) {
                ClientManager.addNewClient(scanner, connection, dateFormat, clientCache);

                // После добавления клиента, спрашиваем о создании карты
                System.out.println("Создать карту для клиента? (Да/Нет)");
                String cardResponse = scanner.nextLine();
                if (cardResponse.equalsIgnoreCase("Да")) {
                    ClientManager.createCard(scanner, connection, clientCache, cardsCache);
                }
            }  else if (response.equalsIgnoreCase("Нет")) {
                System.out.println("Добавить карту для существующего клиента? (Да/Нет)");
                String deleteResponse = scanner.nextLine();
                if (deleteResponse.equalsIgnoreCase("Да")) {

                    ClientManager.createCard(scanner, connection, clientCache, cardsCache);
                }
                break;
            }else if (response.equalsIgnoreCase("Нет")) {
                System.out.println("Удалить карту существующего клиента? (Да/Нет)");
                String deleteResponse = scanner.nextLine();
                if (deleteResponse.equalsIgnoreCase("Да")) {
                    ClientManager.cancelCard(scanner, connection, cardsCache);
                }
                break;
            } else {
                System.out.println("Некорректный ввод! Введите 'Да' или 'Нет'");
            }
        }



        scanner.close();
    }
}
