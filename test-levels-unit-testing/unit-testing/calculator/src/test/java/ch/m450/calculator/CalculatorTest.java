package ch.m450.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests für die Klasse Calculator (JUnit 5).
 *
 * Aufbau jedes Tests nach dem AAA-Muster: Arrange, Act, Assert.
 * Bei double-Vergleichen wird immer ein Delta (Toleranz) angegeben,
 * weil Gleitkommazahlen binär gerundet werden.
 */
class CalculatorTest {

    private static final double DELTA = 1e-9;

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        // Vor JEDEM Test eine frische Instanz -> Tests sind isoliert
        calculator = new Calculator();
    }

    // ---------- Addition ----------

    @Test
    @DisplayName("add: addiert zwei positive Zahlen")
    void addsTwoPositiveNumbers() {
        assertEquals(5.0, calculator.add(2.0, 3.0), DELTA);
    }

    @Test
    @DisplayName("add: funktioniert mit negativen Zahlen")
    void addsNegativeNumbers() {
        assertEquals(-1.5, calculator.add(-3.0, 1.5), DELTA);
    }

    @ParameterizedTest(name = "add({0}, {1}) = {2}")
    @CsvSource({
            "0.0,   0.0,   0.0",
            "1.5,   2.5,   4.0",
            "-2.0, -3.0,  -5.0",
            "0.1,   0.2,   0.3"
    })
    @DisplayName("add: parametrisierte Fälle")
    void addParameterized(double a, double b, double expected) {
        assertEquals(expected, calculator.add(a, b), DELTA);
    }

    // ---------- Subtraktion ----------

    @Test
    @DisplayName("subtract: subtrahiert korrekt")
    void subtracts() {
        assertEquals(1.0, calculator.subtract(3.0, 2.0), DELTA);
    }

    @Test
    @DisplayName("subtract: Resultat kann negativ sein")
    void subtractsIntoNegative() {
        assertEquals(-4.0, calculator.subtract(2.0, 6.0), DELTA);
    }

    // ---------- Multiplikation ----------

    @Test
    @DisplayName("multiply: multipliziert korrekt")
    void multiplies() {
        assertEquals(12.0, calculator.multiply(3.0, 4.0), DELTA);
    }

    @Test
    @DisplayName("multiply: mit 0 ergibt 0")
    void multiplyByZero() {
        assertEquals(0.0, calculator.multiply(123.45, 0.0), DELTA);
    }

    @Test
    @DisplayName("multiply: minus mal minus gibt plus")
    void multiplyTwoNegatives() {
        assertEquals(6.0, calculator.multiply(-2.0, -3.0), DELTA);
    }

    // ---------- Division ----------

    @Test
    @DisplayName("divide: dividiert korrekt")
    void divides() {
        assertEquals(2.5, calculator.divide(5.0, 2.0), DELTA);
    }

    @Test
    @DisplayName("divide: Division durch 0 wirft ArithmeticException")
    void divideByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> calculator.divide(1.0, 0.0));
    }
}
