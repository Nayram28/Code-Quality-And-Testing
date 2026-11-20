package com.rosewood.payroll;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class BandedTaxCalculatorTest {

    private final TaxCalculator calculator = new BandedTaxCalculator(
            List.of(new LowTaxBand(), new MediumTaxBand(), new HighTaxBand())
    );

    @Test
    void appliesLowRateBelowOneThousand() {
        assertEquals(
                new BigDecimal("40.00"),
                calculator.calculateTax(new BigDecimal("800.00"))
        );
    }

    @Test
    void appliesMediumRateAtExactlyOneThousand() {
        assertEquals(
                new BigDecimal("150.00"),
                calculator.calculateTax(new BigDecimal("1000.00"))
        );
    }

    @Test
    void appliesMediumRateBetweenOneAndFiveThousand() {
        assertEquals(
                new BigDecimal("225.00"),
                calculator.calculateTax(new BigDecimal("1500.00"))
        );
    }

    @Test
    void appliesHighRateAtExactlyFiveThousand() {
        assertEquals(
                new BigDecimal("1250.00"),
                calculator.calculateTax(new BigDecimal("5000.00"))
        );
    }

    @Test
    void usesTheFirstBandThatApplies() {
        TaxBand everything = fixedTaxBand(new BigDecimal("1.00"));
        TaxBand alsoEverything = fixedTaxBand(new BigDecimal("2.00"));

        TaxCalculator ordered = new BandedTaxCalculator(
                List.of(everything, alsoEverything)
        );

        assertEquals(
                new BigDecimal("1.00"),
                ordered.calculateTax(new BigDecimal("500.00"))
        );
    }

    @Test
    void supportsANewTaxBandWithoutModifyingTheCalculator() {
        TaxBand topBand = new TaxBand() {
            @Override
            public boolean appliesTo(BigDecimal grossPay) {
                return grossPay.compareTo(new BigDecimal("10000.00")) >= 0;
            }

            @Override
            public BigDecimal calculateTax(BigDecimal grossPay) {
                return grossPay.multiply(new BigDecimal("0.30")).setScale(2);
            }
        };

        TaxCalculator extended = new BandedTaxCalculator(
                List.of(
                        new LowTaxBand(),
                        new MediumTaxBand(),
                        topBand,
                        new HighTaxBand()
                )
        );

        assertEquals(
                new BigDecimal("3000.00"),
                extended.calculateTax(new BigDecimal("10000.00"))
        );
    }

    @Test
    void rejectsGrossPayWithoutAnApplicableTaxBand() {
        TaxCalculator incomplete = new BandedTaxCalculator(
                List.of(new LowTaxBand(), new MediumTaxBand())
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> incomplete.calculateTax(new BigDecimal("6000.00"))
        );

        assertEquals(
                "No tax band found for gross pay: 6000.00",
                exception.getMessage()
        );
    }

    @Test
    void rejectsNullGrossPay() {
        assertThrows(
                NullPointerException.class,
                () -> calculator.calculateTax(null)
        );
    }

    @Test
    void rejectsEmptyTaxBandConfiguration() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BandedTaxCalculator(List.of())
        );

        assertEquals(
                "At least one tax band must be configured",
                exception.getMessage()
        );
    }

    @Test
    void rejectsNullTaxBandConfiguration() {
        assertThrows(
                NullPointerException.class,
                () -> new BandedTaxCalculator(null)
        );
    }

    private static TaxBand fixedTaxBand(BigDecimal tax) {
        return new TaxBand() {
            @Override
            public boolean appliesTo(BigDecimal grossPay) {
                return true;
            }

            @Override
            public BigDecimal calculateTax(BigDecimal grossPay) {
                return tax;
            }
        };
    }
}
