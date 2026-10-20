# TDD – Aufgaben 1–3: FancyApp mit PHPUnit

Kernidee der Übung: **Die Tests sind die Anforderungen.** Es gibt keine andere
Spezifikation – was die drei Methoden tun sollen, steht nur in den Assertions
von `Testcases/Test.php`.

## Aufgabe 1 – Setup
PHP ist bei mir nicht installiert → Docker mit `php:8.3-cli` + PHPUnit-PHAR
(reproduzierbar ohne lokales Setup, gleicher Gedanke wie der Maven-Wrapper):

```bash
cd tdd/fancy-app
curl -sSL -o phpunit-9.phar https://phar.phpunit.de/phpunit-9.phar   # einmalig
docker run --rm -v "$PWD":/app -w /app php:8.3-cli \
    php phpunit-9.phar --testdox Testcases/Test.php
```

In `Test.php` wie vorgesehen auf `extends TestCase` umgestellt und den
`require_once`-Pfad auf `__DIR__` abgestützt.

## Aufgabe 2 – Aus den Tests abgeleitete Anforderungen

| Methode | Verhalten laut Assertions |
|---|---|
| `shortString($text, $length, $ending='...')` | kürzt auf max. `$length` Zeichen inkl. `$ending`; passt der Text → unverändert; `$ending` länger als `$length` → `false` |
| `calcAverage($values)` | Durchschnitt; leeres Array → `0`; numerische Strings zählen; ein nicht-numerischer Wert → `false` |
| `getOpposite($value, $delimiter=',')` | Array → String (implode), String → Array (explode), sonst `false`; doppelt angewendet = Original |

**Red → Green live erlebt:** Meine erste `shortString`-Version mit `strlen` war rot –
der Test-String enthält ein «ä», das in UTF-8 2 Bytes hat. `strlen` zählt Bytes,
der Test erwartet Zeichen → Fix mit `mb_strlen`/`mb_substr`. Der rote Test hat den
Bug präzise lokalisiert, manuell wäre das kaum aufgefallen.

Resultat: `OK (3 tests, 27 assertions)`.

## Aufgabe 3 – Auswertung
Vergleich mit den naheliegenden Implementations-Varianten: Die Testsuite ist so eng,
dass alle korrekten Lösungen **funktional identisch** sein müssen – jede echte
Abweichung (`strlen` statt `mb_strlen`, fehlende `is_numeric`-Prüfung, falsche
Reihenfolge der Checks) wird sofort als roter Test sichtbar. Unterscheiden kann
sich nur der Stil. Genau das meint «die Testsuite ist die Spezifikation».
