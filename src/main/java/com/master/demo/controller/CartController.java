package com.master.demo.controller;

import com.master.demo.entity.Cart;
import com.master.demo.entity.CartItemRequest;
import com.master.demo.entity.OrderEntity;
import com.master.demo.service.CartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Cart getCart(Authentication auth) {
        return cartService.getCartByCustomer(auth.getName());
    }

    @PostMapping("/add")
    public Cart addToCart(Authentication auth, @RequestBody CartItemRequest request) {
        return cartService.addToCart(auth.getName(), request);
    }

    @PostMapping("/checkout")
    public OrderEntity checkout(Authentication auth) {
        return cartService.checkout(auth.getName());
    }

    @PostMapping("/remove")
    public void removeFromCart(Authentication auth, @RequestBody Map<String, Long> body) {
        Long itemId = body.get("itemId");
        cartService.removeFromCart(auth.getName(), itemId);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCartItem(
            @RequestParam String customerId,
            @RequestParam Long itemId,
            @RequestParam int quantity
    ) {
        cartService.updateQuantity(customerId, itemId, quantity);
        return ResponseEntity.ok().build();
    }
}
