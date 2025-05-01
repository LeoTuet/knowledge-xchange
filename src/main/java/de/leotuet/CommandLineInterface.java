package de.leotuet;

import java.util.Scanner;

public class CommandLineInterface {

    public static String getString(String message) {
        String input;
        System.out.print(message + " ");
        try (Scanner sc = new Scanner(System.in)) {
            input = sc.nextLine();
            sc.close();
        }
        return input;
    }

    public static int getInt(String message) {
        int input;
        System.out.print(message + " ");
        try (Scanner sc = new Scanner(System.in)) {
            input = sc.nextInt();
            sc.close();
        }
        return input;
    }

    public static int getChoice(String[] options) {
        Integer choice = 0;
        boolean validChoice = false;
        while (!validChoice) {
            printChoices(options);
            choice = getInt("Auswahl:");
            if (choice < 1 || choice > options.length) {
                System.out
                        .println("Ungültige Auswahl. Bitte wählen Sie eine Zahl zwischen 1 und " + options.length);
            } else {
                validChoice = true;
            }
        }
        return choice;
    }

    private static void printChoices(String[] options) {
        System.out.println("Welche aktion möchten Sie ausführen?");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }
}
