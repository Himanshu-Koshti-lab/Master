package com.master.demo.service;

import com.master.demo.entity.Employee;
import com.master.demo.entity.EmployeeRepository;
import com.master.demo.handler.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee getEmployeeById(Long id) {
        log.info("Fetching employee by ID: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });
    }

    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        List<Employee> employees = employeeRepository.findAll();
        log.info("Found {} employees", employees.size());
        return employees;
    }
}

