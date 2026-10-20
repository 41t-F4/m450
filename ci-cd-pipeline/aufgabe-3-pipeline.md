# CI/CD-Pipeline – Aufgabe 3: Build-Pipeline mit GitHub Actions

**Datei:** `.github/workflows/recipe-planner-backend.yml` (muss im Repo-Root unter `.github/workflows/` liegen)
**Pipeline-Läufe:** https://github.com/41t-F4/m450/actions

## Wahl: GitHub Actions statt GitLab CI

Das Repo liegt auf GitHub, darum GitHub Actions. Das Prinzip ist identisch mit dem GitLab-Beispiel
aus der Theorie, nur die Begriffe heissen anders:

| GitLab (Theorie) | GitHub Actions (meine Umsetzung) |
|---|---|
| Pipeline (`.gitlab-ci.yml`) | Workflow (`.github/workflows/*.yml`) |
| Stage (`stages: [build, test, deploy]`) | Job; Reihenfolge über `needs:` |
| Job (`script:`) | Step (`run:` oder `uses:`) |
| Runner (shared) | `runs-on: ubuntu-latest` (GitHub-hosted Runner) |
| `image: maven:latest` | `actions/setup-java@v4` mit Temurin 17 + Maven-Cache |
| `artifacts:` | `actions/upload-artifact@v4` |
| `artifacts: reports: junit:` | `dorny/test-reporter@v2` (zeigt Surefire-XML als Check an) |
| GitLab Pages | GitHub Pages (`actions/deploy-pages@v4`) |
| `$CI_PROJECT_DIR` | `${{ github.workspace }}`, `$GITHUB_STEP_SUMMARY` usw. |

## Aufbau: drei Stages wie in der Theorie

```
push ──▶ build ──▶ test ──▶ deploy-report (nur main)
           │          │            │
        JAR-Artefakt  Surefire+JaCoCo  JaCoCo-HTML auf GitHub Pages
                      als Artefakt +
                      Check + Summary
```

### Stage `build`
- JDK 17 (Modul-Vorgabe; lokal läuft JDK 25, darum Byte-Buddy/Lombok-Versionen im pom angehoben)
- `./mvnw --batch-mode -DskipTests package` → kompiliert und baut das JAR
- JAR wird 7 Tage als Artefakt aufbewahrt («Produkt» des Builds)

### Stage `test` (`needs: build`)
- `./mvnw --batch-mode test` → Surefire-XML + JaCoCo-Report (siehe Aufgabe 2)
- `dorny/test-reporter`: liest `target/surefire-reports/TEST-*.xml` und zeigt pro Test grün/rot direkt
  im GitHub-UI (Tab «Checks»). `if: always()` → auch bei roten Tests sichtbar, sonst wüsste man nicht, *welcher* Test failte.
- Coverage-Zeile in die **Job Summary**: ein kleines `awk` summiert `jacoco.csv` → Prozentzahl auf der Übersichtsseite des Laufs.
- Surefire + JaCoCo als Artefakt `test-reports` (30 Tage, herunterladbar) → **ein Report pro Pipeline-Durchlauf**

### Stage `deploy-report` (`needs: test`, nur `main`)
- Holt das Artefakt `test-reports` und publiziert `site/jacoco` auf GitHub Pages
- Einmalige Voraussetzung im Repo: **Settings → Pages → Source = «GitHub Actions»**
- Danach ist der aktuelle Coverage-Report unter `https://41t-f4.github.io/m450/` einsehbar

## Trigger

```yaml
on:
  push:              # Aufgabe 3: jeder Push triggert Build + Tests
  pull_request:
  workflow_dispatch: # manuell startbar
```

Bei einem grösseren Repo würde man mit `paths:` auf den Backend-Ordner einschränken, damit ein
Markdown-Commit keinen Java-Build auslöst. Hier bewusst nicht, damit die Pipeline für die Abgabe
bei jedem Push sichtbar läuft.

## Entscheide und Begründungen

| Entscheid | Warum |
|---|---|
| `working-directory` als Default | Das Backend liegt nicht im Repo-Root, sondern unter `code-reviews/recipe-planner/...` |
| `cache: maven` | Spart pro Lauf ca. 1 Minute Download der Dependencies |
| JDK 17 in CI | Modul-Vorgabe fürs Projekt; zeigt gleichzeitig, dass der Code nicht von JDK 25 abhängt |
| `--batch-mode` | Kein interaktiver Output / keine Download-Fortschrittsbalken im Log (wie `MAVEN_CLI_OPTS` in der Theorie) |
| drei getrennte Jobs statt einem | Entspricht den Stages der Theorie; man sieht sofort, *wo* es rot wurde (Build vs. Test) |
| `if: always()` bei Reports | Report gerade dann wichtig, wenn Tests rot sind |
| Pages nur ab `main` | Feature-Branches sollen den publizierten Report nicht überschreiben |

## Checkliste für die Abgabe

- [ ] Pipeline-Lauf grün unter Actions
- [ ] Tab «JUnit Tests» (Check) zeigt 11 Tests
- [ ] Artefakt `test-reports` herunterladbar
- [ ] Settings → Pages → Source «GitHub Actions», danach Coverage-Seite erreichbar
- [ ] Lehrperson hat Zugriff aufs Repo (öffentlich oder als Collaborator)
