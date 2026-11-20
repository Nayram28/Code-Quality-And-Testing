package com.rosewood.payroll;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Writes net pay as a single line of text to a {@link PrintStream}.
 * The stream is injected so the output can be captured in tests.
 */
public class ConsolePayrollReporter implements PayrollReporter {

    private final PrintStream output;

    public ConsolePayrollReporter() {
        this(System.out);
    }

    public ConsolePayrollReporter(PrintStream output) {
        this.output = Objects.requireNonNull(
                output, "Output stream cannot be null"
        );
    }

    @Override
    public void reportNetPay(Employee employee, BigDecimal netPay) {
        output.println(
                "Net pay for " + employee.getName() + ": GHS "
                        + netPay.toPlainString()
        );
    }
}
