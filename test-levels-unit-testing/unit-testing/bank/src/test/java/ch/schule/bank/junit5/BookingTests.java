package ch.schule.bank.junit5;

import ch.schule.Booking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die Klasse Booking.
 *
 * @author Nicola Wehrli
 * @version 1.1
 */
public class BookingTests {

    private final PrintStream originalOut = System.out;

    @AfterEach
    public void restoreStdout() {
        System.setOut(originalOut);
    }

    /**
     * Tests für die Erzeugung von Buchungen.
     */
    @Test
    public void testInitialization() {
        Booking booking = new Booking(42, 150000);

        assertEquals(42, booking.getDate());
        assertEquals(150000, booking.getAmount());

        // auch negative Beträge (Abhebungen) sind als Buchung gültig
        Booking withdrawal = new Booking(43, -50000);
        assertEquals(-50000, withdrawal.getAmount());
    }

    /**
     * Experimente mit print().
     */
    @Test
    public void testPrint() {
        // Banktag 0 = 01.01.1970, 100000 Millirappen = 1.00
        Booking booking = new Booking(0, 100000);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        booking.print(0);
        String output = out.toString();

        assertTrue(output.contains("01.01.1970"));
        assertTrue(output.contains("1.00"), "Betrag muss formatiert erscheinen");
    }
}
