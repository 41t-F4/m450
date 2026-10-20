# CI/CD-Pipeline – Aufgabe 2: Reports

**Code:** `code-reviews/recipe-planner/recipe-planner-backend/pom.xml` (Plugin-Abschnitt)

## Was wird erzeugt?

| Tool | Woher | Was | Wo |
|---|---|---|---|
| **Surefire** | schon über den Spring-Boot-Parent dabei | Testresultate pro Testklasse (bestanden / fehlgeschlagen / Dauer) als XML + TXT | `target/surefire-reports/TEST-*.xml` |
| **JaCoCo** | neu als Plugin eingebunden | Code Coverage (Instructions, Branches, Lines, Methods, Classes) als HTML, XML und CSV | `target/site/jacoco/index.html` |

Beide entstehen **automatisch** bei jedem `./mvnw test`, niemand muss etwas manuell starten.

## JaCoCo-Konfiguration

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

- `prepare-agent` setzt die Maven-Property `argLine` auf `-javaagent:...jacoco-agent.jar`. Surefire
  startet die Test-JVM mit diesem Agent; er instrumentiert die Klassen zur Laufzeit und zählt, welche
  Bytecode-Instruktionen ausgeführt werden → `target/jacoco.exec`.
- `report` in der Phase `test` wandelt die `.exec`-Datei in den lesbaren Report um.

## Resultat lokal

```
./mvnw test
open target/site/jacoco/index.html
```

| Metrik | Wert |
|---|---|
| Instruction Coverage | 78 % (483 von 615) |
| Branch Coverage | 56 % |
| Tests | 11 / 11 grün |

Nicht abgedeckt sind hauptsächlich `RecipePlannerApplication` (Seed-Logik im `CommandLineRunner`)
und `RecipeService` – der Service wurde im Controller-Test bewusst gemockt. Das ist ein guter
Hinweis, wo der nächste Test sinnvoll wäre (Service mit gemocktem Repository, Block 05-Stil).

## Warum Surefire + JaCoCo und nicht nur eines?

- Surefire beantwortet «**sind die Tests grün?**» → das braucht die Pipeline, um rot/grün zu entscheiden.
- JaCoCo beantwortet «**wie viel Code wird von den Tests überhaupt durchlaufen?**» → Qualität der Testsuite,
  nicht des Codes. 78 % Coverage heisst nicht 78 % korrekt (siehe Grundlagen: Coverage ≠ Fehlerfreiheit).
