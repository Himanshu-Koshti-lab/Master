package com.master.demo.service;

import com.master.demo.entity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;

    public Cart getCartByCustomer(String customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customerId);
                    return cartRepository.save(newCart);
                });
    }

    public Cart addToCart(String customerId, CartItemRequest request) {
        Cart cart = getCartByCustomer(customerId);

        // Check if product already in cart
        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(request.getProductId())) {
                item.setQuantity(item.getQuantity() + request.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public OrderEntity checkout(String customerId) {
        Cart cart = getCartByCustomer(customerId);
        Employee employee = employeeRepository.findByName(customerId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total
        double total = 0.0;
        List<Long> productIds = new java.util.ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            total += product.getPrice() * item.getQuantity();
            productIds.add(product.getId());
        }

        // Create new order
        OrderEntity order = new OrderEntity();
        order.setCustomerId(employee.getId());
        order.setProductIds(productIds);
        order.setTotalAmount(total);
        OrderEntity savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    @Transactional
    public Cart removeFromCart(String customerId, Long itemId) {
        Cart cart = getCartByCustomer(customerId);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new RuntimeException("Cart item not found for id: " + itemId);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public void updateQuantity(String customerId, Long itemId, int quantity) {
        Cart cart = getCartByCustomer(customerId);
        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(itemId)) {
                if (quantity <= 0) {
                    cart.getItems().remove(item);
                } else {
                    item.setQuantity(quantity);
                }
                break;
            }
        }
        cartRepository.save(cart);
    }

}
