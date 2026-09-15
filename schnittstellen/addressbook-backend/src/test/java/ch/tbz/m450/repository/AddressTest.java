package ch.tbz.m450.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Aufgabe 1: "Fangen Sie an mit dem Testen von Adressen, welche Sie erstellen."
 * Testet das Erstellen von Address-Objekten über beide Lombok-Konstruktoren
 * sowie die Getter/Setter.
 */
class AddressTest {

    private Date registrationDate;

    @BeforeEach
    void setUp() {
        registrationDate = new Date();
    }

    @Test
    @DisplayName("AllArgsConstructor setzt alle Felder korrekt")
    void createAddress_withAllArgsConstructor_setsAllFields() {
        Address address = new Address(1, "Nicola", "Wehrli", "0791234567", registrationDate);

        assertAll(
                () -> assertEquals(1, address.getId()),
                () -> assertEquals("Nicola", address.getFirstname()),
                () -> assertEquals("Wehrli", address.getLastname()),
                () -> assertEquals("0791234567", address.getPhonenumber()),
                () -> assertEquals(registrationDate, address.getRegistrationDate())
        );
    }

    @Test
    @DisplayName("NoArgsConstructor erstellt leere Adresse, Setter befüllen sie")
    void createAddress_withNoArgsConstructorAndSetters_setsAllFields() {
        Address address = new Address();

        assertNull(address.getFirstname());
        assertNull(address.getLastname());

        address.setId(2);
        address.setFirstname("Anna");
        address.setLastname("Muster");
        address.setPhonenumber("0447654321");
        address.setRegistrationDate(registrationDate);

        assertAll(
                () -> assertEquals(2, address.getId()),
                () -> assertEquals("Anna", address.getFirstname()),
                () -> assertEquals("Muster", address.getLastname()),
                () -> assertEquals("0447654321", address.getPhonenumber()),
                () -> assertEquals(registrationDate, address.getRegistrationDate())
        );
    }
}
