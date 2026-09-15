# Automation Testing – Übung 1: REST-Schnittstelle automatisiert testen

## Aufgabenstellung
- Die Spring-Boot-Applikation (`spring-boot-angular-basic-lw2`) lokal zum Laufen bringen
- Ein Tool/eine Möglichkeit finden, die REST-Schnittstelle des Backends **automatisiert** zu testen
- Lösung zeigen

## Das Testobjekt

Spring-Boot-Backend auf **Port 8081** (`server.port=8081`), h2 in-memory-DB,
beim Start werden 5 Studenten geseedet (Jonas, Patrick, Yves, Peter, Ann):

| Endpoint | Funktion |
|---|---|
| `GET /students` | alle Studenten als JSON-Array |
| `POST /students` | Student anlegen (JSON-Body mit `name`, `email`) |

Backend starten (fürs manuelle Ausprobieren – die automatisierten Tests starten es selbst):

```bash
cd automation-testing/spring-boot-angular-basic-lw2
./mvnw spring-boot:run
curl localhost:8081/students
```

## Tool-Wahl: REST Assured

Evaluiert habe ich drei Möglichkeiten:

| Tool | Ansatz | Beurteilung |
|---|---|---|
| **REST Assured** ✅ | Java-Bibliothek: API-Tests als JUnit-Tests im Maven-Build | Läuft bei jedem `./mvnw test` und damit automatisch in jeder CI-Pipeline; gleiche Sprache wie das Backend; kein zusätzliches Tool zu installieren |
| Postman + Newman | Requests im GUI zusammenklicken, Collection mit `newman` in CI ausführen | Gut für exploratives Testen; Automatisierung braucht aber Node/newman als Extra-Infrastruktur |
| curl + Shell-Skript | «kreative» Minimallösung | funktioniert, aber Assertions/Reports muss man selbst bauen |

Entscheid: **REST Assured**, weil die Tests so Teil des Builds sind – genau die Kernaussage
des Moduls zu Test-Automatisierung (wiederholbar bei jedem Inkrement, keine manuelle Arbeit).

## Die Lösung

`src/test/java/ch/tbz/m450/testing/tools/controller/StudentApiRestAssuredTest.java`

- `@SpringBootTest(webEnvironment = RANDOM_PORT)` startet das **echte Backend** inkl. h2
  auf einem zufälligen freien Port (kein Port-Konflikt, parallel ausführbar).
- `@BeforeEach` setzt `RestAssured.port` auf diesen Port.
- REST Assured feuert **echte HTTP-Requests** und prüft Status, Content-Type und JSON-Inhalt
  im lesbaren **given/when/then**-Stil (BDD):

```java
given()
        .contentType(ContentType.JSON)
        .body("""
                {"name": "Nicola Wehrli", "email": "nicola@tbz.ch"}
                """)
.when()
        .post("/students")
.then()
        .statusCode(200);

when()
        .get("/students")
.then()
        .statusCode(200)
        .body("name", hasItem("Nicola Wehrli"));
```

Die 3 Testfälle:

| Test | Prüft |
|---|---|
| `getStudents_returns200AndJsonArray` | Status 200, Content-Type JSON, Body ist Array |
| `postStudent_thenGetContainsStudent` | POST → 200, danach enthält GET den neuen Studenten (Roundtrip!) |
| `postStudent_assignsGeneratedId` | die DB vergibt eine generierte Id (`find { it.name == ... }.id` ist nicht null) |

Einordnung in die Test Levels: Das ist ein **API-/Integrationstest** – die Schichten
Controller → Repository → h2 werden **echt** durchlaufen (nichts gemockt), aber
automatisiert vom Entwickler ausführbar wie ein Unit-Test.

## Ausführen

```bash
cd automation-testing/spring-boot-angular-basic-lw2
./mvnw test        # 4 Tests (3 REST Assured + contextLoads), BUILD SUCCESS
```

## Anpassungen an der Vorgabe
1. `rest-assured` als Test-Dependency ergänzt (Version verwaltet Spring Boot).
2. Lombok-Version auf 1.18.42 angehoben + als Annotation-Processor beim
   `maven-compiler-plugin` registriert (ab JDK 23 laufen Prozessoren nicht mehr implizit;
   nötig für den Build mit lokalem JDK 25).
3. Fehlenden `.mvn`-Wrapper-Ordner ergänzt (war im Zip nicht enthalten).
