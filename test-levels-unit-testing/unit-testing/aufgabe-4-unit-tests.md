# Unit Testing – Aufgabe 4: Unit-Tests für die Banken-Simulation

Die Tests liegen im Projekt [bank/](bank/) unter `src/test/java/ch/schule/bank/junit5/`.

## Ausführen

```bash
cd bank
./mvnw test
```

**Resultat: 20 Tests, 0 Failures** (AccountTests 7, BankTests 8, BookingTests 2, SavingsAccountTests 1, SalaryAccountTests 1, PromoYouthSavingsAccountTests 1).

## Was getestet wird (Auswahl)

| Testklasse | Abgedeckte Fälle |
|---|---|
| `AccountTests` | Initialisierung; Einzahlen (inkl. negativer Betrag, Buchung in der Vergangenheit); Abheben (Basisklasse erlaubt Überziehen!); Polymorphie über `Account`-Referenz; `canTransact` mit Grenzfall «gleicher Tag»; `print()`/`print(year, month)` mit umgeleitetem `System.out` |
| `BankTests` | Konto-Erstellung (Id-Sequenz S-/Y-/P-1000…, ungültige Kreditlimite → `null`); deposit/withdraw inkl. unbekanntem Konto; Gesamtsaldo (**negativ** aus Bank-Sicht!); Top5/Bottom5 (max. 5 Zeilen, korrekte Sortierung) |
| `BookingTests` | Erzeugung (auch negativer Betrag = Abhebung); formatierte Ausgabe (Datum, Betrag) |
| `SavingsAccountTests` | Nie überziehen; Grenzfall «exakt den Saldo abheben»; negativer Betrag |
| `SalaryAccountTests` | Überziehen bis exakt zur Kreditlimite (Grenzfall), darunter abgelehnt; Einzahlung schafft Spielraum |
| `PromoYouthSavingsAccountTests` | 1% Bonus; ganzzahliger Bonus (99 → Bonus 0); geerbte Sparkonto-Regeln |

## Angewendete Prinzipien

- **Isolation:** Jeder Test baut seine eigenen Objekte (`@BeforeEach` in `BankTests`); keine Abhängigkeit von Ausführungsreihenfolge.
- **Grenzwerte:** exakt der Saldo, exakt die Kreditlimite, gleicher Buchungstag, Bonus unter 100 Millirappen, 6 Konten bei Top **5**.
- **stdout-Tests:** `print`-Methoden schreiben auf `System.out` → wird mit `ByteArrayOutputStream` umgeleitet und der Inhalt geprüft; `@AfterEach` stellt stdout wieder her.
- **Erwartungswerte aus der Spezifikation/Javadoc**, nicht aus dem Code (z. B. das negative Vorzeichen von `Bank.getBalance()` ist dokumentiertes Verhalten aus Bank-Sicht).

## Code Coverage

Gemessen mit JaCoCo (`./mvnw org.jacoco:jacoco-maven-plugin:0.8.13:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.13:report`, Report unter `target/site/jacoco/index.html`):

| Klasse | Zeilen | Methoden |
|---|---|---|
| Account | 46/49 | 8/10 |
| Bank | 59/63 | 12/14 |
| Booking, BankUtils, Comparators, alle Kontoarten | ~100% | ~100% |
| Main (Demo, kein Testobjekt) | 0/5 | 0/2 |
| **Total** | **92%** | **84%** |

Nicht abgedeckt sind nur die Demo-Klasse `Main` und die **ungenutzten** `booking`/`account`-Getter/Setter (toter Code) – gemäss Theorie testen gute Unit-Tests *relevanten* Code, keine leeren Getter/Setter.

In IntelliJ: Rechtsklick auf `src/test/java` → **Run 'All Tests' with Coverage** zeigt dieselben Werte.
