package com.master.demo.entity;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private int quantity;
}