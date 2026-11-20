package com.rosewood.payroll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ConsolePayrollReporterTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PayrollReporter reporter = new ConsolePayrollReporter(
            new PrintStream(output, true, StandardCharsets.UTF_8)
    );

    @Test
    void writesOneLineWithTheEmployeeNameAndNetPay() {
        reporter.reportNetPay(
                new Employee("e1", "Efua Sarpong"), new BigDecimal("760.00")
        );

        assertEquals(
                "Net pay for Efua Sarpong: GHS 760.00" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void writesAmountsInPlainNotationRatherThanScientific() {
        reporter.reportNetPay(
                new Employee("e1", "Efua Sarpong"), new BigDecimal("1E+3")
        );

        assertEquals(
                "Net pay for Efua Sarpong: GHS 1000" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void rejectsANullOutputStream() {
        assertThrows(
                NullPointerException.class,
                () -> new ConsolePayrollReporter(null)
        );
    }
}
