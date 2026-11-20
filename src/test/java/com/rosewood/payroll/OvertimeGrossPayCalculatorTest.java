package com.rosewood.payroll;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class OvertimeGrossPayCalculatorTest {

    private final GrossPayCalculator calculator = new OvertimeGrossPayCalculator();

    @Test
    void calculatesGrossPayForStandardWorkingHours() {
        BigDecimal result = calculator.calculateGrossPay(
                40, new BigDecimal("20.00")
        );

        assertEquals(new BigDecimal("800.00"), result);
    }

    @Test
    void calculatesGrossPayWithOvertime() {
        BigDecimal result = calculator.calculateGrossPay(
                50, new BigDecimal("30.00")
        );

        assertEquals(new BigDecimal("1650.00"), result);
    }

    @Test
    void doesNotApplyOvertimeAtExactlyFortyHours() {
        BigDecimal result = calculator.calculateGrossPay(
                40, new BigDecimal("30.00")
        );

        assertEquals(new BigDecimal("1200.00"), result);
    }

    @Test
    void calculatesZeroGrossPayWhenNoHoursAreWorked() {
        BigDecimal result = calculator.calculateGrossPay(
                0, new BigDecimal("20.00")
        );

        assertEquals(new BigDecimal("0.00"), result);
    }
}