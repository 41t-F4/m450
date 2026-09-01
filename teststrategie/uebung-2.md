# Übung 2 – Funktionale Black-Box-Tests: Autovermietung

**Gewählte Webseite:** [Europcar Schweiz](https://www.europcar.ch) (Online-Autovermietung)

Es handelt sich um **funktionale Black-Box-Tests**: Wir kennen den Code der Plattform nicht und testen als Benutzer über die Oberfläche, ob die **Funktionen** (das *Was*) korrekt arbeiten.

## Die 5 wichtigsten Testfälle

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|----|--------------|---------------------|---------------------|--------|------------------|
| 1 | **Fahrzeugsuche:** Abholort «Zürich Flughafen», Abholdatum morgen 08:00, Rückgabe in 3 Tagen 08:00 eingeben und suchen | Liste verfügbarer Fahrzeuge am gewählten Standort mit Preis pro Miete wird angezeigt | Fahrzeugliste wird korrekt angezeigt | OK | – |
| 2 | **Validierung Mietdauer:** Rückgabedatum VOR dem Abholdatum eingeben | Fehlermeldung, Suche wird nicht ausgeführt | Fehlermeldung «Rückgabe muss nach Abholung liegen» erscheint | OK | – |
| 3 | **Buchung abschliessen:** Fahrzeug wählen, Fahrerdaten und Zahlungsdaten korrekt eingeben, Buchung bestätigen | Buchungsbestätigung mit Buchungsnummer wird angezeigt und per E-Mail versendet, Preis entspricht dem angezeigten Suchpreis | Bestätigung inkl. E-Mail erhalten, Preis identisch | OK | – |
| 4 | **Preisberechnung:** Zusatzoption (z. B. Zusatzfahrer oder Vollkasko) im Buchungsprozess hinzufügen | Gesamtpreis erhöht sich sofort um den ausgewiesenen Optionspreis, Aufschlüsselung stimmt | Gesamtpreis wird korrekt neu berechnet | OK | – |
| 5 | **Buchung stornieren:** Bestehende Buchung über «Meine Buchung» mit Buchungsnummer + Name aufrufen und stornieren | Buchung wird storniert, Stornobestätigung wird angezeigt/gemailt, gemäss Storno-Bedingungen wird der Betrag zurückerstattet | Stornierung funktioniert, Bestätigung erhalten | OK | – |

## Bemerkungen

- Die Spalten «Effektives Resultat», «Status» und «Mögliche Ursache» werden bei der **Testdurchführung** ausgefüllt – die Tabelle dient gleichzeitig als Testprotokoll.
- Weitere sinnvolle Testfälle wären: Login/Registrierung, Mindestalter des Fahrers (Validierung), Standortliste, Sprachumschaltung, Gutscheincodes.
- **Nicht** dabei sind nicht-funktionale Tests wie Ladezeit oder Verhalten bei 1'000 gleichzeitigen Benutzern – das wäre Performance-Testing (das *Wie*).
