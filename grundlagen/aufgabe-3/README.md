# Aufgabe 3 – Testtreiber für die Preisberechnung

> Setzen Sie den Code um und erstellen Sie einen **Testtreiber** (kein Unit-Test!), um die Preisberechnung zu testen.

## Dateien

| Datei | Inhalt |
|---|---|
| `PriceCalculation.java` | Die Preisberechnung: `calculatePrice(...)` (Original aus der Aufgabe, fehlerhaft) und `calculatePriceFixed(...)` (korrigiert, Bonus) |
| `TestDriver.java` | Testtreiber mit `main`: ruft die Methode mit 8 Testfällen auf und vergleicht IST mit SOLL (PASS/FAIL) |

## Ausführen

```bash
javac PriceCalculation.java TestDriver.java
java TestDriver
```

## Testfälle

Die SOLL-Werte sind aus der **Spezifikation** abgeleitet (nicht aus dem Code!):

| # | baseprice | specialprice | extraprice | extras | discount | SOLL | Original-Code |
|---|---|---|---|---|---|---|---|
| T1 | 10'000 | 2'000 | 1'000 | 0 | 0% | 13'000.00 | PASS |
| T2 | 10'000 | 2'000 | 1'000 | 2 | 0% | 13'000.00 | PASS |
| T3 | 10'000 | 2'000 | 1'000 | 3 | 0% | 12'900.00 | PASS |
| T4 | 10'000 | 2'000 | 1'000 | 4 | 0% | 12'900.00 | PASS |
| T5 | 10'000 | 2'000 | 1'000 | 5 | 0% | 12'850.00 | **FAIL** (IST 12'900) |
| T6 | 10'000 | 2'000 | 1'000 | 8 | 0% | 12'850.00 | **FAIL** (IST 12'900) |
| T7 | 10'000 | 2'000 | 1'000 | 0 | 10% | 12'000.00 | **FAIL** (IST 11'900) |
| T8 | 20'000 | 1'500 | 2'000 | 3 | 5% | 22'300.00 | PASS |

Die Testfälle decken bewusst die **Grenzwerte** der Bedingungen ab (2/3 Extras und 4/5 Extras), denn genau an den Grenzen passieren die meisten Fehler.

## Bonus – Fehler im Code

**Fehler 1 (Hauptfehler): falsche Reihenfolge der Bedingungen**

```java
if (extras >= 3)
    addon_discount = 10;
else if (extras >= 5)   // <- unerreichbar!
    addon_discount = 15;
```

Da jede Zahl ≥ 5 auch ≥ 3 ist, greift immer der erste Zweig. Der 15%-Rabatt wird **nie** vergeben (toter Code). Sichtbar in T5/T6.
**Korrektur:** Zuerst `extras >= 5` prüfen, dann `extras >= 3`.

**Fehler 2: Händlerrabatt überschreibt den Zubehörrabatt**

```java
if (discount > addon_discount)
    addon_discount = discount;
```

Laut Spezifikation gilt der Händlerrabatt **nur auf den Grundpreis** und der Zubehörrabatt **nur auf die Zubehörteile**. Diese Zeile wendet den Händlerrabatt aber zusätzlich auf das Zubehör an, sobald er grösser ist als der Zubehörrabatt. Sichtbar in T7 (10% Händlerrabatt → Zubehör wird fälschlicherweise auch 10% günstiger).
**Korrektur:** Zeile ersatzlos streichen.

Beide Korrekturen sind in `calculatePriceFixed(...)` umgesetzt – damit sind alle 8 Testfälle grün.
