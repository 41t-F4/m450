# Code Reviews – Aufgabe 1: Feature «Recipe hinzufügen» + Review

Da ich alleine arbeite, habe ich die Team-Aufgabe adaptiert: **zwei Varianten**
desselben Features gebaut und gegeneinander reviewt (Checkliste aus der Theorie:
Naming, Logikfehler, DRY/KISS, Tests). Die nach dem Review verbesserte Variante
ist im Code (`recipe-planner/recipe-planner-fronend`).

## Ausgangslage
`AddRecipe.js` war in der Vorgabe ein totes Formular: Felder nicht angebunden,
Submit ohne Handler, kein POST.

## Die zwei Varianten
- **A (übernommen):** react-hook-form + `useFieldArray` für die Zutaten-Zeilen,
  Validierung via `register(..., {required})`, POST über zentrale `apis/recipeApi.js`.
- **B:** kontrollierte `useState`-Komponenten mit eigenem `listId`-Zähler
  (wie in der Vorgabe angelegt), axios-Call direkt in der Komponente.

## Wichtigste Review-Findings

| An | Finding | Schwere |
|---|---|---|
| B | Feldnamen passen nicht zum Backend (`quantity`/String statt `amount`/int) – wäre erst zur Laufzeit aufgefallen | Critical |
| B | `baseURL` hartcodiert, dieselbe URL schon in `Browse.js` → zentrale `recipeApi.js` | Major |
| B | manuelles `listId`-Handling fehleranfällig → `useFieldArray` macht das eingebaut | Major |
| A | tote Imports in `App.js`, fehlendes `key`-Prop in `Browse.js` | Minor (gefixt) |

## Review vom Kollegen auf meinem PR (41t-F4/m450 #1)

| Datei | Kommentar | Entscheid |
|---|---|---|
| `recipeApi.js` | Endpoint sei `/recipes` ohne `/api`, 404 möglich | **Nicht übernommen**, mit Beleg beantwortet: `RecipeController` mappt `/api/recipes`, und `Browse.js` nutzte den Pfad schon vorher. Lehre: Review-Kommentare sind Hypothesen, der Autor muss sie prüfen und begründet antworten dürfen |
| `AddIngredient.css` | Löschen der Komponente verletzt SRP, `AddRecipe` rendert wieder die ganze Zeile | **Übernommen**: `AddIngredient` bleibt als Zeilen-Komponente, neu über `register` von react-hook-form angebunden |
| `AddRecipe.js` | Nit: `parseInt` liefert bei leerem Feld `NaN` | **Übernommen**, aber anders als vorgeschlagen: statt Fallback `valueAsNumber` plus `validate` (ganze Zahl ≥ 1), damit kein `NaN` abgeschickt werden kann |
| `Browse.js` | Lob für Naming und zentrale API | – |

## Erkenntnisse
- Zwei korrekte Lösungen unterscheiden sich kaum in der Funktionalität, aber stark
  in der Wartbarkeit – genau solche Design-Entscheide macht ein Review sichtbar.
- Der Critical-Fund (Feldnamen) kam aus dem Review, nicht aus Tests – Reviews und
  Tests ergänzen sich.
- Kommentare konkret und als Vorschlag formulieren, nicht «das ist falsch».
- Nicht jeder Kommentar ist richtig: den URL-Kommentar habe ich mit Verweis auf den
  Controller abgelehnt. Begründet widersprechen gehört zum Review-Dialog.
- Ein Nit kann zu einem besseren Design führen: aus «Fallback für NaN» wurde
  Validierung an der Quelle, so dass der ungültige Wert gar nicht erst entsteht.

Verifikation: `npm run build` sauber; POST → GET Roundtrip gegen das laufende Backend geprüft.
