package com.rosewood.payroll;

import java.math.BigDecimal;
import java.util.Objects;

public class PayrollCalculator {

    private final EmployeeRepository repository;
    private final GrossPayCalculator grossPayCalculator;
    private final TaxCalculator taxCalculator;
    private final PayrollReporter reporter;

    public PayrollCalculator(
            EmployeeRepository repository,
            GrossPayCalculator grossPayCalculator,
            TaxCalculator taxCalculator,
            PayrollReporter reporter
    ) {
        this.repository = Objects.requireNonNull(
                repository, "Employee repository cannot be null"
        );
        this.grossPayCalculator = Objects.requireNonNull(
                grossPayCalculator, "Gross pay calculator cannot be null"
        );
        this.taxCalculator = Objects.requireNonNull(
                taxCalculator, "Tax calculator cannot be null"
        );
        this.reporter = Objects.requireNonNull(
                reporter, "Payroll reporter cannot be null"
        );
    }

    public BigDecimal calculateNetPay(
            String employeeId,
            double hoursWorked,
            BigDecimal hourlyRate
    ) {
        validatePayrollInputs(employeeId, hoursWorked, hourlyRate);

        Employee employee = repository.findById(employeeId);

        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId);
        }

        BigDecimal grossPay = grossPayCalculator.calculateGrossPay(
                hoursWorked,
                hourlyRate
        );

        BigDecimal tax = taxCalculator.calculateTax(grossPay);

        BigDecimal netPay = grossPay.subtract(tax);

        reporter.reportNetPay(employee, netPay);

        return netPay;
    }

    private void validatePayrollInputs(
            String employeeId,
            double hoursWorked,
            BigDecimal hourlyRate
    ) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException(
                    "Employee ID must not be blank"
            );
        }

        if (!Double.isFinite(hoursWorked) || hoursWorked < 0) {
            throw new IllegalArgumentException(
                    "Hours worked must be a finite, non-negative value"
            );
        }

        if (hourlyRate == null || hourlyRate.signum() < 0) {
            throw new IllegalArgumentException(
                    "Hourly rate must be non-negative and cannot be null"
            );
        }
    }
}
