package com.rosewood.payroll;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TaxBandTest {

    private final TaxBand low = new LowTaxBand();
    private final TaxBand medium = new MediumTaxBand();
    private final TaxBand high = new HighTaxBand();

    /** Exactly one band must apply to every amount, on both sides of each boundary. */
    @ParameterizedTest(name = "{0} -> low={1}, medium={2}, high={3}")
    @CsvSource({
            "0.00,     true,  false, false",
            "999.99,   true,  false, false",
            "1000.00,  false, true,  false",
            "4999.99,  false, true,  false",
            "5000.00,  false, false, true",
            "100000.00, false, false, true"
    })
    void eachAmountFallsIntoExactlyOneBand(
            BigDecimal grossPay, boolean inLow, boolean inMedium, boolean inHigh
    ) {
        assertEquals(inLow, low.appliesTo(grossPay));
        assertEquals(inMedium, medium.appliesTo(grossPay));
        assertEquals(inHigh, high.appliesTo(grossPay));
    }

    @Test
    void lowBandChargesFivePercent() {
        assertEquals(new BigDecimal("50.00"), low.calculateTax(new BigDecimal("1000.00")));
    }

    @Test
    void mediumBandChargesFifteenPercent() {
        assertEquals(new BigDecimal("150.00"), medium.calculateTax(new BigDecimal("1000.00")));
    }

    @Test
    void highBandChargesTwentyFivePercent() {
        assertEquals(new BigDecimal("250.00"), high.calculateTax(new BigDecimal("1000.00")));
    }

    @Test
    void roundsTaxHalfUpToTwoDecimalPlaces() {
        // 0.05 * 10.10 = 0.505 -> 0.51 with HALF_UP (HALF_EVEN would give 0.50)
        assertEquals(new BigDecimal("0.51"), low.calculateTax(new BigDecimal("10.10")));
    }

    @Test
    void rejectsATaxRateAboveOneHundredPercent() {
        assertThrows(IllegalArgumentException.class, () -> bandWithRate("1.01"));
    }

    @Test
    void rejectsANegativeTaxRate() {
        assertThrows(IllegalArgumentException.class, () -> bandWithRate("-0.01"));
    }

    @Test
    void acceptsZeroAndOneHundredPercentRates() {
        assertEquals(
                new BigDecimal("0.00"),
                bandWithRate("0").calculateTax(new BigDecimal("10.00"))
        );
        assertEquals(
                new BigDecimal("10.00"),
                bandWithRate("1").calculateTax(new BigDecimal("10.00"))
        );
    }

    private static TaxBand bandWithRate(String rate) {
        return new FlatRateTaxBand(new BigDecimal(rate)) {
            @Override
            public boolean appliesTo(BigDecimal grossPay) {
                return true;
            }
        };
    }
}
