package de.leotuet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    static final String[] PROGRAM_ACTIONS = new String[] {
            "CSV Import",
            "Manueller Import",
            "Generiere Gruppen",
            "Beenden"
    };

    public static void main(String[] args) {
        boolean running = true;
        String url = "jdbc:mysql://127.0.0.1:3306/knowledge-xchange";
        String user = "root";
        String password = "admin";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            while (running) {
                int choice = CommandLineInterface.getChoice(PROGRAM_ACTIONS);
                Importer importer = new Importer(conn);
                switch (choice) {
                    case 1 -> importer.importCSV();
                    case 2 -> importer.importManual();
                    case 3 -> System.out.println("Generiere Gruppen");
                    case 4 -> {
                        System.out.println("Beende Programm");
                        running = false;
                    }
                    default -> System.out.println("Ungültige Auswahl");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

}
