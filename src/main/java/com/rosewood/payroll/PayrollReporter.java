package com.rosewood.payroll;

import java.math.BigDecimal;

/**
 * Reports a calculated net pay. Keeps output concerns (console, file,
 * email, ...) out of the payroll calculation.
 */
public interface PayrollReporter {

    void reportNetPay(Employee employee, BigDecimal netPay);
}
