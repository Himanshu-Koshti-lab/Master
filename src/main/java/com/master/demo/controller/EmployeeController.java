package com.master.demo.controller;

import com.master.demo.entity.Employee;
import com.master.demo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        log.info("Received request to get employee with ID: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        log.info("Returning employee: {}", employee.getName());
        return employee;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        log.info("Received request to get all employees");
        List<Employee> employees = employeeService.getAllEmployees();
        log.info("Returning {} employees", employees.size());
        return employees;
    }
}
