# Schnittstellen – Aufgabe 2: Comparator erweitern

## Aufgabenstellung
- Die Comparator-Klasse so erweitern, dass nach **zusätzlichen Attributen** verglichen werden kann
- Die neue Funktionalität entsprechend testen

## Design der Erweiterung

`AddressComparator` bekommt ein Enum `SortAttribute` und einen Varargs-Konstruktor.
Die Attribute werden **der Reihe nach** verglichen – bei Gleichstand entscheidet das
nächste Attribut (mehrstufige Sortierung):

```java
public enum SortAttribute { LASTNAME, FIRSTNAME, PHONENUMBER, REGISTRATION_DATE, ID }

new AddressComparator()                                   // Default: LASTNAME, FIRSTNAME (Aufgabe 1)
new AddressComparator(SortAttribute.PHONENUMBER)          // nur nach Telefonnummer
new AddressComparator(SortAttribute.LASTNAME,
                      SortAttribute.FIRSTNAME,
                      SortAttribute.PHONENUMBER)          // mehrstufig
```

Entscheide:
- **Default-Konstruktor bleibt** wie in Aufgabe 1 (Nachname → Vorname): bestehender Code
  (`AddressService.getAll()`) funktioniert unverändert weiter – die Erweiterung ist abwärtskompatibel.
- Strings case-insensitive, Datum chronologisch, Id numerisch.
- **Null-Werte ans Ende** (defensiv, statt NullPointerException).
- Leere Attributliste → `IllegalArgumentException` (Fail Fast statt stiller Fehlkonfiguration).

## Die Tests (`AddressComparatorExtendedTest`, 6 Tests)

| Test | Prüft |
|---|---|
| `compare_byPhonenumber` | Sortierung nur nach Telefonnummer |
| `compare_byRegistrationDate` | chronologische Sortierung (älteste zuerst) |
| `compare_byId` | numerische Sortierung nach Id |
| `compare_multiLevel_nameThenPhonenumber` | Gleichstand beim Namen → Telefonnummer entscheidet, in beide Richtungen (Symmetrie) |
| `compare_nullRegistrationDate_sortsLast` | null ans Ende, null == null → 0 |
| `constructor_withoutAttributes_throws` | `assertThrows(IllegalArgumentException.class, ...)` |

Beispiel (mehrstufig):

```java
@Test
void compare_multiLevel_nameThenPhonenumber() {
    AddressComparator comparator = new AddressComparator(
            SortAttribute.LASTNAME, SortAttribute.FIRSTNAME, SortAttribute.PHONENUMBER);

    // arnold und arnoldZwilling heissen gleich -> Telefonnummer entscheidet
    assertTrue(comparator.compare(arnoldZwilling, arnold) < 0);
    assertTrue(comparator.compare(arnold, arnoldZwilling) > 0);
}
```

## Ausführen

```bash
cd schnittstellen/addressbook-backend
./mvnw test        # inkl. AddressComparatorExtendedTest, alle grün
```
