package ch.schule.bank.junit5;

import ch.schule.SalaryAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests der Klasse SalaryAccount.
 *
 * @author Nicola Wehrli
 * @version 1.1
 */
public class SalaryAccountTests {

    /**
     * Das Lohnkonto darf bis zur (negativen) Kreditlimite überzogen werden.
     */
    @Test
    public void test() {
        SalaryAccount account = new SalaryAccount("P-1000", -50000);

        // Überziehen innerhalb der Kreditlimite ist erlaubt
        assertTrue(account.withdraw(1, 30000));
        assertEquals(-30000, account.getBalance());

        // exakt bis zur Limite ist erlaubt (Grenzfall)
        assertTrue(account.withdraw(2, 20000));
        assertEquals(-50000, account.getBalance());

        // unter die Limite -> abgelehnt
        assertFalse(account.withdraw(3, 1));
        assertEquals(-50000, account.getBalance());

        // Einzahlung schafft wieder Spielraum
        assertTrue(account.deposit(4, 60000));
        assertEquals(10000, account.getBalance());
        assertTrue(account.withdraw(5, 15000));
        assertEquals(-5000, account.getBalance());
    }
}
