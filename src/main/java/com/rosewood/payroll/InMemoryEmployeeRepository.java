package com.rosewood.payroll;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> employees = new HashMap<>();

    /** Creates a repository holding the two sample employees used by the demo. */
    public InMemoryEmployeeRepository() {
        this(List.of(
                new Employee("e1", "Efua Sarpong"),
                new Employee("e2", "Kwame Owusu")
        ));
    }

    public InMemoryEmployeeRepository(Collection<Employee> employees) {
        Objects.requireNonNull(employees, "Employees cannot be null");

        for (Employee employee : employees) {
            Objects.requireNonNull(employee, "Employee cannot be null");

            if (this.employees.putIfAbsent(employee.getId(), employee) != null) {
                throw new IllegalArgumentException(
                        "Duplicate employee ID: " + employee.getId()
                );
            }
        }
    }

    @Override
    public Optional<Employee> findById(String id) {
        return Optional.ofNullable(employees.get(id));
    }
}
