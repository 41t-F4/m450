package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.util.AddressComparator.SortAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Aufgabe 2: Der Comparator kann über den Varargs-Konstruktor nach
 * zusätzlichen Attributen vergleichen (Telefonnummer, Registrierungsdatum, Id),
 * inklusive mehrstufiger Sortierung.
 */
class AddressComparatorExtendedTest {

    private Address wehrli;
    private Address arnold;
    private Address arnoldZwilling;

    @BeforeEach
    void setUp() {
        wehrli = new Address(3, "Nicola", "Wehrli", "0790000002", new Date(2000));
        arnold = new Address(1, "Zoe", "Arnold", "0790000009", new Date(1000));
        arnoldZwilling = new Address(2, "Zoe", "Arnold", "0790000001", new Date(3000));
    }

    @Test
    @DisplayName("Sortierung nach Telefonnummer")
    void compare_byPhonenumber() {
        AddressComparator comparator = new AddressComparator(SortAttribute.PHONENUMBER);

        List<Address> sorted = Stream.of(wehrli, arnold, arnoldZwilling).sorted(comparator).toList();

        assertEquals(List.of(arnoldZwilling, wehrli, arnold), sorted);
    }

    @Test
    @DisplayName("Sortierung nach Registrierungsdatum (älteste zuerst)")
    void compare_byRegistrationDate() {
        AddressComparator comparator = new AddressComparator(SortAttribute.REGISTRATION_DATE);

        List<Address> sorted = Stream.of(wehrli, arnoldZwilling, arnold).sorted(comparator).toList();

        assertEquals(List.of(arnold, wehrli, arnoldZwilling), sorted);
    }

    @Test
    @DisplayName("Mehrstufig: gleicher Name -> Telefonnummer entscheidet")
    void compare_multiLevel_nameThenPhonenumber() {
        AddressComparator comparator = new AddressComparator(
                SortAttribute.LASTNAME, SortAttribute.FIRSTNAME, SortAttribute.PHONENUMBER);

        // arnold und arnoldZwilling heissen gleich -> Telefonnummer entscheidet
        assertTrue(comparator.compare(arnoldZwilling, arnold) < 0);
        assertTrue(comparator.compare(arnold, arnoldZwilling) > 0);
    }

    @Test
    @DisplayName("Konstruktor ohne Attribute wirft IllegalArgumentException")
    void constructor_withoutAttributes_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new AddressComparator(new SortAttribute[0]));
    }
}
