package com.rosewood.payroll;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InMemoryEmployeeRepositoryTest {

    private final Employee efua = new Employee("e1", "Efua Sarpong");

    @Test
    void findsAnEmployeeById() {
        EmployeeRepository repository =
                new InMemoryEmployeeRepository(List.of(efua));

        assertSame(efua, repository.findById("e1").orElseThrow());
    }

    @Test
    void returnsEmptyForAnUnknownId() {
        EmployeeRepository repository =
                new InMemoryEmployeeRepository(List.of(efua));

        assertEquals(Optional.empty(), repository.findById("unknown"));
    }

    @Test
    void returnsEmptyForANullId() {
        EmployeeRepository repository =
                new InMemoryEmployeeRepository(List.of(efua));

        assertTrue(repository.findById(null).isEmpty());
    }

    @Test
    void defaultRepositoryContainsTheSampleEmployees() {
        EmployeeRepository repository = new InMemoryEmployeeRepository();

        assertEquals("Efua Sarpong", repository.findById("e1").orElseThrow().getName());
        assertEquals("Kwame Owusu", repository.findById("e2").orElseThrow().getName());
    }

    @Test
    void rejectsDuplicateEmployeeIds() {
        Employee duplicate = new Employee("e1", "Someone Else");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InMemoryEmployeeRepository(List.of(efua, duplicate))
        );

        assertEquals("Duplicate employee ID: e1", exception.getMessage());
    }

    @Test
    void rejectsNullEmployeeCollection() {
        assertThrows(
                NullPointerException.class,
                () -> new InMemoryEmployeeRepository(null)
        );
    }
}
