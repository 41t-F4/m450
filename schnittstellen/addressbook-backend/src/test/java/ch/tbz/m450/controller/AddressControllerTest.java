package ch.tbz.m450.controller;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Aufgabe 1: Der Controller wird isoliert getestet – der AddressService ist gemockt.
 * Geprüft werden die HTTP-Statuscodes (201/200/404) und dass der Controller
 * korrekt an den Service delegiert.
 */
@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    private AddressController controller;

    private Address wehrli;

    @BeforeEach
    void setUp() {
        controller = new AddressController(addressService);
        wehrli = new Address(1, "Nicola", "Wehrli", "0791234567", new Date());
    }

    @Test
    @DisplayName("POST /address gibt 201 und die gespeicherte Adresse zurück")
    void createAddress_returns201WithSavedAddress() {
        when(addressService.save(wehrli)).thenReturn(wehrli);

        ResponseEntity<Address> response = controller.createAddress(wehrli);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(wehrli, response.getBody());
        verify(addressService).save(wehrli);
    }

    @Test
    @DisplayName("GET /address gibt 200 und alle Adressen zurück")
    void getAddresses_returns200WithAllAddresses() {
        when(addressService.getAll()).thenReturn(List.of(wehrli));

        ResponseEntity<List<Address>> response = controller.getAddresses();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(wehrli), response.getBody());
    }

    @Test
    @DisplayName("GET /address/{id} gibt 200 und die Adresse zurück, wenn sie existiert")
    void getAddress_existingId_returns200WithAddress() {
        when(addressService.getAddress(1)).thenReturn(Optional.of(wehrli));

        ResponseEntity<Address> response = controller.getAddress(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(wehrli, response.getBody());
    }

    @Test
    @DisplayName("GET /address/{id} gibt 404 ohne Body, wenn die Id nicht existiert")
    void getAddress_unknownId_returns404() {
        when(addressService.getAddress(99)).thenReturn(Optional.empty());

        ResponseEntity<Address> response = controller.getAddress(99);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
