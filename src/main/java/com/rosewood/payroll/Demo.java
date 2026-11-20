package com.rosewood.payroll;

import java.math.BigDecimal;
import java.util.List;

/**
 * Composition root: the only place that chooses concrete implementations.
 */
public class Demo {

    public static void main(String[] args) {
        PayrollCalculator calculator = new PayrollCalculator(
                new InMemoryEmployeeRepository(),
                new OvertimeGrossPayCalculator(),
                new BandedTaxCalculator(List.of(
                        new LowTaxBand(),
                        new MediumTaxBand(),
                        new HighTaxBand()
                )),
                new ConsolePayrollReporter()
        );

        run(calculator, "e1", 40, new BigDecimal("20.00"));
        run(calculator, "e2", 50, new BigDecimal("30.00"));
        run(calculator, "unknown", 40, new BigDecimal("20.00"));
        run(calculator, "e1", -5, new BigDecimal("20.00"));
    }

    /**
     * Runs one calculation so that a failure for one employee is reported
     * and does not stop the rest of the payroll run. Only expected,
     * recoverable failures are caught; anything else is a bug and should
     * still crash loudly.
     */
    private static void run(
            PayrollCalculator calculator,
            String employeeId,
            double hoursWorked,
            BigDecimal hourlyRate
    ) {
        try {
            calculator.calculateNetPay(employeeId, hoursWorked, hourlyRate);
        } catch (EmployeeNotFoundException | IllegalArgumentException exception) {
            System.out.println(
                    "Payroll calculation failed for '" + employeeId + "': "
                            + exception.getMessage()
            );
        }
    }
}
