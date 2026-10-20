# Code Reviews – Erkenntnisse aus den vier Pull Requests

Wir haben beide Features (Add Recipe, Edit Recipe) unabhängig implementiert und
uns gegenseitig reviewt. Vier PRs, zwei pro Person:

| PR | Autor | Umfang | Review-Ergebnis |
|---|---|---|---|
| 41t-F4/m450 #1 «Aufgabe 1» | ich | 7 Dateien, ~160 Zeilen | 4 Kommentare, 1 davon abgelehnt |
| 41t-F4/m450 #2 «Aufgabe 2» | ich | 9 Dateien, ~200 Zeilen | 2 Kommentare, 1 echter Bug |
| BusyMoreno/M450 #1 «Add Recipe» | Kollege | 2 Dateien, ~140 Zeilen | 3 Kommentare, 2 Critical |
| BusyMoreno/M450 #2 «Edit Recipe» | Kollege | 26 Dateien, 21'436 Zeilen | 4 Kommentare, 1 Blocker (PR-Hygiene) |

## 1. Was die Reviews gefunden haben

### Auf seinen PRs (ich als Reviewer)
- **Grenze Frontend/Backend:** Base-URL ohne `/api` (404) und Zutaten-Felder
  `ingredient`/`quantity` statt `name`/`amount`. Spring ignoriert unbekannte Felder
  stillschweigend, die Zutat wäre mit `name = null`, `amount = 0` gespeichert worden.
  **Kein Test hätte das gefunden**, weil weder Frontend- noch Backend-Tests die
  Schnittstelle dazwischen prüfen. Nur jemand, der beide Seiten liest, sieht es.
- **Nicht angebundene Komponente:** `AddIngredient` ohne `value`/`onChange`, Eingaben
  kamen nie in den State. Aufgefallen, weil die Datei **nicht** im PR war: Was fehlt,
  ist genauso ein Finding wie das, was da ist.
- **PR-Hygiene:** `target/`-Ordner mit 20 Build-Artefakten und absoluten Windows-Pfaden,
  dazu `package-lock.json` mit 21'049 Zeilen. 60 echte Änderungen in 21'436 Zeilen.
  So ein PR ist praktisch nicht reviewbar. Lösung: `.gitignore`, Lockfile als eigener Commit.
- **Design:** Edit als Modal in der Karte (124 Zeilen, vier Zuständigkeiten, Felder
  dupliziert zu `AddRecipe`) gegen meine Lösung mit eigener Route und gemeinsamem
  `RecipeForm`. Beides funktioniert, der Unterschied liegt in der Wartbarkeit.
- **Fehlende Tests** für den neuen PUT-Endpoint (Checkliste: «existieren Tests?»).

### Auf meinen PRs (er als Reviewer)
- **Echter Bug:** `Browse.js` übergab keine `id` an die Karte, der Edit-Button
  navigierte auf `/edit/undefined`. Er fand ihn, indem er **den Aufrufer** der
  geänderten Komponente las, nicht die geänderte Datei selbst. Mein eigener Test war
  ein curl-Aufruf gegen den PUT-Endpoint und lief an der UI vorbei.
- **SRP:** Ich hatte `AddIngredient` gelöscht und die Zeile in `AddRecipe` inlined.
  Berechtigt, umgesetzt: Zeilen-Komponente bleibt, neu über `register` angebunden.
- **Nit mit Folgen:** `parseInt` liefert bei leerem Feld `NaN`. Statt des vorgeschlagenen
  Fallbacks habe ich die Validierung an die Quelle gezogen (`valueAsNumber` plus
  «ganze Zahl ≥ 1»), damit der ungültige Wert gar nicht erst entsteht.
- **Falscher Kommentar:** «Endpoint ist `/recipes` ohne `/api`». Mit Verweis auf den
  Controller abgelehnt. Derselbe Irrtum steckte in seinem eigenen PR.
- **`alert()`** durch Fehler-State im UI ersetzt.

## 2. Was wichtig ist

1. **PR-Grösse entscheidet über die Review-Qualität.** Meine PRs mit 160 bis 200 Zeilen
   bekamen präzise Kommentare bis auf Zeilenebene. Sein 21'000-Zeilen-PR bekam zuerst
   einen Kommentar zur Grösse selbst. Theorie bestätigt: 200 bis 400 Zeilen.
2. **Reviews und Tests ergänzen sich, sie ersetzen sich nicht.** Die drei schwersten
   Funde (Feldnamen, Base-URL, `/edit/undefined`) lagen alle an einer Grenze zwischen
   zwei Teilen, die kein Unit-Test gemeinsam prüft.
3. **Den Aufrufer mitlesen.** Die geänderte Datei ist nur die halbe Wahrheit. Wer
   benutzt die Komponente? Stimmt der Vertrag (Props, Feldnamen, Pfade) auf beiden Seiten?
4. **Was nicht im Diff ist, zählt auch.** Fehlende Dateien, fehlende Tests, fehlendes
   `.gitignore`.
5. **Zwei korrekte Lösungen unterscheiden sich kaum in der Funktion, aber stark in der
   Wartbarkeit.** Modal-in-der-Karte gegen Route-mit-Formular: genau solche
   Design-Entscheide macht der Vergleich zweier PRs sichtbar.
6. **Review-Findings in Code giessen.** Das `RecipeForm`-Refactoring in Aufgabe 2 ist
   direkt aus dem Review von Aufgabe 1 entstanden.
7. **Selbst verifizieren, bevor man postet.** Jeden Critical-Kommentar habe ich vorher am
   Branch nachgeprüft (Controller-Mapping, `AddIngredient` ohne `onChange`). Ein
   falscher Critical kostet Vertrauen.

## 3. Wie man kommunizieren soll

Aus der Theorie («Soziale Aspekte eines Prüfers») und dem, was in der Praxis
funktioniert hat:

**Als Reviewer**
- **Schwere kennzeichnen:** `Critical` / `Major` / `Minor` / `Nit` am Anfang. Der Autor
  sieht sofort, was blockiert und was Geschmack ist.
- **Belegen statt behaupten:** Nicht «das ist falsch», sondern «der Controller mappt
  `/api/recipes`, siehe `RecipeController.java`». Ein Kommentar mit Beleg ist in
  einer Minute geprüft, einer ohne löst eine Diskussion aus.
- **Konsequenz nennen:** «Spring ignoriert unbekannte Felder, die Zutat landet mit
  `name = null` in der DB» erklärt, warum es Critical ist.
- **Vorschlag mitliefern:** Jeder Kommentar enthält einen konkreten nächsten Schritt,
  im Idealfall als Code-Zeile.
- **Geschmacksfragen als Vorschlag:** «mein Vorschlag wäre», «könnte man». Nicht jede
  Zeile nach eigenem Gusto umbiegen.
- **Positives explizit nennen.** Die Zusammenfassung beginnt mit dem, was gut ist
  (`isSubmitting`, `Alert` statt `alert()`, `key`-Prop). Das ist keine Höflichkeit,
  sondern Information: Der Autor weiss, was er beibehalten soll.
- **Ausserhalb des Scopes → Follow-up.** Repository mit falschem Id-Typ, POST liefert
  200 statt 201: protokolliert, aber nicht im Feature-PR verlangt.
- **Fragen statt unterstellen.** «Wird `id` tatsächlich durchgereicht?» war als Frage
  formuliert und hat trotzdem den Bug gefunden.

**Als Autor**
- **Jeden Kommentar beantworten**, auch den abgelehnten. Mit Beleg ablehnen ist
  erlaubt und erwünscht, schweigend ignorieren nicht.
- **Nicht jeden Vorschlag wörtlich übernehmen.** Aus «Fallback für NaN» wurde
  Validierung an der Quelle. Das Ziel des Kommentars zählt, nicht der Wortlaut.
- **Umsetzung dokumentieren:** Welcher Kommentar wurde wie umgesetzt, welcher bewusst
  nicht und warum (Tabellen in den beiden Aufgaben-Dateien).
- **Nicht persönlich nehmen.** Der Reviewer fand in meinem Code einen Bug, den ich
  mit curl «verifiziert» hatte. Das ist der Sinn des Vier-Augen-Prinzips.

**Was wir im Team geändert haben**
- `.gitignore` vor dem ersten Feature-Branch, nicht danach.
- Lockfiles und Dependency-Updates als eigener Commit.
- Vor dem Posten eines Critical den Befund am Branch nachprüfen.
- Bei Frontend-Änderungen einmal durch die UI klicken, nicht nur die API aufrufen.
