package ch.m450.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    // Toleranz für double-Vergleiche (0.1 + 0.2 ist binär nicht exakt 0.3)
    private static final double DELTA = 1e-9;

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    void testAdd() {
        assertEquals(5.0, calculator.add(2.0, 3.0), DELTA);
        assertEquals(-1.5, calculator.add(-3.0, 1.5), DELTA);
    }

    @ParameterizedTest(name = "add({0}, {1}) = {2}")
    @CsvSource({
            "0.0,   0.0,   0.0",
            "1.5,   2.5,   4.0",
            "-2.0, -3.0,  -5.0",
            "0.1,   0.2,   0.3"
    })
    void testAddParameterized(double a, double b, double expected) {
        assertEquals(expected, calculator.add(a, b), DELTA);
    }

    @Test
    void testSubtract() {
        assertEquals(1.0, calculator.subtract(3.0, 2.0), DELTA);
        assertEquals(-4.0, calculator.subtract(2.0, 6.0), DELTA);
    }

    @Test
    void testMultiply() {
        assertEquals(12.0, calculator.multiply(3.0, 4.0), DELTA);
        assertEquals(0.0, calculator.multiply(123.45, 0.0), DELTA);
        assertEquals(6.0, calculator.multiply(-2.0, -3.0), DELTA);
    }

    @Test
    void testDivide() {
        assertEquals(2.5, calculator.divide(5.0, 2.0), DELTA);
    }

    @Test
    @DisplayName("Division durch 0 wirft Exception")
    void testDivideByZero() {
        assertThrows(ArithmeticException.class, () -> calculator.divide(1.0, 0.0));
    }
}
