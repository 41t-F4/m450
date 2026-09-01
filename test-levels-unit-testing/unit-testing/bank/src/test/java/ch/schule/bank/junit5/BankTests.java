package ch.schule.bank.junit5;

import ch.schule.Bank;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für die Klasse 'Bank'.
 *
 * @author Nicola Wehrli
 * @version 1.0
 */
public class BankTests {

    private Bank bank;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        bank = new Bank();
    }

    @AfterEach
    public void restoreStdout() {
        System.setOut(originalOut);
    }

    private ByteArrayOutputStream captureStdout() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        return buffer;
    }

    /**
     * Tests to create new Accounts.
     */
    @Test
    public void testCreate() {
        // Ids beginnen bei 1000 und zählen über alle Kontoarten hoch
        assertEquals("S-1000", bank.createSavingsAccount());
        assertEquals("Y-1001", bank.createPromoYouthSavingsAccount());
        assertEquals("P-1002", bank.createSalaryAccount(-50000));

        // positive Kreditlimite ist ungültig -> null
        assertNull(bank.createSalaryAccount(10000));

        // nach dem Fehlversuch geht die Nummerierung normal weiter
        assertEquals("S-1003", bank.createSavingsAccount());
    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    public void testDeposit() {
        String id = bank.createSavingsAccount();

        assertTrue(bank.deposit(id, 10, 5000));
        assertEquals(5000, bank.getBalance(id));

        // unbekanntes Konto
        assertFalse(bank.deposit("S-9999", 10, 5000));

        // ungültiger Betrag wird ans Konto delegiert und abgelehnt
        assertFalse(bank.deposit(id, 11, -1));
        assertEquals(5000, bank.getBalance(id));
    }

    /**
     * Testet das Abheben von einem Konto.
     */
    @Test
    public void testWithdraw() {
        String id = bank.createSavingsAccount();
        bank.deposit(id, 10, 5000);

        assertTrue(bank.withdraw(id, 11, 3000));
        assertEquals(2000, bank.getBalance(id));

        // unbekanntes Konto
        assertFalse(bank.withdraw("S-9999", 11, 100));

        // Sparkonto darf nicht überzogen werden
        assertFalse(bank.withdraw(id, 12, 99999));
        assertEquals(2000, bank.getBalance(id));
    }

    /**
     * Experimente mit print().
     */
    @Test
    public void testPrint() {
        String id = bank.createSavingsAccount();
        bank.deposit(id, 0, 100000);

        ByteArrayOutputStream out = captureStdout();
        bank.print(id);
        assertTrue(out.toString().contains("Kontoauszug 'S-1000'"));

        // unbekanntes Konto: keine Ausgabe, kein Absturz
        ByteArrayOutputStream out2 = captureStdout();
        bank.print("S-9999");
        assertEquals("", out2.toString());
    }

    /**
     * Experimente mit print(year, month).
     */
    @Test
    public void testMonthlyPrint() {
        String id = bank.createSavingsAccount();
        bank.deposit(id, 0, 100000);  // Januar 1970
        bank.deposit(id, 35, 50000);  // Februar 1970

        ByteArrayOutputStream out = captureStdout();
        bank.print(id, 1970, 1);
        String output = out.toString();

        assertTrue(output.contains("Monat: 1.1970"));
        assertTrue(output.contains("01.01.1970"));
        assertFalse(output.contains("06.02.1970"));

        // unbekanntes Konto: keine Ausgabe, kein Absturz
        ByteArrayOutputStream out2 = captureStdout();
        bank.print("S-9999", 1970, 1);
        assertEquals("", out2.toString());
    }

    /**
     * Testet den Gesamtkontostand der Bank.
     *
     * Achtung: Bank.getBalance() rechnet aus Sicht der BANK.
     * Kundenguthaben sind Verbindlichkeiten, deshalb ist das
     * Resultat das NEGATIV der Summe aller Kontostände.
     */
    @Test
    public void testBalance() {
        assertEquals(0, bank.getBalance());

        String s = bank.createSavingsAccount();
        String p = bank.createSalaryAccount(-100000);
        bank.deposit(s, 1, 5000);
        bank.deposit(p, 1, 3000);

        assertEquals(-8000, bank.getBalance());

        bank.withdraw(p, 2, 4000); // Lohnkonto geht 1000 ins Minus
        assertEquals(-4000, bank.getBalance());
    }

    /**
     * Tested die Ausgabe der "top 5" Konten (höchste Saldi zuerst).
     */
    @Test
    public void testTop5() {
        // 6 Konten mit unterschiedlichen Saldi -> nur 5 dürfen erscheinen
        for (int i = 0; i < 6; i++) {
            String id = bank.createSavingsAccount(); // S-1000 .. S-1005
            bank.deposit(id, 1, (i + 1) * 1000L);    // 1000 .. 6000
        }

        ByteArrayOutputStream out = captureStdout();
        bank.printTop5();
        String[] lines = out.toString().trim().split("\\R");

        assertEquals(5, lines.length, "es dürfen nur 5 Konten ausgegeben werden");
        assertEquals("S-1005: 6000", lines[0].trim());
        assertEquals("S-1001: 2000", lines[4].trim());
    }

    /**
     * Tested die Ausgabe der "bottom 5" Konten (tiefste Saldi zuerst).
     */
    @Test
    public void testBottom5() {
        for (int i = 0; i < 6; i++) {
            String id = bank.createSavingsAccount();
            bank.deposit(id, 1, (i + 1) * 1000L);
        }

        ByteArrayOutputStream out = captureStdout();
        bank.printBottom5();
        String[] lines = out.toString().trim().split("\\R");

        assertEquals(5, lines.length);
        assertEquals("S-1000: 1000", lines[0].trim());
        assertEquals("S-1004: 5000", lines[4].trim());
    }
}
