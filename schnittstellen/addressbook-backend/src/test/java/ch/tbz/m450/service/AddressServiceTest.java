package ch.tbz.m450.service;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Aufgabe 1: Der Service wird getestet, indem die h2-Datenbank "weg-gemockt" wird:
 * Das AddressRepository (die Schnittstelle zur DB) wird durch ein Mockito-Mock
 * ersetzt – es läuft also KEINE echte Datenbank. Wir stubben die Antworten (when/thenReturn)
 * und verifizieren die Interaktionen (verify) -> Mock UND Stub in einem.
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    private AddressService addressService;

    private Address wehrli;
    private Address arnold;
    private Address zwahlen;

    @BeforeEach
    void setUp() {
        addressService = new AddressService(addressRepository);
        wehrli = new Address(1, "Nicola", "Wehrli", "0791234567", new Date());
        arnold = new Address(2, "Zoe", "Arnold", "0447654321", new Date());
        zwahlen = new Address(3, "Adam", "Zwahlen", "0791112233", new Date());
    }

    @Test
    @DisplayName("save delegiert ans Repository und gibt die gespeicherte Adresse zurück")
    void save_delegatesToRepository_andReturnsSavedAddress() {
        when(addressRepository.save(wehrli)).thenReturn(wehrli);

        Address result = addressService.save(wehrli);

        assertEquals(wehrli, result);
        verify(addressRepository).save(wehrli);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    @DisplayName("getAll liefert die Adressen sortiert nach Nachname/Vorname")
    void getAll_returnsAddressesSortedByComparator() {
        when(addressRepository.findAll()).thenReturn(List.of(zwahlen, wehrli, arnold));

        List<Address> result = addressService.getAll();

        assertEquals(List.of(arnold, wehrli, zwahlen), result);
        verify(addressRepository).findAll();
    }

    @Test
    @DisplayName("getAddress liefert Optional.empty, wenn die Id nicht existiert")
    void getAddress_unknownId_returnsEmptyOptional() {
        when(addressRepository.findById(99)).thenReturn(Optional.empty());

        assertTrue(addressService.getAddress(99).isEmpty());
        verify(addressRepository).findById(99);
    }
}
