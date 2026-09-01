# Aufgabe 1 – Formen von Tests aus der Informatik

> Welche Formen von Tests kennen Sie aus der Informatik? Mind. drei Beispiele aus der Praxis. Wie werden die Tests durchgeführt?

## 1. Unit-Test (Komponententest)

**Was:** Eine einzelne, kleinste Einheit der Software (Methode oder Klasse) wird isoliert getestet, z. B. eine Methode `calculateDiscount()`, die aus einem Kaufpreis den Rabatt berechnet.

**Durchführung:** Der Entwickler schreibt Testcode, der die Methode mit definierten Eingabewerten aufruft und das Resultat mit dem erwarteten Wert vergleicht (z. B. mit JUnit in Java). Die Tests laufen automatisiert bei jedem Build – lokal in der IDE oder in der CI-Pipeline.

**Praxisbeispiel:** Im ÜK / in Java-Projekten haben wir mit JUnit geprüft, ob eine Berechnungsmethode für Randwerte (0, negative Zahlen, Maximalwerte) das richtige Resultat liefert.

## 2. Manueller System-/GUI-Test

**Was:** Das fertige System wird als Ganzes über die Benutzeroberfläche geprüft – so, wie es ein Endbenutzer verwenden würde.

**Durchführung:** Ein Tester arbeitet ein Testprotokoll mit vorbereiteten Testfällen ab («Login mit gültigen Daten», «Login mit falschem Passwort», …), führt die Schritte von Hand aus und dokumentiert IST- vs. SOLL-Resultat in einer Tabelle. Der Code ist dabei nicht sichtbar (Black-Box).

**Praxisbeispiel:** Vor einem Release einer Web-Applikation klickt man die wichtigsten Abläufe (Registrierung, Bestellung, Bezahlung) manuell durch und hält Abweichungen als Bug-Tickets fest.

## 3. Automatisierter End-to-End-/Integrationstest

**Was:** Mehrere Komponenten (Frontend, Backend, Datenbank) werden im Zusammenspiel getestet, oft automatisiert über die UI oder die API.

**Durchführung:** Mit Werkzeugen wie Selenium, Playwright oder Cypress werden Benutzeraktionen (Klicks, Eingaben) per Skript simuliert; API-Tests werden z. B. mit Postman/REST-Clients ausgeführt. Die Tests laufen automatisch in der CI/CD-Pipeline nach jedem Push – schlägt ein Test fehl, wird das Deployment gestoppt.

**Praxisbeispiel:** Ein Webshop testet nach jedem Merge automatisch, ob der komplette Bestellablauf «Artikel in Warenkorb → Checkout → Bestellbestätigung» noch funktioniert.

## Weitere bekannte Formen (Ergänzung)

- **Abnahmetest:** Der Kunde prüft am Schluss, ob das System seine Anforderungen erfüllt (Grundlage für die Abnahme).
- **Performance-/Lasttest:** Es wird gemessen, wie sich das System unter Last verhält (z. B. mit JMeter viele gleichzeitige Anfragen simulieren) – ein nicht-funktionaler Test.
- **Penetrationstest:** Sicherheitstest, bei dem gezielt versucht wird, Schwachstellen auszunutzen.
