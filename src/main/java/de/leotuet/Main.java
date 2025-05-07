package de.leotuet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    static final String[] PROGRAM_ACTIONS = new String[] {
            "CSV Import",
            "Manueller Import",
            "Generiere Gruppen",
            "Gruppen anzeigen",
            "Verfügbarkeit ändern",
            "Beenden"
    };

    public static void main(String[] args) {
        boolean running = true;
        String url = "jdbc:mysql://127.0.0.1:3306/knowledge-xchange";
        String user = "root";
        String password = "admin";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            DatabaseCreator.createTables(conn);
            while (running) {
                int choice = CommandLineInterface.getChoice(PROGRAM_ACTIONS);
                Importer importer = new Importer(conn);
                Matcher matcher = new Matcher(conn);
                Displayer displayer = new Displayer(conn);

                switch (choice) {
                    case 1 -> importer.startCSVImport();
                    case 2 -> importer.startManualImport();
                    case 3 -> matcher.match();
                    case 4 -> displayer.displayGroups();
                    case 5 -> importer.changeAvailability();
                    case 6 -> {
                        System.out.println("Beende Programm...");
                        running = false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

}
