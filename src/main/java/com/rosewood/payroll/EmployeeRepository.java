package com.rosewood.payroll;

import java.util.Optional;

public interface EmployeeRepository {

    /**
     * @return the employee with this id, or an empty Optional when no such
     *         employee exists. Never returns null.
     */
    Optional<Employee> findById(String id);
}
