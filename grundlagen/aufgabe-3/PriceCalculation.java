// Aufgabe 3 (Grundlagen): Preisberechnung Auto-Verkauf
// calculatePrice = Original aus der Aufgabenstellung (enthält Fehler, siehe README)
// calculatePriceFixed = korrigierte Version (Bonus)
public class PriceCalculation {

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

    double calculatePriceFixed(double baseprice, double specialprice, double extraprice, int extras, double discount) {
        double addon_discount;

        // Fehler 1: >= 5 muss vor >= 3 geprüft werden, sonst kommt man nie in den 15%-Zweig
        if (extras >= 5)
            addon_discount = 15;
        else if (extras >= 3)
            addon_discount = 10;
        else
            addon_discount = 0;

        // Fehler 2: das "if (discount > addon_discount)" aus dem Original ist raus,
        // der Händlerrabatt gilt laut Spezifikation nur auf den Grundpreis
        return baseprice / 100.0 * (100 - discount) + specialprice
                + extraprice / 100.0 * (100 - addon_discount);
    }
}
