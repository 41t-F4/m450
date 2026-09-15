package ch.schule.bank.junit5;

import ch.schule.PromoYouthSavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für das Promo-Jugend-Sparkonto.
 *
 * @author Nicola Wehrli
 * @version 1.0
 */
public class PromoYouthSavingsAccountTests {

    /**
     * Jede Einzahlung gibt 1% Bonus, sonst gelten die Sparkonto-Regeln.
     */
    @Test
    public void test() {
        PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-1000");

        assertTrue(account.deposit(1, 10000));
        assertEquals(10100, account.getBalance()); // 10000 + 1% Bonus

        // Bonus ist Ganzzahl-Division: bei 99 gibts nichts
        assertTrue(account.deposit(2, 99));
        assertEquals(10199, account.getBalance());

        assertFalse(account.deposit(3, -1000));
        assertEquals(10199, account.getBalance());

        // geerbt vom Sparkonto: überziehen geht nicht
        assertFalse(account.withdraw(4, 99999));
        assertEquals(10199, account.getBalance());
    }
}
