/**
 * Testtreiber für die Preisberechnung (KEIN Unit-Test / kein JUnit).
 *
 * Der Testtreiber ruft die Methode calculatePrice(...) mit definierten
 * Eingabewerten auf, vergleicht das Resultat mit dem laut Spezifikation
 * erwarteten SOLL-Wert und gibt PASS/FAIL aus.
 */
public class TestDriver {

    private static final double EPSILON = 0.001;
    private static final PriceCalculation calc = new PriceCalculation();

    public static void main(String[] args) {
        System.out.println("=== Testtreiber: originale (fehlerhafte) Version ===");
        boolean okOriginal = test_calculate_price(false);
        System.out.println("\n=== Testtreiber: korrigierte Version ===");
        boolean okFixed = test_calculate_price(true);

        System.out.println("\nResultat original:   " + (okOriginal ? "ALLE TESTS OK" : "TESTS FEHLGESCHLAGEN"));
        System.out.println("Resultat korrigiert: " + (okFixed ? "ALLE TESTS OK" : "TESTS FEHLGESCHLAGEN"));
    }

    static boolean test_calculate_price(boolean useFixedVersion) {
        boolean test_ok = true;

        // Testfälle: baseprice, specialprice, extraprice, extras, discount, erwartetes Resultat (SOLL laut Spezifikation)
        test_ok &= check("T1: keine Extras, kein Rabatt", 10000, 2000, 1000, 0, 0, 13000.00, useFixedVersion);
        test_ok &= check("T2: 2 Extras -> kein Zubehoerrabatt", 10000, 2000, 1000, 2, 0, 13000.00, useFixedVersion);
        test_ok &= check("T3: 3 Extras -> 10% Zubehoerrabatt", 10000, 2000, 1000, 3, 0, 12900.00, useFixedVersion);
        test_ok &= check("T4: 4 Extras -> 10% Zubehoerrabatt", 10000, 2000, 1000, 4, 0, 12900.00, useFixedVersion);
        test_ok &= check("T5: 5 Extras -> 15% Zubehoerrabatt", 10000, 2000, 1000, 5, 0, 12850.00, useFixedVersion);
        test_ok &= check("T6: 8 Extras -> 15% Zubehoerrabatt", 10000, 2000, 1000, 8, 0, 12850.00, useFixedVersion);
        test_ok &= check("T7: Haendlerrabatt 10% nur auf Grundpreis", 10000, 2000, 1000, 0, 10, 12000.00, useFixedVersion);
        test_ok &= check("T8: Haendlerrabatt 5% + 3 Extras", 20000, 1500, 2000, 3, 5, 22300.00, useFixedVersion);

        return test_ok;
    }

    static boolean check(String name, double baseprice, double specialprice, double extraprice,
                         int extras, double discount, double expected, boolean useFixedVersion) {

        double price = useFixedVersion
                ? calc.calculatePriceFixed(baseprice, specialprice, extraprice, extras, discount)
                : calc.calculatePrice(baseprice, specialprice, extraprice, extras, discount);

        boolean ok = Math.abs(price - expected) < EPSILON;
        System.out.printf("%-45s SOLL: %10.2f | IST: %10.2f | %s%n",
                name, expected, price, ok ? "PASS" : "FAIL");
        return ok;
    }
}
