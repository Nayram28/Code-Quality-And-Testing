package com.rosewood.payroll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A tax band that charges a single percentage of gross pay. Subclasses
 * only decide which gross pay amounts the band applies to.
 */
public abstract class FlatRateTaxBand implements TaxBand {

    private static final int CURRENCY_SCALE = 2;

    private final BigDecimal taxRate;

    protected FlatRateTaxBand(BigDecimal taxRate) {
        Objects.requireNonNull(taxRate, "Tax rate cannot be null");

        if (taxRate.signum() < 0 || taxRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "Tax rate must be between 0 and 1: " + taxRate
            );
        }
        this.taxRate = taxRate;
    }

    @Override
    public final BigDecimal calculateTax(BigDecimal grossPay) {
        return grossPay.multiply(taxRate)
                .setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
    }
}
