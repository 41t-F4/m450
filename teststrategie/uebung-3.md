# Übung 3 – Testfälle in der Bank-Software

Die Bank-Software (Konsolen-App, `ch.tbz.bank.software`) wurde lokal als Maven-Projekt in Betrieb genommen. Sie simuliert einen Bankschalter: Konten anzeigen/erstellen/löschen, ein- und auszahlen, überweisen (inkl. Währungsumrechnung) und Wechselkurs über eine externe API abfragen.

## 1. Black-Box-Testfälle (als Benutzer über die Konsole)

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|----|--------------|---------------------|---------------------|--------|------------------|
| B1 | App starten | Begrüssung «Willkommen am Schalter 1!» und Hauptmenü erscheinen, 5 Konten vorhanden | wie erwartet | OK | – |
| B2 | «a» eingeben (alle Konten anzeigen) | Liste aller 5 Konten mit Nr., Name, Währung | wie erwartet | OK | – |
| B3 | Gültige Kontonummer (z. B. `3`) eingeben | Kontodetails (Nummer, Nachname, Kontostand mit Währung) und Bearbeitungsmenü | wie erwartet | OK | – |
| B4 | Ungültige Kontonummer (z. B. `99`) eingeben | Fehlermeldung «Ein Konto mit dieser Nummer ist nicht vorhanden!» | wie erwartet | OK | – |
| B5 | Einzahlen: Konto 1, Betrag `100` | Kontostand steigt von 1500.00 auf 1600.00 USD | wie erwartet | OK | – |
| B6 | Einzahlen mit **negativem Betrag** `-500` | Fehlermeldung, Kontostand unverändert | Betrag wird akzeptiert, Kontostand **sinkt** um 500 | **Fehler** | `deposit()` validiert den Betrag nicht (`balance += amount`) |
| B7 | Abheben: Betrag grösser als Kontostand | Fehlermeldung «Kontostand zu niedrig» | wie erwartet | OK | – |
| B8 | Abheben mit **negativem Betrag** `-500` | Fehlermeldung, Kontostand unverändert | Kontostand **steigt** um 500 | **Fehler** | `withdraw()` prüft nur `amount > balance`, nicht `amount <= 0` |
| B9 | Überweisung USD→CHF (Konto 1 → Konto 3), Betrag 100 | Zielkonto erhält 111.00 CHF (Kurs 1.11) | wie erwartet | OK | – |
| B10 | Überweisung **EUR→CHF** (Konto 2 → Konto 3), Betrag 100 | Betrag wird zum EUR/CHF-Kurs umgerechnet | Meldung «Es wurde keine Umrechnung vorgenommen», Zielkonto erhält 100 CHF **1:1** | **Fehler** | `convertCurrency()` implementiert nur USD→CHF, USD→EUR, CHF→USD |
| B11 | Konto erstellen («e»), Name + Währung `CHF` | Neues Konto mit korrekter Währung und Startsaldo 0 erscheint | wie erwartet | OK | – |
| B12 | Konto erstellen mit unbekannter Währung `ABC` | Fehlermeldung und erneute Eingabeaufforderung | Meldung «…nicht bekannt, es wird USD verwendet» – Konto wird **stillschweigend mit USD** erstellt | Mangel | Default-Zweig im `switch` statt erneuter Abfrage |
| B13 | Konto löschen («l»), mit `j` bestätigen | Konto wird gelöscht und erscheint nicht mehr in der Liste | wie erwartet | OK | – |
| B14 | **Leere Eingabe** (nur Enter) im Bearbeitungsmenü | Fehlermeldung «Ungültige Eingabe» | App **stürzt ab** (`StringIndexOutOfBoundsException`) | **Fehler** | `input.substring(0,1)` ohne Prüfung auf leeren String |
| B15 | Wechselkurs abfragen («w», z. B. `CHF USD`) | Aktueller Kurs wird angezeigt («1 CHF = x USD») | abhängig von API/Key; bei Fehler saubere Fehlermeldung | OK/Fehler | API-Key abgelaufen, kein Netz, Zertifikat |
| B16 | «q» eingeben | «Auf Wiedersehen!», Programm beendet sich | wie erwartet | OK | – |

## 2. Kandidaten für White-Box-Testfälle

Für White-Box-Tests eignen sich Methoden mit **Logik und Rückgabewert**, die ohne Konsolen-Eingabe testbar sind:

| Klasse.Methode | Warum geeignet |
|---|---|
| `Account.withdraw(double)` | Verzweigung (genug/zu wenig Saldo, Grenzfall `amount == balance`), Rückgabewert `boolean` – alle Code-Pfade einfach prüfbar |
| `Account.deposit(double)` | Zustandstest des Kontostands, Grenzfälle 0 und negative Beträge |
| `Counter.convertCurrency(double, Currency, Currency)` | Mehrere if-Pfade (3 Kurse + «keine Umrechnung»-Pfad) → ideal für Code-Pfad-Testing |
| `Bank.getAccount(int)` | Suche in Liste: gefunden / nicht gefunden (`null`) |
| `Bank.createAccount(...)` / `deleteAccount(...)` / `getNumberOfAccounts()` | Zustand der Kontenliste vor/nach der Operation |
| `Counter.chooseAccount()` – Eingabe-Regex | Pattern `\d|a|e|w|q` mit `find()` matcht auch ungültige Eingaben wie `xq` oder `hallo1` – Pfad lohnt sich zu testen |
| `ExchangeRateOkhttp.getExchangeRate(...)` | Nur mit Test-Double (Mock der HTTP-Antwort) sinnvoll testbar – Thema kommt später im Modul |

## 3. Verbesserungen / Best Practices

1. **Eingabevalidierung ins Modell:** `deposit`/`withdraw` müssen negative und Null-Beträge ablehnen – Validierung gehört in die `Account`-Klasse, nicht (nur) in die UI.
2. **Logik von Ein-/Ausgabe trennen:** `Counter` mischt Scanner-Eingabe, Geschäftslogik und `System.out`-Ausgabe. Getrennte Schichten wären viel besser (unit-)testbar.
3. **`convertCurrency` vervollständigen** (EUR→CHF, CHF→EUR, EUR→USD) oder fehlende Kurse als Fehler behandeln statt stillschweigend 1:1 zu überweisen; Kurse nicht als Konstanten im Code hartkodieren.
4. **API-Key nicht im Quellcode:** Der Key in `ExchangeRateOkhttp` gehört in eine Konfiguration/Umgebungsvariable (Sicherheit!).
5. **Robustere Eingabebehandlung:** `substring(0,1)` auf leeren Eingaben crasht; Regex mit `matches()` statt `find()` verwenden, damit «xq» nicht als gültig gilt.
6. **Fehlerbehandlung:** Statt `catch (Exception e)` mit `instanceof`-Kaskaden gezielte Multi-Catch-Blöcke (`catch (AccountExeption | NumberFormatException e)`) verwenden; Tippfehler `AccountExeption` → `AccountException`; Exception als eigene Top-Level-Klasse statt innere Klasse von `Counter`.
7. **Währung bei Kontoerstellung:** Bei unbekanntem Kürzel erneut fragen statt stillschweigend USD zu setzen; `Currency.valueOf()` mit Fehlerbehandlung nutzen statt `switch`.
8. **Geldbeträge nicht als `double`:** Für Finanzberechnungen `BigDecimal` (oder Rappen als `long`) verwenden – `double` rundet binär und erzeugt Rundungsfehler.
9. **Statischer Zähler:** `Account.counter` ist `static` und wird nie zurückgesetzt – erschwert Tests; besser die ID-Vergabe der `Bank` überlassen.
10. **Toten Code entfernen:** auskommentierte Blöcke und `pseudoDeleteAccount()` löschen (Git ist die History).
