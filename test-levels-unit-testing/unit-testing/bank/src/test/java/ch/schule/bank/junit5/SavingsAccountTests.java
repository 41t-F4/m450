package ch.schule.bank.junit5;

import ch.schule.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für die Klasse SavingsAccount.
 *
 * @author Nicola Wehrli
 * @version 1.0
 */
public class SavingsAccountTests {

    /**
     * Das Sparkonto darf nie überzogen werden.
     */
    @Test
    public void test() {
        SavingsAccount account = new SavingsAccount("S-1000");
        assertTrue(account.deposit(1, 5000));
        assertEquals(5000, account.getBalance());

        // mehr als der Saldo geht nicht, genau der Saldo schon
        assertFalse(account.withdraw(2, 5001));
        assertEquals(5000, account.getBalance());
        assertTrue(account.withdraw(3, 5000));
        assertEquals(0, account.getBalance());

        assertFalse(account.withdraw(4, 1));
        assertFalse(account.withdraw(5, -100));
        assertEquals(0, account.getBalance());
    }
}
