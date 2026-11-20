package com.rosewood.payroll;

import java.math.BigDecimal;

/**
 * Thrown when the configured tax bands do not cover a gross pay amount.
 * This is a configuration fault, not bad user input, so it extends
 * IllegalStateException.
 */
public class NoApplicableTaxBandException extends IllegalStateException {

    public NoApplicableTaxBandException(BigDecimal grossPay) {
        super("No tax band found for gross pay: " + grossPay.toPlainString());
    }
}
