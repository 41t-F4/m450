# Unit Testing – Aufgabe 3: Banken-Simulation – Funktionsweise

Analyse des Codes (`ch.schule`) und des Klassendiagramms. Projekt: siehe [bank/](bank/).

## Überblick

Die Software simuliert eine **Bank mit verschiedenen Kontoarten**. Es gibt keine Benutzeroberfläche – `Main`/`TestBank` zeigen die Verwendung nur exemplarisch. Beträge werden in **Millirappen** (`long`) geführt, Daten als **Banktage seit 1.1.1970** (`int`, Modell: 360-Tage-Jahr mit 12×30 Tagen).

## Klassen und Zusammenhänge (Stichworte)

- **`Bank`**
  - verwaltet alle Konten in einer `TreeMap<String, Account>` (Kontonummer → Konto)
  - Factory-Methoden: `createSavingsAccount()` («S-…»), `createPromoYouthSavingsAccount()` («Y-…»), `createSalaryAccount(creditLimit)` («P-…», nur negative Limite erlaubt, sonst `null`)
  - fortlaufende Nummerierung ab 1000 über alle Kontoarten (`nextAccountId`)
  - delegiert `deposit`/`withdraw`/`print` ans jeweilige Konto; unbekannte Kontonummer → `false` bzw. keine Ausgabe
  - `getBalance()` = Saldo **aus Sicht der Bank**: Summe aller Kundensaldi, **negativ** gerechnet (Kundenguthaben = Verbindlichkeit)
  - `printTop5()` / `printBottom5()` sortieren die Konten mit den Comparators und drucken max. 5
- **`Account`** (abstrakt) – Basisklasse aller Konten
  - Attribute: `id` (String), `balance` (long, Millirappen), Liste von `Booking`
  - `deposit`/`withdraw`: lehnen **negative Beträge** ab; erzeugen sonst eine Buchung (Abhebung = Buchung mit negativem Betrag)
  - `canTransact(date)`: Buchungen nur **chronologisch** – Datum muss ≥ Datum der letzten Buchung sein
  - die Basisklasse kennt **keine Saldogrenze** (Überziehen erlaubt) – Grenzen definieren die Unterklassen
  - `print()` / `print(year, month)`: Kontoauszug komplett bzw. pro Monat (rechnet Saldo aus den Buchungen hoch)
- **`SavingsAccount`** (Sparkonto) `extends Account`
  - überschreibt `withdraw`: **nie überziehen** (`getBalance() < amount` → `false`)
- **`PromoYouthSavingsAccount`** `extends SavingsAccount`
  - überschreibt `deposit`: **1% Bonus** auf jede Einzahlung (`bonus = amount / 100`, ganzzahlig)
- **`SalaryAccount`** (Lohnkonto) `extends Account`
  - hat eine **negative Kreditlimite**; `withdraw` erlaubt Überziehen bis zur Limite
- **`Booking`** – eine Buchung: `date` (Banktag) + `amount` (Millirappen, negativ = Abhebung); `print(balance)` druckt eine Auszugszeile
- **`BankUtils`** – statische Formatierer: `formatBankDate(int)` (Banktag → «TT.MM.JJJJ», 360-Tage-Logik), `formatAmount(long)` (Millirappen → Betrag mit 2 Nachkommastellen, rechtsbündig)
- **`AccountBalanceComparator`** / **`AccountInverseBalanceComparator`** – sortieren Konten nach Saldo absteigend bzw. aufsteigend (für Top5/Bottom5)

## Vererbungshierarchie

```
Account (abstrakt)
├── SavingsAccount            (kein Überziehen)
│   └── PromoYouthSavingsAccount  (+1% Bonus pro Einzahlung)
└── SalaryAccount             (Überziehen bis Kreditlimite)
```

## Auffälligkeiten (fürs Testen relevant)

- `Bank.getBalance()` liefert das **Negativ** der Kundensaldi – muss man beim Erwartungswert wissen.
- Bonusberechnung ist **ganzzahlig**: Einzahlung < 100 Millirappen → Bonus 0.
- `Account`/`Bank` enthalten ungenutzte `booking`-/`account`-Properties (Getter/Setter) – toter Code aus der Modellierung, wird nicht getestet.
- Ausgaben laufen über `System.out` → in Tests muss stdout umgeleitet und geprüft werden.
