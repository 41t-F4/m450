# Schnittstellen – Aufgabe 1: Tests für das Addressbook-Backend

## Aufgabenstellung
- Das `addressbook-backend` (Java 21, Spring Boot) in Betrieb nehmen
- Tests für alle Klassen schreiben (inkl. zusätzlicher Annotationen wie `@BeforeEach`)
- Mit dem Testen von Adressen beginnen, die man erstellt
- Den Service testen, indem die h2-Datenbank «weg-gemockt» wird
- Die Comparator-Klasse korrekt implementieren

## Inbetriebnahme

```bash
cd schnittstellen/addressbook-backend
./mvnw spring-boot:run
```

Smoke-Test der REST-Schnittstelle (verifiziert):

| Request | Resultat |
|---|---|
| `POST /address` mit JSON-Body | **201** + gespeicherte Adresse |
| `GET /address` | **200** + Liste aller Adressen (sortiert) |
| `GET /address/99` (unbekannte Id) | **404** |

**Anpassungen an der Vorgabe für den lokalen Build (JDK 25):**
1. Lombok von 1.18.38 auf **1.18.42** angehoben (1.18.38 unterstützt JDK 25 nicht).
2. Lombok explizit als **Annotation-Processor** beim `maven-compiler-plugin` registriert:
   Ab **JDK 23** führt `javac` Annotation-Prozessoren vom Classpath nicht mehr implizit aus –
   ohne diesen Eintrag generiert Lombok keine Getter/Setter und der Build schlägt fehl,
   sobald jemand einen Getter aufruft. (In der Vorgabe fiel das nie auf, weil der
   Original-Code nirgends einen Getter verwendet hat.)

## Architektur des Testobjekts

```
AddressController  --HTTP-->  AddressService  -->  AddressRepository (JPA)  -->  h2 (in-memory DB)
                                    |
                                    +--> AddressComparator (Sortierung für getAll)
```

## Comparator korrekt implementieren

Die Vorgabe war absichtlich falsch (`return -1;` – verletzt sogar den Comparator-Kontrakt,
weil `compare(a,b)` und `compare(b,a)` beide negativ wären). Korrekte Implementation:
**Nachname, dann Vorname**, alphabetisch und case-insensitive, Null-Werte ans Ende
(`addressbook-backend/src/main/java/ch/tbz/m450/util/AddressComparator.java`).

## Die Tests (23 Tests, alle grün)

| Testklasse | Was wird getestet | Technik |
|---|---|---|
| `AddressTest` (2) | Erstellen von Adressen: beide Lombok-Konstruktoren, Getter/Setter | `@BeforeEach`, `assertAll` |
| `AddressComparatorTest` (6) | Standard-Sortierung: Nachname vor Vorname, case-insensitive, Symmetrie (a<b ⇔ b>a), null ans Ende, komplette Listen-Sortierung | reine Unit-Tests |
| `AddressServiceTest` (5) | `save` delegiert, `getAll` sortiert, leere DB, `getAddress` gefunden/nicht gefunden | **Mockito**: `@Mock AddressRepository` |
| `AddressControllerTest` (4) | Statuscodes 201/200/404 und Delegation an den Service | **Mockito**: `@Mock AddressService` |

### Kernidee: die h2-DB weg-mocken

Der Service spricht die Datenbank nie direkt an, sondern über die Schnittstelle
`AddressRepository`. Genau dort setzen wir das Test-Double an:

```java
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;   // ersetzt die echte h2-DB

    @BeforeEach
    void setUp() {
        addressService = new AddressService(addressRepository);
    }

    @Test
    void getAll_returnsAddressesSortedByComparator() {
        // Stub: vordefinierte Antwort statt DB-Zugriff (State Testing)
        when(addressRepository.findAll()).thenReturn(List.of(zwahlen, wehrli, arnold));

        List<Address> result = addressService.getAll();

        assertEquals(List.of(arnold, wehrli, zwahlen), result);  // sortiert!
        // Mock: Interaktion verifizieren (Behavioral Testing)
        verify(addressRepository).findAll();
    }
}
```

Begriffe (Theorie-Bezug):
- `when(...).thenReturn(...)` = **Stub**-Aspekt: das Double *liefert Daten* an das SUT,
  der Test prüft danach den **Zustand** (State Testing).
- `verify(...)` = **Mock**-Aspekt: der Test prüft, ob das SUT **richtig mit dem
  Kollaborateur interagiert** hat (Behavioral Testing).
- Mockito vereint beides in einem Framework («Enten-Syndrom»: kann schwimmen, fliegen, laufen).

**Warum mocken statt echte DB?** Der Test bleibt ein isolierter, schneller Unit-Test:
keine DB starten, keine Testdaten pflegen, kein Nebeneffekt zwischen Tests – und wir testen
*nur* die Logik des Service, nicht JPA/h2.

## Ausführen

```bash
cd schnittstellen/addressbook-backend
./mvnw test        # 23 Tests, 0 Failures, BUILD SUCCESS
```
