package com.master.demo.controller;

import com.master.demo.entity.OrderEntity;
import com.master.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Get all orders — any authenticated user.
     */
    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Get only logged-in customer's orders.
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public List<OrderEntity> getMyOrders(Authentication auth) {
        return orderService.getOrdersByCustomer(auth.getName());
    }

    /**
     * Place new order — only 'CUSTOMER' role.
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderEntity order, Authentication auth) {
        OrderEntity saved = orderService.placeOrder(order, auth.getName());
        return ResponseEntity.ok(saved);
    }

    /**
     * Optional: Delete order — only 'ADMIN' role.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
