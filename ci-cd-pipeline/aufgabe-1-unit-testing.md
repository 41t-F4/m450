# CI/CD-Pipeline – Aufgabe 1: Unit Testing (Recipe-Planner-Backend)

**Code:** `code-reviews/recipe-planner/recipe-planner-backend/src/test/java/ch/tbz/recipe/planner/`
(das Backend liegt bereits für die Code-Review-Aufgaben im Repo und wird nicht dupliziert)

## Teil 1 – Controller-Methoden via MockMvc

Datei: `controller/RecipeControllerTest.java`

### Ansatz

- `@WebMvcTest(RecipeController.class)` lädt nur den **Web-Slice** von Spring: Controller, Routing,
  JSON-Konvertierung. Kein JPA, keine h2 → der Test läuft in ca. 1 Sekunde.
- `RecipeService` und `RecipeEntityMapper` werden mit `@MockBean` ersetzt (Test Doubles aus Block 05).
  Getestet wird nur die Verantwortung des Controllers: Pfad → Methode, JSON rein/raus, HTTP-Status.
- Given-When-Then: `when(service...)` → `mockMvc.perform(...)` → `andExpect(...)`.

### Testfälle

| # | Testfall | Request | Erwartung |
|---|---|---|---|
| 1 | `getRecipes_returnsOkAndList` | `GET /api/recipes` | 200, JSON-Array mit 1 Rezept, `unit` als String `GRAMM` |
| 2 | `getRecipe_byId_returnsOkAndRecipe` | `GET /api/recipes/recipe/{uuid}` | 200, `id` und `name` im JSON; `verify` dass die UUID korrekt geparst an den Service geht |
| 3 | `addRecipe_postsJsonBody_returnsCreatedRecipe` | `POST /api/recipes` + JSON ohne Id | 200, Antwort enthält die vom Service vergebene Id |
| 4 | `updateRecipe_existing_returnsOk` | `PUT /api/recipes/recipe/{uuid}` | 200 mit aktualisiertem Rezept |
| 5 | `updateRecipe_unknownId_returnsNotFound` | `PUT` auf unbekannte Id (Service gibt `null`) | **404** – der einzige Fehlerpfad im Controller |

Alle vier Controller-Methoden sind abgedeckt, `updateRecipe` mit beiden Pfaden (Grenzfall 404).

### Stolpersteine (selbst erlebt)

1. **Byte Buddy / JDK 25**: Mockito aus Spring Boot 3.2.1 konnte unter JDK 25 keine Mocks erzeugen
   («Java 25 (69) is not supported by the current version of Byte Buddy»). Fix: `byte-buddy.version`
   im `pom.xml` auf 1.17.7 anheben – gleiche Massnahme wie vorher schon bei Lombok. In der Pipeline
   läuft JDK 17 (Modul-Vorgabe), dort wäre es kein Problem gewesen.
2. **Seed-Bean in der Application-Klasse**: `RecipePlannerApplication` definiert
   `@Bean CommandLineRunner init(RecipeRepository)`. Die Application-Klasse wird auch im Web-Slice als
   Konfiguration geladen, das JPA-Repository aber nicht → «No qualifying bean of type RecipeRepository».
   Fix: `@MockBean RecipeRepository` im Test. Sauberer wäre, den Seed in eine eigene
   `@Configuration`-Klasse auszulagern – das wäre ein Code-Review-Hinweis ans Team.

## Teil 2 – Mapper mit SoftAssertions

Dateien: `mapper/IngredientEntityMapperTest.java`, `mapper/RecipeEntityMapperTest.java`

### Ansatz

- Die Mapper sind MapStruct-Interfaces; die Implementierung wird beim Kompilieren generiert.
  `Mappers.getMapper(IngredientEntityMapper.class)` holt sie **ohne Spring-Kontext** → reiner Unit-Test.
- Pro Mapper drei Tests: Entity→Domain (alle Felder), Domain→Entity (mit `null`-Grenzfall), ein
  Listen- bzw. Roundtrip-Test.

| Mapper | Testfall | Prüft |
|---|---|---|
| Ingredient | `entityToDomain_mapsAllFields` | id, name, comment, unit, amount |
| Ingredient | `domainToEntity_mapsAllFields` | alle Felder, `comment = null` bleibt `null` |
| Ingredient | `listMapping_keepsOrderAndSize` | Reihenfolge, Grösse, leere Liste |
| Recipe | `entityToDomain_mapsAllFieldsIncludingNestedIngredients` | Felder + verschachteltes Ingredient-Mapping |
| Recipe | `domainToEntity_mapsAllFields_andNullIngredientsStayNull` | `ingredients = null` wird nicht zu leerer Liste |
| Recipe | `roundTrip_domainToEntityToDomain_isLossless` | Domain → Entity → Domain ist `equals` zum Original |

### Warum SoftAssertions?

```java
SoftAssertions softly = new SoftAssertions();
softly.assertThat(domain.getName()).isEqualTo("Butter");
softly.assertThat(domain.getUnit()).isEqualTo(Unit.GRAMM);
softly.assertThat(domain.getAmount()).isEqualTo(250);
softly.assertAll(); // erst hier wird gemeldet
```

- **Harte Assertions** (`assertEquals`, `assertThat(...)`) brechen beim **ersten** Fehler ab. Wären
  drei von fünf Feldern falsch gemappt, sähe ich nur das erste, würde fixen, neu starten, das nächste sehen …
- **SoftAssertions** sammeln alle Fehler und melden sie bei `assertAll()` **gemeinsam**. Genau bei
  Mapper-Tests (viele unabhängige Feld-Prüfungen am gleichen Objekt) ist das der richtige Stil:
  ein Testlauf zeigt das vollständige Bild.
- Wichtig: `assertAll()` nicht vergessen, sonst ist der Test immer grün.

## Resultat

```
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0 – BUILD SUCCESS
```
