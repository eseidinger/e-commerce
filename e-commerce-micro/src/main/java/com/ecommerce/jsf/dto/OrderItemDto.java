package com.ecommerce.jsf.dto;

public record OrderItemDto(Long orderItemId, Long orderId, Long productId, int quantity) {}
