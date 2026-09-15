// Einfacher Testtreiber (bewusst ohne JUnit): ruft calculatePrice mit fixen
// Werten auf und vergleicht mit dem erwarteten Resultat aus der Spezifikation.
public class TestDriver {

    static PriceCalculation calc = new PriceCalculation();

    public static void main(String[] args) {
        System.out.println("=== Testtreiber: originale (fehlerhafte) Version ===");
        boolean okOriginal = runTests(false);
        System.out.println("\n=== Testtreiber: korrigierte Version ===");
        boolean okFixed = runTests(true);

        System.out.println("\nResultat original:   " + (okOriginal ? "ALLE TESTS OK" : "TESTS FEHLGESCHLAGEN"));
        System.out.println("Resultat korrigiert: " + (okFixed ? "ALLE TESTS OK" : "TESTS FEHLGESCHLAGEN"));
    }

    static boolean runTests(boolean fixed) {
        boolean ok = true;

        // baseprice, specialprice, extraprice, extras, discount, erwartet
        ok &= check("T1: keine Extras, kein Rabatt", 10000, 2000, 1000, 0, 0, 13000.00, fixed);
        ok &= check("T2: 2 Extras -> kein Zubehoerrabatt", 10000, 2000, 1000, 2, 0, 13000.00, fixed);
        ok &= check("T3: 3 Extras -> 10% Zubehoerrabatt", 10000, 2000, 1000, 3, 0, 12900.00, fixed);
        ok &= check("T4: 4 Extras -> 10% Zubehoerrabatt", 10000, 2000, 1000, 4, 0, 12900.00, fixed);
        ok &= check("T5: 5 Extras -> 15% Zubehoerrabatt", 10000, 2000, 1000, 5, 0, 12850.00, fixed);
        ok &= check("T6: 8 Extras -> 15% Zubehoerrabatt", 10000, 2000, 1000, 8, 0, 12850.00, fixed);
        ok &= check("T7: Haendlerrabatt 10% nur auf Grundpreis", 10000, 2000, 1000, 0, 10, 12000.00, fixed);
        ok &= check("T8: Haendlerrabatt 5% + 3 Extras", 20000, 1500, 2000, 3, 5, 22300.00, fixed);

        return ok;
    }

    static boolean check(String name, double baseprice, double specialprice, double extraprice,
                         int extras, double discount, double expected, boolean fixed) {

        double price = fixed
                ? calc.calculatePriceFixed(baseprice, specialprice, extraprice, extras, discount)
                : calc.calculatePrice(baseprice, specialprice, extraprice, extras, discount);

        boolean ok = Math.abs(price - expected) < 0.001;
        System.out.printf("%-45s SOLL: %10.2f | IST: %10.2f | %s%n",
                name, expected, price, ok ? "PASS" : "FAIL");
        return ok;
    }
}
