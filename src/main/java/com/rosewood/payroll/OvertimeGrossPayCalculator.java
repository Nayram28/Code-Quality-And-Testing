package com.rosewood.payroll;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pays every hour at the hourly rate, plus a 50% premium on each hour
 * worked beyond the standard 40-hour week.
 */
public class OvertimeGrossPayCalculator implements GrossPayCalculator {

    private static final BigDecimal STANDARD_HOURS = new BigDecimal("40");
    private static final BigDecimal OVERTIME_PREMIUM = new BigDecimal("0.5");
    private static final int CURRENCY_SCALE = 2;

    @Override
    public BigDecimal calculateGrossPay(
            double hoursWorked,
            BigDecimal hourlyRate
    ) {
        BigDecimal hours = BigDecimal.valueOf(hoursWorked);

        BigDecimal grossPay = hours.multiply(hourlyRate);

        if (hours.compareTo(STANDARD_HOURS) > 0) {
            BigDecimal overtimeHours = hours.subtract(STANDARD_HOURS);

            BigDecimal overtimePremium = overtimeHours
                    .multiply(hourlyRate)
                    .multiply(OVERTIME_PREMIUM);

            grossPay = grossPay.add(overtimePremium);
        }

        return grossPay.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
    }
}
