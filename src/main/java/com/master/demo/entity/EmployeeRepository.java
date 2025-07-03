package com.master.demo.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // No need to write anything! CRUD methods come built-in.
}
