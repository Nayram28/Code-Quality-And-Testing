package com.rosewood.payroll;

import java.math.BigDecimal;

/**
 * Calculates the tax owed on a gross pay amount.
 */
public interface TaxCalculator {

    BigDecimal calculateTax(BigDecimal grossPay);
}
