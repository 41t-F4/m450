# TDD – Projekt: Einkaufsliste mit Red / Green / Refactor

**Code:** `code-reviews/recipe-planner/recipe-planner-backend/src/{main,test}/java/ch/tbz/recipe/planner/{service,controller}/ShoppingList*`
**Frontend:** `code-reviews/recipe-planner/recipe-planner-fronend/src/components/Planer/Planer.js`
**Auftrag:** `projekt/tdd.md` – TDD an einem eigenen Feature anwenden (Red → Green → Refactor, iterativ).

## Feature-Wahl: Einkaufsliste aus mehreren Rezepten

Der `Planer` im Frontend ist bisher ein leerer Platzhalter. Das fachlich naheliegende Feature dazu:
aus den geplanten Rezepten eine **Einkaufsliste** erzeugen, in der gleiche Zutaten zusammengefasst
und die Mengen summiert sind.

Warum gerade dieses Feature für TDD:

| Kriterium (Theorie) | Einkaufsliste |
|---|---|
| **Business-Logik** statt Glue-Code | Zusammenfassen + Summieren ist echte Logik; ein Delete-Endpoint wäre nur Durchreichen |
| **Reine Unit-Tests, schnelle Feedbackschleife** | Service-Methode braucht weder Spring noch DB → 6 Tests in 0.02 s |
| **Randfälle** vorhanden | leere Liste, gleiche Zutat in verschiedenen Einheiten, `ingredients == null` |
| **Iterativ aufbaubar** | jede Regel ist ein eigener kleiner Zyklus, das Design (Rückgabetyp, Schlüssel) entstand aus den Tests |

Schnittstelle (nur das Nötigste vorab festgelegt, der Rest kam aus den Tests):

```java
List<Ingredient> createShoppingList(List<Recipe> recipes)          // reine Logik, Zyklus 1–5
List<Ingredient> createShoppingListForRecipeIds(List<UUID> ids)   // lädt via Repository, Zyklus 6
POST /api/shopping-list  Body: ["<uuid>", …]  →  200 + Zeilen      // Zyklus 7
```

`Ingredient` wird als Zeile der Einkaufsliste wiederverwendet (`id`/`comment` bleiben `null`) –
*simplest thing that could possibly work*. Ein eigener Typ `ShoppingListItem` wäre der nächste
Refactor-Kandidat, war aber für den Scope nicht nötig.

Bewusst **nicht** im Scope (KISS): Einheiten umrechnen (1000 g = 1 kg), Schreibweisen normalisieren
(«Tomate» vs. « tomate »), Mengen pro Personenzahl skalieren.

## Protokoll der Zyklen (ein Zyklus = ein Test = eine Regel)

Reihenfolge nach Best Practice 3: **mit dem einfachsten Fall beginnen**, Randfälle danach.
Schnelle Schleife: `./mvnw -q test -Dtest=ShoppingListServiceTest`.

| # | Test (eine Regel) | Red – beobachtet | Green – minimaler Code | Refactor |
|---|---|---|---|---|
| 1 | `emptyInput_returnsEmptyList` | **Compile-Fehler** `cannot find symbol: class ShoppingListService` | Klasse anlegen, `return List.of()` | – |
| 2 | `oneRecipeOneIngredient_isPassedThrough` | `Expecting [] to contain exactly [("Tomate", PIECE, 5)]` | doppelte `for`-Schleife, Zutaten kopieren | – |
| 3 | `sameIngredientInTwoRecipes_amountsAreSummed` | `Expecting [("Tomate", PIECE, 5), ("Tomate", PIECE, 3)] to contain exactly [("Tomate", PIECE, 8)]` | `LinkedHashMap` mit Schlüssel `name + "\|" + unit`, Mengen addieren | – |
| 4 | `sameNameDifferentUnit_staysSeparate` | **war sofort grün** – der Schlüssel aus #3 enthielt die Einheit schon | nichts | – |
| 5 | `recipeWithoutIngredients_isIgnored` | `NullPointerException: Cannot invoke "List.iterator()" because getIngredients() is null` | `null`-Guard (`continue`) | `keyOf`, `toShoppingListLine`, `sumAmounts` extrahiert, `Map.merge` statt get/put → alle 5 grün |
| 6 | `forRecipeIds_loadsKnownRecipes_ignoresUnknownId` | Compile-Fehler: Konstruktor `(mapper, repository)` und Methode fehlen | Konstruktor-Injection, `findById` → `Optional::stream` → Mapper → `createShoppingList` | Test-Setup auf `@ExtendWith(MockitoExtension)` + `@BeforeEach` |
| 7 | `ShoppingListControllerTest.postsRecipeIds_returnsOkAndLines` | Compile-Fehler `class ShoppingListController` | `@PostMapping("/api/shopping-list")`, `@RequestBody List<UUID>` | – |

**Entscheid in #6:** unbekannte Ids werden **ignoriert** (Liste aus den gefundenen Rezepten) statt 404.
Begründung: Ein Planer mit einem gelöschten Rezept soll trotzdem eine Liste liefern; der Test hält den Entscheid fest.

### Test Levels im Feature

| Test-Klasse | Level | Doubles | Laufzeit |
|---|---|---|---|
| `ShoppingListServiceTest` #1–5 | Unit (reine Logik, kein Spring) | keine | 0.02 s |
| `ShoppingListServiceTest` #6 | Unit mit Schnittstelle | `@Mock RecipeRepository` (Mockito), Mapper echt via `Mappers.getMapper` | |
| `ShoppingListControllerTest` #7 | Komponententest Web-Slice | `@WebMvcTest` + `@MockBean ShoppingListService` (wie `RecipeControllerTest`) | ~1 s |

## Einbindung im Frontend (Planer)

Der bisher leere `Planer` nutzt jetzt den Endpoint: Rezepte per Checkbox wählen → **«Einkaufsliste erstellen»**
→ Tabelle mit Zutat / Menge / Einheit. Die Komponente enthält **keine** Logik (kein Summieren im Browser) –
alles, was fachlich schiefgehen kann, liegt im Backend und ist dort per TDD abgedeckt. Darum gibt es für den
Planer bewusst keinen eigenen Test (weniger, gezielte Tests); die Kontrolle ist die Live-Demo.

Verifiziert gegen die laufende App (Pasta: Tomate 5 Stk., Mehl 500 g · Salat: Tomate 3 Stk., Mehl 1 kg):

| Zutat | Menge | Einheit | Regel |
|---|---|---|---|
| Tomate | 8 | PIECE | Zyklus 3 – summiert |
| Mehl | 500 | GRAMM | Zyklus 4 – andere Einheit bleibt getrennt |
| Mehl | 1 | KILOGRAMM | Zyklus 4 |

## Stolpersteine (selbst erlebt)

- **Zyklus 4 war nie rot.** Der Test hat nichts «getrieben», weil der Green-Schritt von #3 die Einheit
  bereits in den Schlüssel genommen hatte. Behalten als Absicherung gegen eine spätere «Vereinfachung»
  des Schlüssels – aber bewusst so dokumentiert: ein nie-roter Test ist kein TDD-Schritt.
- **Compile-Fehler ist auch Red.** Bei #1, #6 und #7 scheiterte schon `testCompile`. Das fühlt sich erst
  komisch an (die IDE ist rot), ist aber genau das Mantra: kein Code ohne vorher fehlschlagenden Test.
- **Konstruktor-Änderung in #6** brach die Tests #1–5 beim Kompilieren (`new ShoppingListService()`).
  Die Lösung war ein Refactor im Test (`@BeforeEach` mit Mock + echtem Mapper), nicht in der Logik –
  Refactor gilt für Code **und** Tests.
- `Map.merge` statt `get`/`put`: Lombok-`@Data` macht `Ingredient` veränderbar, darum summiert
  `sumAmounts` in-place auf der Kopie, die `toShoppingListLine` erzeugt – das Original-Rezept bleibt unverändert.

## Resultat

```
./mvnw test
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0 – BUILD SUCCESS
Instruction Coverage: 82 % (601 von 733), Branch 63 %     (vorher 78 %, 11 Tests)
ShoppingListService 106/106, ShoppingListController 12/12 Instruktionen
```

Die 100 % auf den neuen Klassen sind kein Ziel, sondern eine **Folge** von TDD: Es gibt keine Zeile,
die nicht wegen eines roten Tests geschrieben wurde.

## Reflexion TDD

- **Der rote Test hat das Design getrieben:** Ohne #3 hätte ich die Einkaufsliste vermutlich direkt mit
  einer Map pro Name gebaut – und #4 (gleicher Name, andere Einheit) wäre ein Bug geworden. Weil ich #3
  als konkretes Beispiel mit Einheiten schreiben musste, war die Einheit automatisch im Schlüssel.
- **Minimal-Green ist ungewohnt:** `return List.of()` in #1 wirkt sinnlos, zwingt aber dazu, die nächste
  Regel als Test zu formulieren, statt «schon mal alles» zu programmieren. Die doppelte Schleife aus #2
  wurde in #3 komplett ersetzt – das ist normal, nicht Verschwendung.
- **Refactor nur mit grünem Netz:** Das Extrahieren von `keyOf`/`sumAmounts` und der Wechsel auf
  `Map.merge` nach #5 war in zwei Minuten erledigt, weil fünf Tests sofort gesagt hätten, wenn ich
  etwas kaputt gemacht hätte.
- **Vergleich FancyApp vs. Projekt:** In der FancyApp waren die Tests vorgegeben, ich musste nur
  «grün machen». Hier war das Schwierige, die Regel **vor** dem Code als Assertion zu formulieren –
  das ist der eigentliche Denk-Schritt von TDD, und er zwingt dazu, Entscheide (unbekannte Id ignorieren
  statt 404) explizit zu machen.
- **Berufsalltag:** Für reine Logik (Service, Berechnungen, Parser) würde ich TDD so anwenden. Für
  Controller und Glue-Code (#7) ist der Mehrwert klein: Der Test ist länger als der Code und prüft
  hauptsächlich Framework-Verhalten. Dort reicht ein Test nach dem Code als Absicherung.
