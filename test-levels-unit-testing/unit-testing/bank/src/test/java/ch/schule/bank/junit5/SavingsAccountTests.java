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

        // mehr abheben als vorhanden -> abgelehnt
        assertFalse(account.withdraw(2, 5001));
        assertEquals(5000, account.getBalance());

        // exakt den Saldo abheben ist erlaubt (Grenzfall)
        assertTrue(account.withdraw(3, 5000));
        assertEquals(0, account.getBalance());

        // bei Saldo 0 ist gar nichts mehr abhebbar
        assertFalse(account.withdraw(4, 1));

        // negativer Betrag wird abgelehnt (Regel der Basisklasse)
        assertFalse(account.withdraw(5, -100));
        assertEquals(0, account.getBalance());
    }
}
