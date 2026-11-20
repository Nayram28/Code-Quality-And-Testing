package com.rosewood.payroll;

public class Employee {

    private final String id;
    private final String name;

    public Employee(String id, String name) {
        this.id = requireText(id, "Employee ID must not be blank");
        this.name = requireText(name, "Employee name must not be blank");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
