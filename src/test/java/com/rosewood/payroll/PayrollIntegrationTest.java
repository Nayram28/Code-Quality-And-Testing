package com.rosewood.payroll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Runs the real collaborators together and checks the amounts a payroll
 * clerk would see. Expected values are worked out by hand in each row.
 */
class PayrollIntegrationTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private final PayrollCalculator calculator = new PayrollCalculator(
            new InMemoryEmployeeRepository(List.of(
                    new Employee("e1", "Efua Sarpong"),
                    new Employee("e2", "Kwame Owusu")
            )),
            new OvertimeGrossPayCalculator(),
            new BandedTaxCalculator(List.of(
                    new LowTaxBand(), new MediumTaxBand(), new HighTaxBand()
            )),
            new ConsolePayrollReporter(
                    new PrintStream(output, true, StandardCharsets.UTF_8)
            )
    );

    @ParameterizedTest(name = "{0}h @ {1} -> net {2}")
    @CsvSource({
            // hours, rate,   expected net   // working
            "35,   25.00,  831.25",          // 875.00 gross, low 5%   -> 875.00 - 43.75
            "40,   20.00,  760.00",          // 800.00 gross, low 5%   -> 800.00 - 40.00
            "50,   30.00,  1402.50",         // 1500 + 5h*30*0.5=75 -> 1650.00, medium 15% -> 1650.00 - 247.50
            "60,   100.00, 5250.00",         // 6000 + 20h*100*0.5=1000 -> 7000.00, high 25% -> 7000.00 - 1750.00
            "0,    20.00,  0.00",
            "40,   0.00,   0.00"
    })
    void calculatesNetPayEndToEnd(double hours, BigDecimal rate, BigDecimal expectedNet) {
        assertEquals(expectedNet, calculator.calculateNetPay("e1", hours, rate));
    }

    @Test
    void reportsNetPayForTheRightEmployee() {
        calculator.calculateNetPay("e2", 50, new BigDecimal("30.00"));

        assertEquals(
                "Net pay for Kwame Owusu: GHS 1402.50" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void unknownEmployeeFailsLoudlyInsteadOfReturningZero() {
        assertThrows(
                EmployeeNotFoundException.class,
                () -> calculator.calculateNetPay("unknown", 40, new BigDecimal("20.00"))
        );

        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }
}
