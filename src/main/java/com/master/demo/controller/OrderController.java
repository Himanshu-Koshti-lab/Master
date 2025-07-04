package com.master.demo.controller;

import com.master.demo.entity.OrderEntity;
import com.master.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Add this
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j // Add Lombok logger
public class OrderController {

    private final OrderService orderService;

    /**
     * Get all orders — any authenticated user.
     */
    @GetMapping
    public List<OrderEntity> getAllOrders() {
        log.info("Fetching all orders");
        List<OrderEntity> orders = orderService.getAllOrders();
        log.info("Total orders fetched: {}", orders.size());
        return orders;
    }

    /**
     * Get only logged-in customer's orders.
     */
//    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public List<OrderEntity> getMyOrders(Authentication auth) {
        String username = auth.getName();
        log.info("Fetching orders for customer: {}", username);
        List<OrderEntity> myOrders = orderService.getOrdersByCustomer(username);
        log.info("Orders fetched for {}: {}", username, myOrders.size());
        return myOrders;
    }

    /**
     * Place new order — only 'CUSTOMER' role.
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderEntity order, Authentication auth) {
        String username = auth.getName();
        log.info("Placing new order for customer: {}", username);
        log.debug("Order request payload: {}", order);
        OrderEntity saved = orderService.placeOrder(order, username);
        log.info("Order placed successfully with ID: {}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    /**
     * Optional: Delete order — only 'ADMIN' role.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        log.warn("Deleting order with ID: {}", id);
        orderService.deleteOrder(id);
        log.info("Order with ID {} deleted successfully", id);
        return ResponseEntity.ok().build();
    }
}
