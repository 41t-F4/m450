# Schnittstellen – Aufgabe 1: Tests für das Addressbook-Backend

## Inbetriebnahme

```bash
cd schnittstellen/addressbook-backend
./mvnw spring-boot:run
```

Smoke-Test: `POST /address` → 201, `GET /address` → 200 (sortierte Liste),
`GET /address/99` → 404.

**Anpassungen für den lokalen Build (JDK 25):** Lombok auf 1.18.42 angehoben und
explizit als Annotation-Processor beim `maven-compiler-plugin` registriert
(ab JDK 23 führt `javac` Prozessoren vom Classpath nicht mehr implizit aus).

## Comparator korrekt implementieren

Die Vorgabe war absichtlich falsch (`return -1;` – verletzt den Comparator-Kontrakt:
`compare(a,b)` und `compare(b,a)` wären beide negativ). Korrekt: **Nachname, dann
Vorname**, case-insensitive, Null-Werte ans Ende.

## Die Tests (11 Tests, alle grün)

Gezielte Kernfälle pro Klasse statt exhaustiver Abdeckung:

| Testklasse | Tests | Was |
|---|---|---|
| `AddressTest` | 1 | Erstellen einer Adresse (AllArgsConstructor, `assertAll`) |
| `AddressComparatorTest` | 4 | Nachname zuerst, Vorname bei Gleichstand, case-insensitive, komplette Listen-Sortierung |
| `AddressServiceTest` | 3 | `save` delegiert, `getAll` sortiert, unbekannte Id → `Optional.empty` – **h2 weg-gemockt** |
| `AddressControllerTest` | 3 | Statuscodes 201/200/404, Delegation an den Service (Service gemockt) |

### Kernidee: die h2-DB weg-mocken

Der Service spricht die DB nur über die Schnittstelle `AddressRepository` an –
genau dort setzt das Test-Double an:

```java
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;   // ersetzt die echte h2-DB

    @Test
    void getAll_returnsAddressesSortedByComparator() {
        when(addressRepository.findAll()).thenReturn(List.of(zwahlen, wehrli, arnold)); // Stub
        assertEquals(List.of(arnold, wehrli, zwahlen), addressService.getAll());        // State
        verify(addressRepository).findAll();                                            // Mock/Behavior
    }
}
```

- `when/thenReturn` = **Stub**: Double liefert Daten, Test prüft den **Zustand**.
- `verify` = **Mock**: Test prüft die **Interaktion** mit dem Kollaborateur.
- Warum mocken? Test bleibt isoliert und schnell: keine DB, keine Testdaten,
  getestet wird nur die Service-Logik, nicht JPA/h2.

## Ausführen

```bash
./mvnw test        # 15 Tests (inkl. Aufgabe 2), 0 Failures
```
