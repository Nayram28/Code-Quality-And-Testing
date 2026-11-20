package com.rosewood.payroll;

import java.math.BigDecimal;

/** 15% on gross pay from GHS 1,000 up to (but not including) GHS 5,000. */
public class MediumTaxBand extends FlatRateTaxBand {

    private static final BigDecimal MINIMUM_GROSS_PAY = new BigDecimal("1000.00");
    private static final BigDecimal MAXIMUM_GROSS_PAY = new BigDecimal("5000.00");

    public MediumTaxBand() {
        super(new BigDecimal("0.15"));
    }

    @Override
    public boolean appliesTo(BigDecimal grossPay) {
        return grossPay.compareTo(MINIMUM_GROSS_PAY) >= 0
                && grossPay.compareTo(MAXIMUM_GROSS_PAY) < 0;
    }
}
