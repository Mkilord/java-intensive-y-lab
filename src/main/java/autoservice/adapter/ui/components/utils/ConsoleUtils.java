package autoservice.adapter.ui.components.utils;

import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Utility class for console input operations and validation.
 */
public class ConsoleUtils {
    /**
     * Predicate that checks if a string is not blank.
     * Prints an error message if the string is blank.
     */
    public static final Predicate<String> NOT_BLANK_STR = s -> {
        if (s.isBlank()) {
            System.out.println("It's empty input!");
            return false;
        }
        return true;
    };
    /**
     * Predicate that checks if a string is not too short (length > 2).
     * Prints an error message if the string is too short.
     */
    public static final Predicate<String> NOT_SMALL_SIZE = s -> {
        if (!s.isEmpty() && s.length() <= 2) {
            System.out.println("Very short input!");
            return false;
        }
        return true;
    };
    /**
     * Predicate that checks if a string has exactly 11 characters.
     * Prints an error message if the string does not have 11 characters.
     */
    public static final Predicate<String> SIZE_11 = s -> {
        if (s.length() != 11) {
            System.out.println("The phone number is not entered!");
            return false;
        }
        return true;
    };

    /**
     * Checks if the provided string can be parsed into an integer.
     *
     * @param s the string to check
     * @return the parsed integer, or -1 if parsing fails
     */
    public static int checkInt(String s) {
        if (s.isBlank()) return -1;
        int outInt;
        try {
            outInt = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
        return outInt;
    }

    /**
     * Checks if the given string can be parsed into a long value.
     * If the string is blank or cannot be parsed as a long, returns -1.
     *
     * @param s the string to be checked and parsed
     * @return the parsed long value if the string is not blank and is a valid long; otherwise, returns -1
     */
    public static long checkLong(String s) {
        if (s.isBlank()) return -1L;
        long outLong;
        try {
            outLong = Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1L;
        }
        return outLong;
    }

    /**
     * Checks if the provided integer is valid (not equal to -1).
     *
     * @param n the integer to check
     * @return true if the integer is valid, false otherwise
     */
    public static boolean isValidInt(int n) {
        return n != -1;
    }

    /**
     * Checks if the provided long value is valid (not equal to -1).
     *
     * @param n the long value to check
     * @return true if the long value is valid, false otherwise
     */
    public static boolean isValidLong(Long n) {
        return n != -1L;
    }

    public static int readInt(Scanner in, String errorMsg) {
        int value;
        boolean isValid;
        do {
            value = checkInt(in.nextLine());
            isValid = isValidInt(value);
            if (!isValid) System.out.println(errorMsg);
        } while (!isValid);
        return value;
    }

    public static int readInt(Scanner in, Predicate<Integer> predicate, String errorMsg) {
        int value;
        boolean isValid;
        do {
            value = checkInt(in.nextLine());
            isValid = isValidInt(value);
            if (!isValid) {
                System.out.println(errorMsg);
                continue;
            }
            isValid = predicate.test(value);
        } while (!isValid);
        return value;
    }

    /**
     * Reads a long value from the console, validating it with the provided predicate.
     *
     * @param in        the {@link Scanner} for console input
     * @param predicate the predicate to validate the long value
     * @return the validated long value
     */
    public static Long readLong(Scanner in, Predicate<Long> predicate) {
        long value;
        boolean isValid;
        do {
            value = checkLong(in.nextLine());
            isValid = isValidLong(value);
            if (!isValid) System.out.println("Invalid input! Input number!");
            isValid = predicate.test(value);
        } while (!isValid);
        return value;
    }

    /**
     * Reads a long value from the console.
     *
     * @param in the {@link Scanner} for console input
     * @return the read long value
     */
    public static long readLong(Scanner in) {
        long value;
        boolean isValid;
        do {
            value = checkLong(in.nextLine());
            isValid = isValidLong(value);
            if (!isValid) System.out.println("Invalid input! Input number!");
        } while (!isValid);
        return value;
    }

    /**
     * Reads an integer from the console.
     *
     * @param in the {@link Scanner} for console input
     * @return the read integer
     */
    public static int readInt(Scanner in) {
        int value;
        boolean isValid;
        do {
            value = checkInt(in.nextLine());
            isValid = isValidInt(value);
            if (!isValid) System.out.println("Invalid input! Input number!");
        } while (!isValid);
        return value;
    }

    public static int readIntOfRange(Scanner in, int start, int end, String errorMsg) {
        return readInt(in, n -> {
            boolean isOk = n >= start && n <= end;
            if (!isOk) {
                System.out.println(errorMsg);
            }
            return isOk;
        }, errorMsg);
    }

    /**
     * Reads a string from the console, validating it with the provided predicate.
     *
     * @param in        the {@link Scanner} for console input
     * @param predicate the predicate to validate the string
     * @return the validated string
     */
    public static String readStr(Scanner in, Predicate<String> predicate) {
        String value;
        do {
            value = in.nextLine();
        } while (!predicate.test(value));
        return value;
    }
}
