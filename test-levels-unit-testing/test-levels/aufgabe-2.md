# Test Levels – Aufgabe 2: Begriffe einordnen

> Versuchen Sie folgende Begriffe einzuordnen und Abhängigkeiten untereinander zu erkennen: Testing approach, Testing levels, Testing types/techniques/tactics.

## Einordnung

```
Testing Approach (Gesamtstrategie: WIE gehen wir grundsätzlich vor?)
│
├── Testing Levels (WO im Entwicklungsprozess wird getestet?)
│      Unit → Component → Integration → System → Acceptance
│
└── Testing Types, Techniques & Tactics (WAS und WOMIT wird auf jedem Level getestet?)
       Types:      funktional / nicht-funktional (Performance, Usability, Security…)
       Techniques: White-Box (Code-Pfade, Coverage) / Black-Box (Äquivalenzklassen, Grenzwerte)
       Tactics:    manuell vs. automatisiert, explorativ, Regression, Mocking…
```

### Testing Approach (oberste Ebene)

Der **Approach** ist die Gesamtstrategie des Testens – er gehört zur Teststrategie/zum Testkonzept eines Projekts. Er beantwortet Fragen wie: Testen wir **präventiv** (Tests früh, parallel zur Entwicklung, wie im V-Modell oder bei TDD) oder **reaktiv** (Tests nach der Implementierung)? Wie viel wird automatisiert? Welche Levels und Typen setzen wir überhaupt ein? → Der Approach **bestimmt** die Auswahl der Levels und Types/Techniques.

### Testing Levels (WO / WANN)

Die **Levels** ordnen das Testen den Stufen des Entwicklungsprozesses zu — von klein/schnell nach gross/langsam, aufeinander aufbauend:

1. **Unit Testing** – kleinste Einheiten isoliert (Entwickler, White-Box, Teil des Builds)
2. **Component Testing** – Zusammenspiel mehrerer Komponenten, externe Schnittstellen gemockt (Entwickler)
3. **Integration Testing** – echte Integrationen (DB, APIs) werden benutzt (klassisch QA, heute auch Entwickler)
4. **System Testing** – das Ganze auf produktionsnaher Umgebung (QA, Black-Box, funktional + nicht-funktional)
5. **Acceptance Testing** – Kunde prüft die Akzeptanzkriterien (Black-Box)

### Testing Types, Techniques & Tactics (WAS / WOMIT)

- **Types** beschreiben, *was für eine Eigenschaft* geprüft wird: funktionale Tests (das WAS der Software) und nicht-funktionale Tests (das WIE: Load-/Stress-Testing, Usability, Security).
- **Techniques** beschreiben, *wie Testfälle entworfen werden*: White-Box-Techniken (Code-Pfad-Testing, Coverage) und Black-Box-Techniken (Testfälle aus der Spezifikation, Äquivalenzklassen, Grenzwertanalyse).
- **Tactics** sind die *konkrete Umsetzung*: manuell oder automatisiert, explorativ, Regressionstests, Einsatz von Mocks/Test-Doubles, Testtreiber usw.

## Abhängigkeiten

- Der **Approach** (aus der Teststrategie) legt fest, **welche Levels** durchlaufen und **welche Types/Techniques/Tactics** eingesetzt werden.
- **Jedes Level kombiniert bestimmte Types und Techniques:** Unit-Tests sind typischerweise funktional + White-Box + automatisiert; Systemtests sind funktional und nicht-funktional + Black-Box; Abnahmetests funktional + Black-Box + oft manuell.
- Die Begriffe sind **orthogonal**: Ein Level sagt WO getestet wird, ein Type WAS geprüft wird, eine Technique WIE die Testfälle entstehen — ein konkreter Test ist immer eine Kombination aus allen dreien (z. B. «automatisierter funktionaler White-Box-Test auf Unit-Level»).
