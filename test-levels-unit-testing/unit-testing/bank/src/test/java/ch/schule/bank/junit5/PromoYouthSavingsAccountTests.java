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
     * Jede Einzahlung erhält 1% Bonus; sonst gelten die Sparkonto-Regeln.
     */
    @Test
    public void test() {
        PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-1000");

        // 10'000 einzahlen -> 1% Bonus -> 10'100
        assertTrue(account.deposit(1, 10000));
        assertEquals(10100, account.getBalance());

        // Bonus wird ganzzahlig gerechnet: 99 / 100 = 0 Bonus
        assertTrue(account.deposit(2, 99));
        assertEquals(10199, account.getBalance());

        // negative Einzahlung wird abgelehnt
        assertFalse(account.deposit(3, -1000));
        assertEquals(10199, account.getBalance());

        // geerbte Sparkonto-Regel: nicht überziehbar
        assertFalse(account.withdraw(4, 99999));
        assertEquals(10199, account.getBalance());
    }
}
