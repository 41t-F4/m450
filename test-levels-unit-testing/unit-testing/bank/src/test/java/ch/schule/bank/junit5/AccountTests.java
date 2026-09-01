package ch.schule.bank.junit5;

import ch.schule.Account;
import ch.schule.SavingsAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für die Klasse Account.
 *
 * @author Nicola Wehrli
 * @version 1.0
 */
public class AccountTests {

    /**
     * Account ist abstrakt - für die Tests der Basisklasse verwenden
     * wir eine minimale konkrete Unterklasse ohne Zusatzlogik.
     */
    private static class PlainAccount extends Account {
        PlainAccount(String id) {
            super(id);
        }
    }

    private final PrintStream originalOut = System.out;

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
     * Tested die Initialisierung eines Kontos.
     */
    @Test
    public void testInit() {
        Account account = new PlainAccount("A-0001");

        assertEquals("A-0001", account.getId());
        assertEquals(0, account.getBalance());
        assertTrue(account.canTransact(0), "Neues Konto ohne Buchungen muss buchbar sein");
    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    public void testDeposit() {
        Account account = new PlainAccount("A-0001");

        assertTrue(account.deposit(10, 5000));
        assertEquals(5000, account.getBalance());

        // negativer Betrag wird abgelehnt, Saldo bleibt unverändert
        assertFalse(account.deposit(11, -100));
        assertEquals(5000, account.getBalance());

        // Buchung in der Vergangenheit (vor letzter Buchung) wird abgelehnt
        assertFalse(account.deposit(9, 1000));
        assertEquals(5000, account.getBalance());
    }

    /**
     * Testet das Abheben von einem Konto.
     */
    @Test
    public void testWithdraw() {
        Account account = new PlainAccount("A-0001");
        account.deposit(10, 5000);

        assertTrue(account.withdraw(11, 2000));
        assertEquals(3000, account.getBalance());

        // negativer Betrag wird abgelehnt
        assertFalse(account.withdraw(12, -50));
        assertEquals(3000, account.getBalance());

        // Die Basisklasse kennt KEINE Saldo-Grenze: Überziehen ist erlaubt
        assertTrue(account.withdraw(13, 10000));
        assertEquals(-7000, account.getBalance());

        // Buchung in der Vergangenheit wird abgelehnt
        assertFalse(account.withdraw(5, 100));
        assertEquals(-7000, account.getBalance());
    }

    /**
     * Tests the reference from SavingsAccount:
     * Eine SavingsAccount-Instanz kann als Account referenziert werden
     * (Polymorphie) - die überschriebene withdraw-Logik greift trotzdem.
     */
    @Test
    public void testReferences() {
        Account account = new SavingsAccount("S-9999");

        assertInstanceOf(Account.class, account);
        account.deposit(1, 1000);

        // dynamische Bindung: SavingsAccount.withdraw verhindert Überziehen,
        // obwohl die Referenz vom Typ Account ist
        assertFalse(account.withdraw(2, 2000));
        assertEquals(1000, account.getBalance());
    }

    /**
     * Testet das canTransact-Flag (Buchungen nur chronologisch).
     */
    @Test
    public void testCanTransact() {
        Account account = new PlainAccount("A-0001");

        // ohne Buchungen: jedes Datum erlaubt
        assertTrue(account.canTransact(0));
        assertTrue(account.canTransact(999));

        account.deposit(100, 500);

        assertTrue(account.canTransact(100), "gleicher Tag wie letzte Buchung ist erlaubt");
        assertTrue(account.canTransact(101), "späteres Datum ist erlaubt");
        assertFalse(account.canTransact(99), "früheres Datum ist nicht erlaubt");
    }

    /**
     * Experimente mit print().
     */
    @Test
    public void testPrint() {
        Account account = new PlainAccount("A-0001");
        account.deposit(0, 100000);  // 1.00 am 01.01.1970
        account.withdraw(30, 50000); // -0.50 am 01.02.1970

        ByteArrayOutputStream out = captureStdout();
        account.print();
        String output = out.toString();

        assertTrue(output.contains("Kontoauszug 'A-0001'"));
        assertTrue(output.contains("01.01.1970"));
        assertTrue(output.contains("01.02.1970"));
        assertTrue(output.contains("1.00"));
    }

    /**
     * Experimente mit print(year, month).
     */
    @Test
    public void testMonthlyPrint() {
        Account account = new PlainAccount("A-0001");
        account.deposit(0, 100000);   // Januar 1970 (Banktag 0)
        account.deposit(35, 200000);  // Februar 1970 (Banktag 35)

        ByteArrayOutputStream out = captureStdout();
        account.print(1970, 1);
        String output = out.toString();

        assertTrue(output.contains("Monat: 1.1970"));
        assertTrue(output.contains("01.01.1970"), "Januar-Buchung muss erscheinen");
        assertFalse(output.contains("06.02.1970"), "Februar-Buchung darf nicht erscheinen");
    }
}
