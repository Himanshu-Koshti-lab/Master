package com.master.demo;

import com.master.demo.entity.Employee;
import com.master.demo.entity.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner addUsers(EmployeeRepository employeeRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (employeeRepository.findAll().stream().noneMatch(e -> e.getName().equalsIgnoreCase("rohan"))) {
                Employee alice = new Employee(
                        "rohan",
                        "HR",
                        encoder.encode("rohan123"),
                        "admin"
                );
                employeeRepository.save(alice);
                System.out.println("✅ User 'rohan' created (role: admin)");
            }

            if (employeeRepository.findAll().stream().noneMatch(e -> e.getName().equalsIgnoreCase("bob"))) {
                Employee bob = new Employee(
                        "bob",
                        "Sales",
                        encoder.encode("bob123"),
                        "vendor"
                );
                employeeRepository.save(bob);
                System.out.println("✅ User 'bob' created (role: vendor)");
            }

            if (employeeRepository.findAll().stream().noneMatch(e -> e.getName().equalsIgnoreCase("charlie"))) {
                Employee charlie = new Employee(
                        "charlie",
                        "Support",
                        encoder.encode("charlie123"),
                        "customer"
                );
                employeeRepository.save(charlie);
                System.out.println("✅ User 'charlie' created (role: customer)");
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
