package com.rosewood.payroll;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Calculates tax using the first configured {@link TaxBand} that applies
 * to the gross pay. New bands are added by configuration, not by editing
 * this class.
 */
public class BandedTaxCalculator implements TaxCalculator {

    private final List<TaxBand> taxBands;

    public BandedTaxCalculator(List<TaxBand> taxBands) {
        this.taxBands = List.copyOf(
                Objects.requireNonNull(taxBands, "Tax bands cannot be null")
        );

        if (this.taxBands.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one tax band must be configured"
            );
        }
    }

    @Override
    public BigDecimal calculateTax(BigDecimal grossPay) {
        Objects.requireNonNull(grossPay, "Gross pay cannot be null");

        TaxBand applicableBand = taxBands.stream()
                .filter(band -> band.appliesTo(grossPay))
                .findFirst()
                .orElseThrow(() -> new NoApplicableTaxBandException(grossPay));

        return applicableBand.calculateTax(grossPay);
    }
}
