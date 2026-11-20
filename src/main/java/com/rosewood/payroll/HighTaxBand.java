package com.rosewood.payroll;

import java.math.BigDecimal;

/** 25% on gross pay of GHS 5,000 and above. */
public class HighTaxBand extends FlatRateTaxBand {

    private static final BigDecimal MINIMUM_GROSS_PAY = new BigDecimal("5000.00");

    public HighTaxBand() {
        super(new BigDecimal("0.25"));
    }

    @Override
    public boolean appliesTo(BigDecimal grossPay) {
        return grossPay.compareTo(MINIMUM_GROSS_PAY) >= 0;
    }
}
