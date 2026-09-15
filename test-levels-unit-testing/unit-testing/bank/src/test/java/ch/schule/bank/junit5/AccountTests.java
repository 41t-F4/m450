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

    // Account ist abstrakt, darum eine minimale Unterklasse nur für die Tests
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

    /**
     * Tested die Initialisierung eines Kontos.
     */
    @Test
    public void testInit() {
        Account account = new PlainAccount("A-0001");

        assertEquals("A-0001", account.getId());
        assertEquals(0, account.getBalance());
        assertTrue(account.canTransact(0));
    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    public void testDeposit() {
        Account account = new PlainAccount("A-0001");

        assertTrue(account.deposit(10, 5000));
        assertEquals(5000, account.getBalance());

        // negativer Betrag und Buchung in der Vergangenheit gehen nicht
        assertFalse(account.deposit(11, -100));
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

        assertFalse(account.withdraw(12, -50));
        assertEquals(3000, account.getBalance());

        // die Basisklasse hat keine Saldo-Grenze, überziehen geht
        assertTrue(account.withdraw(13, 10000));
        assertEquals(-7000, account.getBalance());

        assertFalse(account.withdraw(5, 100));
        assertEquals(-7000, account.getBalance());
    }

    /**
     * Tests the reference from SavingsAccount
     */
    @Test
    public void testReferences() {
        Account account = new SavingsAccount("S-9999");

        assertInstanceOf(Account.class, account);
        account.deposit(1, 1000);

        // Account-Referenz, aber es greift trotzdem SavingsAccount.withdraw (kein Überziehen)
        assertFalse(account.withdraw(2, 2000));
        assertEquals(1000, account.getBalance());
    }

    /**
     * teste the canTransact Flag
     */
    @Test
    public void testCanTransact() {
        Account account = new PlainAccount("A-0001");

        assertTrue(account.canTransact(0));
        assertTrue(account.canTransact(999));

        account.deposit(100, 500);

        // gleicher Tag oder später ok, früher nicht
        assertTrue(account.canTransact(100));
        assertTrue(account.canTransact(101));
        assertFalse(account.canTransact(99));
    }

    /**
     * Experimente mit print().
     */
    @Test
    public void testPrint() {
        Account account = new PlainAccount("A-0001");
        account.deposit(0, 100000);
        account.withdraw(30, 50000);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        account.print();
        String output = out.toString();

        assertTrue(output.contains("Kontoauszug 'A-0001'"));
        assertTrue(output.contains("01.01.1970"));
        assertTrue(output.contains("01.02.1970"));
        assertTrue(output.contains("1.00"));
    }

    /**
     * Experimente mit print(year,month).
     */
    @Test
    public void testMonthlyPrint() {
        Account account = new PlainAccount("A-0001");
        account.deposit(0, 100000);   // Januar 1970
        account.deposit(35, 200000);  // Februar 1970

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        account.print(1970, 1);
        String output = out.toString();

        assertTrue(output.contains("Monat: 1.1970"));
        assertTrue(output.contains("01.01.1970"));
        assertFalse(output.contains("06.02.1970"));
    }
}
