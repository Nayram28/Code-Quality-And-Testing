package com.rosewood.payroll;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OvertimeGrossPayCalculatorTest {

    private final GrossPayCalculator calculator = new OvertimeGrossPayCalculator();

    @ParameterizedTest(name = "{0}h @ {1} -> {2}")
    @CsvSource({
            // hours, rate,  expected gross  // working
            "0,     20.00, 0.00",
            "35,    25.00, 875.00",          // no overtime
            "40,    30.00, 1200.00",         // exactly 40h: no overtime premium
            "40.5,  20.00, 815.00",          // 810.00 + 0.5h*20*0.5=5.00
            "50,    30.00, 1650.00",         // 1500.00 + 10h*30*0.5=150.00
            "37.5,  12.34, 462.75",          // 462.75 exactly
            "1,     10.005, 10.01",          // rounds HALF_UP to 2 dp
            "0.1,   10.00, 1.00"             // decimal hours stay exact, no 0.999.. drift
    })
    void calculatesGrossPay(double hours, BigDecimal rate, BigDecimal expected) {
        assertEquals(expected, calculator.calculateGrossPay(hours, rate));
    }
}
