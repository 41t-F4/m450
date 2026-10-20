# Code Reviews – Aufgabe 2: Feature «Recipe editieren» (FE + BE) + Review

Gleiche Solo-Adaption wie in [Aufgabe 1](aufgabe-1-review-add-recipe.md):
Implementation + Review nach Checkliste.

## Das Feature
- **Backend:** `PUT /api/recipes/recipe/{recipeId}` neu (gab es nicht) –
  unbekannte Id → **404**; im Service gewinnt die Id aus dem Pfad (Body kann
  keine fremde Id überschreiben), `orphanRemoval` ersetzt die Zutatenliste sauber.
- **Frontend:** `EditRecipe` lädt per GET, zeigt das aus Aufgabe 1 **wiederverwendete
  `RecipeForm`** vorbefüllt und speichert per PUT; Edit-Button auf der Karte
  (vorher funktionslos) navigiert auf `/edit/{id}`; Route in `App.js`.

## Wichtigste Review-Findings

| Wo | Finding | Entscheid |
|---|---|---|
| BE | Erste Version gab bei unbekannter Id `200` mit `null` zurück – Muster unbewusst vom bestehenden `getRecipeById` kopiert | Gefixt: 404. Reviews verhindern, dass sich schlechte Muster fortpflanzen |
| BE | Altlast: Repository `CrudRepository<_, Long>` bei `UUID`-Ids; `addRecipe` gibt 200 statt 201 | Protokolliert, aber **nicht im Feature-PR gefixt** → eigener PR, kein Scope Creep |
| FE | `useEffect` ohne `[recipeId]`-Dependency → falsches Recipe bei Navigation zwischen zwei Edits | Gefixt |
| – | Lombok der Vorgabe inkompatibel mit JDK 25 → `lombok.version` 1.18.42 | Build-Fix |

## Erkenntnisse
- FE- und BE-Änderungen als **getrennte, kleine PRs** – kleine Diffs bekommen die
  besseren Reviews (Theorie: 200–400 Zeilen optimal).
- Findings ausserhalb des Feature-Scopes gehören ins Protokoll/Follow-up, nicht in den PR.
- Die Wiederverwendung (`RecipeForm`) entstand aus dem Review von Aufgabe 1 –
  Reviews verbessern auch die Architektur, nicht nur den einzelnen PR.

Verifikation: Backend gestartet; POST → PUT (ändert Name + ersetzt Zutaten) →
PUT auf unbekannte Id = 404 → GET zeigt den editierten Stand. Frontend-Build sauber.
