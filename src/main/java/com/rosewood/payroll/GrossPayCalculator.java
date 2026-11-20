package com.rosewood.payroll;

import java.math.BigDecimal;

/**
 * Calculates an employee's gross pay (pay before tax) for a pay period.
 */
public interface GrossPayCalculator {

    BigDecimal calculateGrossPay(double hoursWorked, BigDecimal hourlyRate);
}
