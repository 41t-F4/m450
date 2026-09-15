package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Aufgabe 1: Standard-Verhalten des korrekt implementierten Comparators
 * (Sortierung nach Nachname, dann Vorname, case-insensitive).
 */
class AddressComparatorTest {

    private AddressComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new AddressComparator();
    }

    private Address address(String firstname, String lastname) {
        return new Address(0, firstname, lastname, "000", new Date());
    }

    @Test
    @DisplayName("Kleinerer Nachname kommt zuerst")
    void compare_smallerLastname_comesFirst() {
        Address arnold = address("Zoe", "Arnold");
        Address zwahlen = address("Adam", "Zwahlen");

        assertTrue(comparator.compare(arnold, zwahlen) < 0);
        assertTrue(comparator.compare(zwahlen, arnold) > 0);
    }

    @Test
    @DisplayName("Gleicher Nachname: Vorname entscheidet")
    void compare_sameLastname_firstnameDecides() {
        Address anna = address("Anna", "Muster");
        Address beat = address("Beat", "Muster");

        assertTrue(comparator.compare(anna, beat) < 0);
        assertTrue(comparator.compare(beat, anna) > 0);
    }

    @Test
    @DisplayName("Identische Namen ergeben 0 (Konsistenz)")
    void compare_equalNames_returnsZero() {
        Address a1 = address("Anna", "Muster");
        Address a2 = address("Anna", "Muster");

        assertEquals(0, comparator.compare(a1, a2));
    }

    @Test
    @DisplayName("Gross-/Kleinschreibung wird ignoriert")
    void compare_isCaseInsensitive() {
        Address lower = address("anna", "muster");
        Address upper = address("ANNA", "MUSTER");

        assertEquals(0, comparator.compare(lower, upper));
    }

    @Test
    @DisplayName("Null-Nachname wird ans Ende sortiert")
    void compare_nullLastname_sortsLast() {
        Address mitName = address("Anna", "Muster");
        Address ohneName = address("Anna", null);

        assertTrue(comparator.compare(mitName, ohneName) < 0);
        assertTrue(comparator.compare(ohneName, mitName) > 0);
    }

    @Test
    @DisplayName("Eine Liste wird komplett korrekt sortiert")
    void sort_listOfAddresses_isOrderedByLastnameThenFirstname() {
        Address zwahlen = address("Adam", "Zwahlen");
        Address musterAnna = address("Anna", "Muster");
        Address musterBeat = address("Beat", "Muster");
        Address arnold = address("Zoe", "Arnold");

        List<Address> sorted = Stream.of(zwahlen, musterBeat, arnold, musterAnna)
                .sorted(comparator)
                .toList();

        assertEquals(List.of(arnold, musterAnna, musterBeat, zwahlen), sorted);
    }
}
