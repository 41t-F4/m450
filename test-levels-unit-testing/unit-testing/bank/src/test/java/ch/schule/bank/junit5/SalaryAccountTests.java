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
     * Das Lohnkonto darf bis zur Kreditlimite überzogen werden.
     */
    @Test
    public void test() {
        SalaryAccount account = new SalaryAccount("P-1000", -50000);

        assertTrue(account.withdraw(1, 30000));
        assertEquals(-30000, account.getBalance());

        // genau bis zur Limite geht, darunter nicht mehr
        assertTrue(account.withdraw(2, 20000));
        assertEquals(-50000, account.getBalance());
        assertFalse(account.withdraw(3, 1));
        assertEquals(-50000, account.getBalance());

        // nach einer Einzahlung hat man wieder Spielraum
        assertTrue(account.deposit(4, 60000));
        assertEquals(10000, account.getBalance());
        assertTrue(account.withdraw(5, 15000));
        assertEquals(-5000, account.getBalance());
    }
}
