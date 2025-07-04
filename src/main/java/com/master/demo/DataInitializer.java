package com.master.demo;

import com.master.demo.entity.Employee;
import com.master.demo.entity.EmployeeRepository;
import com.master.demo.entity.Product;
import com.master.demo.entity.ProductRepository;
import com.master.demo.entity.OrderEntity;
import com.master.demo.entity.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(
            EmployeeRepository employeeRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // ✅ Seed employees
            if (employeeRepository.findAll().stream().noneMatch(e -> e.getName().equalsIgnoreCase("rohan"))) {
                employeeRepository.save(new Employee("rohan", "HR", encoder.encode("rohan123"), "admin"));
            }
            if (employeeRepository.findAll().stream().noneMatch(e -> e.getName().equalsIgnoreCase("bob"))) {
                employeeRepository.save(new Employee("bob", "Sales", encoder.encode("bob123"), "vendor"));
            }
            if (employeeRepository.findAll().stream().noneMatch(e -> e.getName().equalsIgnoreCase("charlie"))) {
                employeeRepository.save(new Employee("charlie", "Support", encoder.encode("charlie123"), "customer"));
            }

            System.out.println("✅ Demo users created");

            // ✅ Seed products
            if (productRepository.count() == 0) {
                productRepository.save(new Product("Laptop", 1000.0, 10));
                productRepository.save(new Product("Smartphone", 600.0, 20));
                productRepository.save(new Product("Headphones", 150.0, 50));
                System.out.println("✅ Demo products created");
            }

            // ✅ Get employees
            Employee charlie = employeeRepository.findAll().stream()
                    .filter(e -> e.getName().equalsIgnoreCase("charlie"))
                    .findFirst()
                    .orElseThrow();

            Employee rohan = employeeRepository.findAll().stream()
                    .filter(e -> e.getName().equalsIgnoreCase("rohan"))
                    .findFirst()
                    .orElseThrow();

            Employee bob = employeeRepository.findAll().stream()
                    .filter(e -> e.getName().equalsIgnoreCase("bob"))
                    .findFirst()
                    .orElseThrow();

            // ✅ Seed orders
            if (orderRepository.count() == 0) {
                // Orders for charlie (customer)
                orderRepository.save(new OrderEntity(List.of(1L, 2L), 1600.0, charlie.getId()));
                orderRepository.save(new OrderEntity(List.of(3L), 150.0, charlie.getId()));

                // Order for rohan (admin)
                orderRepository.save(new OrderEntity(List.of(2L), 600.0, rohan.getId()));

                // Order for bob (vendor)
                orderRepository.save(new OrderEntity(List.of(1L, 3L), 1150.0, bob.getId()));

                System.out.println("✅ Demo orders created for charlie, rohan, and bob");
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
