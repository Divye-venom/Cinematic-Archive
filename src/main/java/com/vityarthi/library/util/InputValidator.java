package com.vityarthi.library.util;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utility helper providing robust input parsing and terminal buffer clearance.
 */
public final class InputValidator {

    private InputValidator() {
        // Utility class
    }

    public static int readInt(Scanner scanner, String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
                return value;
            } catch (InputMismatchException e) {
                System.out.println(errorMessage);
                scanner.nextLine(); // Discard invalid token
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = scanner.nextDouble();
                scanner.nextLine(); // Clear buffer
                return value;
            } catch (InputMismatchException e) {
                System.out.println(errorMessage);
                scanner.nextLine(); // Discard invalid token
            }
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println(errorMessage);
        }
    }
}
