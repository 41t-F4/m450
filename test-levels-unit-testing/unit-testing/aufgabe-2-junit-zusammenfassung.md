# Unit Testing – Aufgabe 2: JUnit 5 Zusammenfassung

Die gängigsten JUnit-5-Features mit kurzen Anwendungsfällen.

**Referenz-Seite meiner Wahl:** [JUnit 5 User Guide (offiziell)](https://junit.org/junit5/docs/current/user-guide/) – ergänzend das [Vogella JUnit Tutorial](https://www.vogella.com/tutorials/JUnit/article.html).

## Architektur (gut zu wissen)

JUnit 5 = **Platform** (startet Tests, IDE-/Build-Integration) + **Jupiter** (neues API & Programmiermodell) + **Vintage** (führt alte JUnit-4-Tests weiter aus).

## Basis-Annotationen

| Annotation | Zweck / Beispiel |
|---|---|
| `@Test` | Markiert eine Methode als Testfall. Keine Parameter nötig, muss nicht mehr `public` sein. |
| `@BeforeEach` / `@AfterEach` | Setup/Teardown **vor bzw. nach jedem** Test – z. B. frisches Testobjekt erstellen, damit Tests isoliert bleiben. |
| `@BeforeAll` / `@AfterAll` | Einmalig vor/nach **allen** Tests der Klasse (statisch) – z. B. teure Ressourcen wie eine Testdatenbank starten. |
| `@DisplayName("…")` | Lesbarer Testname im Report: `@DisplayName("Division durch 0 wirft Exception")`. |
| `@Disabled("Grund")` | Test temporär überspringen – der Grund gehört immer dazu. |
| `@Tag("slow")` | Tests gruppieren und z. B. im Build gezielt ein-/ausschliessen. |
| `@Nested` | Innere Testklassen zum thematischen Gruppieren (z. B. eine Nested-Klasse pro getesteter Methode). |

## Assertions (statisch aus `org.junit.jupiter.api.Assertions`)

| Assertion | Anwendungsfall |
|---|---|
| `assertEquals(expected, actual)` | Standard-Vergleich; **bei double immer mit Delta**: `assertEquals(0.3, calc.add(0.1, 0.2), 1e-9)` |
| `assertTrue(…)` / `assertFalse(…)` | Boolesche Bedingungen: `assertFalse(konto.withdraw(1, -50))` |
| `assertNull(…)` / `assertNotNull(…)` | z. B. `assertNull(bank.createSalaryAccount(+10000))` bei ungültigem Parameter |
| `assertThrows(Exception.class, () -> …)` | Erwartete Exceptions: `assertThrows(ArithmeticException.class, () -> calc.divide(1, 0))` |
| `assertAll(…)` | Mehrere Assertions zusammenfassen – alle werden ausgeführt und gemeinsam rapportiert, statt beim ersten Fehler abzubrechen |
| `assertArrayEquals` / `assertIterableEquals` | Arrays/Collections elementweise vergleichen |
| `assertTimeout(Duration, () -> …)` | Scheitert, wenn der Code zu lange läuft |
| `fail("…")` | Test explizit fehlschlagen lassen (z. B. Platzhalter für ToDo) |

Alle Assertions akzeptieren optional eine **Message** als letzten Parameter: `assertEquals(5000, konto.getBalance(), "Saldo nach Einzahlung falsch")`.

## Parametrisierte Tests (`junit-jupiter-params`)

Gleiche Testlogik mit vielen Eingaben – perfekt für **Grenzwerte und Äquivalenzklassen**:

```java
@ParameterizedTest(name = "add({0}, {1}) = {2}")
@CsvSource({
    "0.0,  0.0,  0.0",
    "1.5,  2.5,  4.0",
    "-2.0, -3.0, -5.0"
})
void addParameterized(double a, double b, double expected) {
    assertEquals(expected, calculator.add(a, b), 1e-9);
}
```

Quellen: `@ValueSource` (einzelne Werte), `@CsvSource` (mehrere Spalten), `@MethodSource` (komplexe Objekte aus einer Factory-Methode), `@EnumSource`.

## Weitere nützliche Features

- **`@RepeatedTest(10)`** – Test mehrfach ausführen (z. B. Flakiness/Zufall).
- **Dynamische Tests** (`@TestFactory` + `DynamicTest`) – Testfälle zur Laufzeit erzeugen, wenn die Fälle erst aus Daten entstehen.
- **Assumptions** (`assumeTrue(…)`) – Test nur unter bestimmten Bedingungen ausführen (z. B. nur auf CI); sonst wird er übersprungen statt rot.
- **Conditional Execution** – `@EnabledOnOs(MAC)`, `@EnabledIfEnvironmentVariable(…)` usw.
- **Extension Model** (`@ExtendWith`) – ersetzt Runner/Rules aus JUnit 4; so integrieren sich z. B. Mockito oder Spring in Tests.
- **`@TempDir`** – stellt ein temporäres Verzeichnis bereit, das automatisch aufgeräumt wird.

## Ausführung

- **IDE:** grüner Pfeil neben Klasse/Methode (IntelliJ), Resultate im Test-Runner-Fenster inkl. Coverage («Run with Coverage»).
- **Maven:** `mvn test` (Surefire-Plugin führt alle `*Test`/`*Tests`-Klassen aus) – so laufen die Tests auch in jeder CI/CD-Pipeline.
