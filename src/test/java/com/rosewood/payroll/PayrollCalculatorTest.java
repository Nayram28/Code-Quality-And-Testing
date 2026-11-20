package com.rosewood.payroll;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PayrollCalculator in isolation: every collaborator is a
 * mock, so these tests check only how the calculator orchestrates them.
 * End-to-end amounts are covered by PayrollIntegrationTest.
 */
@ExtendWith(MockitoExtension.class)
class PayrollCalculatorTest {

    private static final BigDecimal RATE = new BigDecimal("20.00");

    @Mock private EmployeeRepository repository;
    @Mock private GrossPayCalculator grossPayCalculator;
    @Mock private TaxCalculator taxCalculator;
    @Mock private PayrollReporter reporter;

    private final Employee efua = new Employee("e1", "Efua Sarpong");

    private PayrollCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PayrollCalculator(
                repository, grossPayCalculator, taxCalculator, reporter
        );
    }

    @Test
    void netPayIsGrossPayMinusTax() {
        when(repository.findById("e1")).thenReturn(Optional.of(efua));
        when(grossPayCalculator.calculateGrossPay(40, RATE))
                .thenReturn(new BigDecimal("800.00"));
        when(taxCalculator.calculateTax(new BigDecimal("800.00")))
                .thenReturn(new BigDecimal("40.00"));

        BigDecimal netPay = calculator.calculateNetPay("e1", 40, RATE);

        assertEquals(new BigDecimal("760.00"), netPay);
    }

    @Test
    void reportsTheNetPayForTheEmployeeFound() {
        when(repository.findById("e1")).thenReturn(Optional.of(efua));
        when(grossPayCalculator.calculateGrossPay(40, RATE))
                .thenReturn(new BigDecimal("800.00"));
        when(taxCalculator.calculateTax(new BigDecimal("800.00")))
                .thenReturn(new BigDecimal("40.00"));

        calculator.calculateNetPay("e1", 40, RATE);

        verify(reporter).reportNetPay(efua, new BigDecimal("760.00"));
    }

    @Test
    void throwsEmployeeNotFoundAndDoesNotCalculateForAnUnknownEmployee() {
        when(repository.findById("unknown")).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> calculator.calculateNetPay("unknown", 40, RATE)
        );

        assertEquals("Employee not found with ID: unknown", exception.getMessage());
        verifyNoInteractions(grossPayCalculator, taxCalculator, reporter);
    }

    @Test
    void propagatesTaxConfigurationFailuresWithoutReporting() {
        when(repository.findById("e1")).thenReturn(Optional.of(efua));
        when(grossPayCalculator.calculateGrossPay(anyDouble(), any()))
                .thenReturn(new BigDecimal("6000.00"));
        NoApplicableTaxBandException failure =
                new NoApplicableTaxBandException(new BigDecimal("6000.00"));
        when(taxCalculator.calculateTax(any())).thenThrow(failure);

        NoApplicableTaxBandException thrown = assertThrows(
                NoApplicableTaxBandException.class,
                () -> calculator.calculateNetPay("e1", 40, RATE)
        );

        assertSame(failure, thrown);
        verifyNoInteractions(reporter);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsABlankEmployeeIdBeforeLookingItUp(String employeeId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateNetPay(employeeId, 40, RATE)
        );

        assertEquals("Employee ID must not be blank", exception.getMessage());
        verifyNoInteractions(repository, grossPayCalculator, taxCalculator, reporter);
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            -0.01, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    })
    void rejectsInvalidHoursWorked(double hoursWorked) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateNetPay("e1", hoursWorked, RATE)
        );

        assertEquals(
                "Hours worked must be a finite, non-negative value",
                exception.getMessage()
        );
        verifyNoInteractions(repository, grossPayCalculator, taxCalculator, reporter);
    }

    @Test
    void rejectsANegativeHourlyRate() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateNetPay("e1", 40, new BigDecimal("-0.01"))
        );

        assertEquals(
                "Hourly rate must be non-negative and cannot be null",
                exception.getMessage()
        );
        verifyNoInteractions(repository, grossPayCalculator, taxCalculator, reporter);
    }

    @Test
    void rejectsANullHourlyRate() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateNetPay("e1", 40, null)
        );

        assertEquals(
                "Hourly rate must be non-negative and cannot be null",
                exception.getMessage()
        );
        verifyNoInteractions(repository, grossPayCalculator, taxCalculator, reporter);
    }

    @Test
    void rejectsMissingCollaborators() {
        assertThrows(NullPointerException.class, () -> new PayrollCalculator(
                null, grossPayCalculator, taxCalculator, reporter));
        assertThrows(NullPointerException.class, () -> new PayrollCalculator(
                repository, null, taxCalculator, reporter));
        assertThrows(NullPointerException.class, () -> new PayrollCalculator(
                repository, grossPayCalculator, null, reporter));
        assertThrows(NullPointerException.class, () -> new PayrollCalculator(
                repository, grossPayCalculator, taxCalculator, null));
    }
}
