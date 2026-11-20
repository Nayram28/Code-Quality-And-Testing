package com.rosewood.payroll;

import java.math.BigDecimal;
import java.util.List;

public class Demo {

    public static void main(String[] args) {

        EmployeeRepository repository = new InMemoryEmployeeRepository();

        GrossPayCalculator grossPayCalculator = new OvertimeGrossPayCalculator();

        TaxCalculator taxCalculator = new BandedTaxCalculator(
                List.of(
                        new LowTaxBand(),
                        new MediumTaxBand(),
                        new HighTaxBand()
                )
        );

        PayrollReporter reporter = new ConsolePayrollReporter();

        PayrollCalculator calculator = new PayrollCalculator(
                repository,
                grossPayCalculator,
                taxCalculator,
                reporter
        );

        try {
            BigDecimal firstRate = new BigDecimal("20.00");
            BigDecimal secondRate = new BigDecimal("30.00");

            calculator.calculateNetPay("e1", 40, firstRate);
            calculator.calculateNetPay("e2", 50, secondRate);
            calculator.calculateNetPay("unknown", 40, firstRate);

        } catch (EmployeeNotFoundException exception) {
            System.out.println(
                    "Payroll calculation failed: " + exception.getMessage()
            );
        }
    }
}