package com.rosewood.payroll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmployeeTest {

    @Test
    void exposesIdAndName() {
        Employee employee = new Employee("e1", "Efua Sarpong");

        assertEquals("e1", employee.getId());
        assertEquals("Efua Sarpong", employee.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsBlankId(String id) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(id, "Efua Sarpong")
        );

        assertEquals("Employee ID must not be blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsBlankName(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Employee("e1", name)
        );

        assertEquals("Employee name must not be blank", exception.getMessage());
    }
}
