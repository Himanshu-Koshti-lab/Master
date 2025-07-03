package com.master.demo.service;

import com.master.demo.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderEntity> getOrdersByCustomer(String username) {
        Employee customer = getCustomerByUsername(username);
        return orderRepository.findByCustomerId(customer.getId());
    }

    public OrderEntity placeOrder(OrderEntity order, String username) {
        Employee customer = getCustomerByUsername(username);
        order.setCustomerId(customer.getId());

        // Example: reduce count for each product
        for (Long productId : order.getProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            if (product.getCount() <= 0) {
                throw new RuntimeException("Product out of stock: " + product.getName());
            }
            product.setCount(product.getCount() - 1);
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }


    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private Employee getCustomerByUsername(String username) {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getName().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
