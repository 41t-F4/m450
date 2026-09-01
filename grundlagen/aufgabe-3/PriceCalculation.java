/**
 * Preisberechnung für die Auto-Verkauf-Software (Aufgabe 3, Grundlagen).
 *
 * Regeln gemäss Aufgabenstellung:
 * - Grundpreis (baseprice) abzüglich Händlerrabatt (discount, in %).
 *   Der Händlerrabatt bezieht sich NUR auf den Grundpreis.
 * - Dazu kommen Sondermodellaufschlag (specialprice) und Zusatzausstattungen (extraprice).
 * - Ab 3 Zusatzausstattungen (extras): 10% Rabatt auf extraprice.
 * - Ab 5 Zusatzausstattungen: 15% Rabatt auf extraprice.
 */
public class PriceCalculation {

    /**
     * Originale Version aus der Aufgabenstellung – enthält absichtlich Fehler
     * (siehe README.md, Bonus-Aufgabe).
     */
    double calculatePrice(double baseprice, double specialprice, double extraprice, int extras, double discount) {
        double addon_discount;
        double result;

        if (extras >= 3)
            addon_discount = 10;
        else if (extras >= 5)
            addon_discount = 15;
        else
            addon_discount = 0;

        if (discount > addon_discount)
            addon_discount = discount;

        result = baseprice / 100.0 * (100 - discount) + specialprice
                + extraprice / 100.0 * (100 - addon_discount);

        return result;
    }

    /**
     * Korrigierte Version (Bonus-Aufgabe):
     * 1. Die Bedingung "extras >= 5" muss VOR "extras >= 3" geprüft werden,
     *    sonst ist der 15%-Zweig unerreichbar (toter Code).
     * 2. Die Zeile "if (discount > addon_discount) addon_discount = discount;"
     *    wurde entfernt: Laut Spezifikation gilt der Händlerrabatt nur für den
     *    Grundpreis und der Zubehörrabatt nur für die Zubehörteile.
     */
    double calculatePriceFixed(double baseprice, double specialprice, double extraprice, int extras, double discount) {
        double addon_discount;

        if (extras >= 5)
            addon_discount = 15;
        else if (extras >= 3)
            addon_discount = 10;
        else
            addon_discount = 0;

        return baseprice / 100.0 * (100 - discount) + specialprice
                + extraprice / 100.0 * (100 - addon_discount);
    }
}
