package com.rosewood.payroll;

import java.math.BigDecimal;

/** 5% on gross pay below GHS 1,000. */
public class LowTaxBand extends FlatRateTaxBand {

    private static final BigDecimal MAXIMUM_GROSS_PAY = new BigDecimal("1000.00");

    public LowTaxBand() {
        super(new BigDecimal("0.05"));
    }

    @Override
    public boolean appliesTo(BigDecimal grossPay) {
        return grossPay.compareTo(MAXIMUM_GROSS_PAY) < 0;
    }
}
