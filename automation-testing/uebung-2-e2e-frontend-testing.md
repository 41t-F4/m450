# Automation Testing – Übung 2: Angular-GUI automatisiert testen (E2E)

## Tool-Wahl
Angeschaut: Selenium (viel Boilerplate, Driver-Management), Cypress und **Playwright**.
Entschieden habe ich mich für Playwright: Auto-Waiting, startet Backend und Frontend
über die `webServer`-Config gleich selbst, und bei Fehlschlägen gibt es Screenshot + Trace.
`ng test` (Karma/Jasmine) wäre keine Lösung der Aufgabe – das sind Unit-Tests der
Komponenten, kein E2E durch den ganzen Stack.

## Setup (`automation-testing/e2e-playwright/`)
- `playwright.config.js`: zwei `webServer`-Einträge – Spring Boot (8081) und
  `ng serve` (4200) werden automatisch gestartet, ein `npm test` genügt.
- `workers: 1`, weil sich die Tests eine h2-Datenbank teilen.

## Die 2 Testfälle
Bewusst nur die zwei wichtigsten User-Flows (Testpyramide: wenige E2E-Tests,
die Breite decken Unit-/API-Tests ab):

| Test | Prüft |
|---|---|
| Studentenliste | `/students` zeigt die geseedeten Studenten in der Tabelle |
| Student erfassen | Formular ausfüllen → Submit → Redirect → neuer Student in der Liste (Roundtrip GUI → POST → DB → GET → GUI) |

Stolperfalle, die ich gefunden habe: `getByRole('cell', { name: 'Jonas' })` schlug
fehl, weil Playwright Namen als Substring matcht – «Jonas» traf auch die Email-Zelle
`jonas@tbz.ch` (strict mode violation). Fix: `exact: true`. Der strikte Modus
verhindert genau die mehrdeutigen Selektoren, die E2E-Tests flaky machen.

## Ausführen
```bash
cd automation-testing/e2e-playwright
npm install && npx playwright install chromium   # einmalig
npm test                                          # 2 passed
```

## Einordnung
System-/E2E-Level: nichts gemockt, getestet wie ein Benutzer durch den Browser –
dafür langsam und brüchig, deshalb nur für die kritischen Flows.
