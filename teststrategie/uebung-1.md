# Übung 1 – Abstrakte und konkrete Testfälle (Rabattregeln)

> *Bei einem Kaufpreis von weniger als 15'000 CHF soll kein Rabatt gewährt werden. Bei einem Preis bis zu 20'000 CHF sind 5% Rabatt angemessen. Liegt der Kaufpreis unter 25'000 CHF, sind 7% Rabatt möglich, darüber sind 8,5% Rabatt zu gewähren.*

## Abstrakte Testfälle

Abstrakte Testfälle haben **keine konkreten Werte**, sondern beschreiben die Eingaben mit logischen Operatoren.

| ID | Eingabe (Kaufpreis x in CHF) | Erwartetes Resultat (Rabatt) |
|----|------------------------------|------------------------------|
| A1 | x < 15'000                   | 0%                           |
| A2 | 15'000 ≤ x ≤ 20'000          | 5%                           |
| A3 | 20'000 < x < 25'000          | 7%                           |
| A4 | x ≥ 25'000                   | 8.5%                         |

## Konkrete Testfälle

Konkrete Testfälle verwenden **echte Eingabewerte**. Pro Äquivalenzklasse ein «normaler» Wert plus die **Grenzwerte**, weil dort erfahrungsgemäss die meisten Fehler passieren.

| ID  | Kaufpreis (CHF) | Erwarteter Rabatt | Rabattbetrag (CHF) | Bemerkung                          |
|-----|-----------------|-------------------|--------------------|------------------------------------|
| K1  | 10'000.00       | 0%                | 0.00               | Normalwert Klasse A1               |
| K2  | 14'999.95       | 0%                | 0.00               | Grenzwert: knapp unter 15'000      |
| K3  | 15'000.00       | 5%                | 750.00             | Grenzwert: exakt 15'000            |
| K4  | 18'000.00       | 5%                | 900.00             | Normalwert Klasse A2               |
| K5  | 20'000.00       | 5%                | 1'000.00           | Grenzwert: exakt 20'000 («bis zu») |
| K6  | 20'000.05       | 7%                | 1'400.00           | Grenzwert: knapp über 20'000       |
| K7  | 22'500.00       | 7%                | 1'575.00           | Normalwert Klasse A3               |
| K8  | 24'999.95       | 7%                | 1'750.00           | Grenzwert: knapp unter 25'000      |
| K9  | 25'000.00       | 8.5%              | 2'125.00           | Grenzwert: exakt 25'000 («darüber»)|
| K10 | 30'000.00       | 8.5%              | 2'550.00           | Normalwert Klasse A4               |

## Erkenntnis

Die Anforderung ist an den Grenzen **unpräzise formuliert**: Ist bei exakt 25'000 CHF noch 7% oder schon 8.5% gemeint («unter 25'000 … darüber»)? Solche Unklarheiten entdeckt man genau beim Ableiten von Testfällen – sie müssten mit dem Auftraggeber geklärt werden, bevor implementiert wird.
