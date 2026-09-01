# Test Levels – Aufgabe 1: Testing in unserer Firma

> Diskussion: Was wird wie in Ihrer Firma getestet?

*Erfahrungsbericht aus meinem Lehrbetrieb (Softwareentwicklung, agile Projektteams).*

## Mit welchen Test Levels hatte ich bereits zu tun?

| Level | In der Firma | Meine Berührung |
|---|---|---|
| **Unit Testing** | Standard in jedem Projekt; Tests liegen im gleichen Repo wie der Code (z. B. JUnit für Java, entsprechende Frameworks je nach Sprache) | Ja – eigene Unit-Tests zu meinem Code schreiben ist Teil jeder Aufgabe |
| **Component/Integration Testing** | Backend-Tests, die mehrere Komponenten zusammen testen; externe Schnittstellen (DB, APIs) werden je nach Test gemockt oder in einer Testumgebung echt angesprochen | Teilweise – bestehende Integrationstests erweitern |
| **System Testing** | Auf einer Test-/Staging-Umgebung, die möglichst nah an der Produktion ist; vor Releases werden die wichtigsten Abläufe geprüft | Beobachtet / punktuell mitgetestet |
| **Acceptance Testing** | Der Kunde bzw. Product Owner prüft am Ende eines Sprints/Releases, ob die Akzeptanzkriterien der Storys erfüllt sind | Indirekt – Akzeptanzkriterien in Storys umsetzen |

## Wann werden Tests ausgeführt?

- **Bei jedem Push / Merge Request:** Unit- und Component-Tests laufen automatisch in der CI-Pipeline. Ein roter Build blockiert den Merge.
- **Beim Review:** Code Reviews sind Pflicht – dabei wird auch geprüft, ob sinnvolle Tests vorhanden sind.
- **Vor einem Release:** Integrations-/Systemtests auf der Staging-Umgebung, teils automatisiert, teils manuell nach Testprotokoll.
- **Nach dem Deployment:** Smoke-Tests, ob die Kernfunktionen auf der Zielumgebung laufen.

## Haben wir dedizierte Testing-/QA-Teams?

Kein grosses zentrales QA-Team: Die **Entwickler sind selbst für Unit-, Component- und die meisten Integrationstests verantwortlich** («you build it, you test it»). In grösseren Projekten gibt es einzelne Personen mit Testing-/QA-Fokus, und der Kunde ist beim Abnahmetest stark eingebunden. Das entspricht dem modernen Ansatz aus der Theorie: Integrationstests werden heute oft auch von Entwicklern geschrieben und laufen im Build.

## Wie sieht unser Testing Life Cycle aus?

1. Story mit **Akzeptanzkriterien** wird definiert (daraus ergeben sich die Testfälle)
2. Entwickler implementiert Feature **plus Unit-/Component-Tests**
3. **CI-Pipeline** führt alle Tests beim Push automatisch aus
4. **Code Review** (inkl. Blick auf die Tests) → Merge
5. Deployment auf **Testumgebung** → Integrations-/Systemtests
6. **Abnahme** durch PO/Kunde anhand der Akzeptanzkriterien
7. Release auf Produktion → Smoke-Tests, Monitoring

## Fazit / Bezug zur Theorie

Unsere Praxis bestätigt die Theorie-Pyramide: **je tiefer das Level, desto mehr und desto schnellere Tests** (viele Unit-Tests in Sekunden, wenige manuelle Systemtests vor Releases). Unit/Component sind Sache der Entwickler (White-Box), System-/Abnahmetests laufen Black-Box durch Tester bzw. Kunde.
