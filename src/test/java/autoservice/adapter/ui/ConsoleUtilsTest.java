package autoservice.adapter.ui;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleUtilsTest {

    @Test
    void testNotBlankStr() {
        String input = "valid input";
        boolean result = ConsoleUtils.NOT_BLANK_STR.test(input);
        assertTrue(result);

        input = "  ";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        result = ConsoleUtils.NOT_BLANK_STR.test(input);
        assertFalse(result);
        assertTrue(outContent.toString().contains("It's empty input!"));
    }

    @Test
    void testNotSmallSize() {
        String input = "valid input";
        boolean result = ConsoleUtils.NOT_SMALL_SIZE.test(input);
        assertTrue(result);

        input = "a";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        result = ConsoleUtils.NOT_SMALL_SIZE.test(input);
        assertFalse(result);
        assertTrue(outContent.toString().contains("Very short input!"));
    }

    @Test
    void testSize11() {
        String input = "12345678901";
        boolean result = ConsoleUtils.SIZE_11.test(input);
        assertTrue(result);

        input = "12345";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        result = ConsoleUtils.SIZE_11.test(input);
        assertFalse(result);
        assertTrue(outContent.toString().contains("The phone number is not entered!"));
    }

    @Test
    void testCheckInt() {
        assertEquals(123, ConsoleUtils.checkInt("123"));
        assertEquals(-1, ConsoleUtils.checkInt("abc"));
        assertEquals(-1, ConsoleUtils.checkInt(" "));
    }

    @Test
    void testIsValidInt() {
        assertTrue(ConsoleUtils.isValidInt(123));
        assertFalse(ConsoleUtils.isValidInt(-1));
    }

    @Test
    void testIsValidLong() {
        assertTrue(ConsoleUtils.isValidLong(123L));
        assertFalse(ConsoleUtils.isValidLong(-1L));
    }

    @Test
    void testReadInt() {
        String input = "456";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        int result = ConsoleUtils.readInt(scanner);
        assertEquals(456, result);
    }

    @Test
    void testReadIntWithPredicate() {
        String input = "123";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        int result = ConsoleUtils.readInt(scanner, n -> n > 0);
        assertEquals(123, result);
    }

    @Test
    void testReadLong() {
        String input = "12345678901";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        long result = ConsoleUtils.readLong(scanner);
        assertEquals(12345678901L, result);
    }

    @Test
    void testReadLongWithPredicate() {
        // Test case for valid input
        String input = "12345678901";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        Long result = ConsoleUtils.readLong(scanner, l -> l > 0);
        assertEquals(12345678901L, result);

        // Test case for invalid input with predicate
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        input = "abc\n0\n-1\n123\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scanner = new Scanner(System.in);
        result = ConsoleUtils.readLong(scanner, l -> l > 0);
        assertEquals(123L, result);
        assertTrue(outContent.toString().contains("Invalid input! Input number!"));
    }

    @Test
    void testReadStr() {
        String input = "test input";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        String result = ConsoleUtils.readStr(scanner, ConsoleUtils.NOT_BLANK_STR);
        assertEquals("test input", result);
    }

    @Test
    void testReadIntOfRange() {
        // Test case for valid input within range
        String input = "5";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        int result = ConsoleUtils.readIntOfRange(scanner, 1, 10);
        assertEquals(5, result);
    }
}
