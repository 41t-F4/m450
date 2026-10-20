package ch.tbz.m450.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Aufgabe 1: "Fangen Sie an mit dem Testen von Adressen, welche Sie erstellen."
 * Testet das Erstellen einer Address ueber den AllArgsConstructor.
 * (Getter/Setter-Tests bewusst weggelassen - kein relevanter Code.)
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

}
